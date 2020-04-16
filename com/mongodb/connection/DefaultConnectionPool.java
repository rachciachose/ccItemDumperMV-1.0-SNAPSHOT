// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.util.List;
import org.bson.ByteBuf;
import com.mongodb.diagnostics.logging.Loggers;
import com.mongodb.MongoSocketReadTimeoutException;
import com.mongodb.MongoSocketException;
import java.util.concurrent.ScheduledExecutorService;
import com.mongodb.MongoWaitQueueFullException;
import com.mongodb.event.ConnectionEvent;
import com.mongodb.event.ConnectionPoolEvent;
import java.util.concurrent.Executors;
import com.mongodb.MongoTimeoutException;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.MongoInternalException;
import com.mongodb.MongoException;
import com.mongodb.event.ConnectionPoolWaitQueueEvent;
import java.util.concurrent.TimeUnit;
import com.mongodb.event.ConnectionPoolOpenedEvent;
import com.mongodb.assertions.Assertions;
import com.mongodb.event.ConnectionPoolListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import com.mongodb.internal.connection.ConcurrentPool;
import com.mongodb.diagnostics.logging.Logger;

class DefaultConnectionPool implements ConnectionPool
{
    private static final Logger LOGGER;
    private final ConcurrentPool<UsageTrackingInternalConnection> pool;
    private final ConnectionPoolSettings settings;
    private final AtomicInteger waitQueueSize;
    private final AtomicInteger generation;
    private final ExecutorService sizeMaintenanceTimer;
    private ExecutorService asyncGetter;
    private final Runnable maintenanceTask;
    private final ConnectionPoolListener connectionPoolListener;
    private final ServerId serverId;
    private volatile boolean closed;
    
    public DefaultConnectionPool(final ServerId serverId, final InternalConnectionFactory internalConnectionFactory, final ConnectionPoolSettings settings, final ConnectionPoolListener connectionPoolListener) {
        this.waitQueueSize = new AtomicInteger(0);
        this.generation = new AtomicInteger(0);
        this.serverId = Assertions.notNull("serverId", serverId);
        this.settings = Assertions.notNull("settings", settings);
        final UsageTrackingInternalConnectionItemFactory connectionItemFactory = new UsageTrackingInternalConnectionItemFactory(internalConnectionFactory);
        this.pool = new ConcurrentPool<UsageTrackingInternalConnection>(settings.getMaxSize(), connectionItemFactory);
        this.maintenanceTask = this.createMaintenanceTask();
        this.sizeMaintenanceTimer = this.createTimer();
        this.connectionPoolListener = Assertions.notNull("connectionPoolListener", connectionPoolListener);
        connectionPoolListener.connectionPoolOpened(new ConnectionPoolOpenedEvent(serverId, settings));
    }
    
    @Override
    public InternalConnection get() {
        return this.get(this.settings.getMaxWaitTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }
    
    @Override
    public InternalConnection get(final long timeout, final TimeUnit timeUnit) {
        try {
            if (this.waitQueueSize.incrementAndGet() > this.settings.getMaxWaitQueueSize()) {
                throw this.createWaitQueueFullException();
            }
            try {
                this.connectionPoolListener.waitQueueEntered(new ConnectionPoolWaitQueueEvent(this.serverId, Thread.currentThread().getId()));
                final PooledConnection pooledConnection = this.getPooledConnection(timeout, timeUnit);
                if (!pooledConnection.opened()) {
                    try {
                        pooledConnection.open();
                    }
                    catch (Throwable t) {
                        this.pool.release(pooledConnection.wrapped, true);
                        if (t instanceof MongoException) {
                            throw (MongoException)t;
                        }
                        throw new MongoInternalException(t.toString(), t);
                    }
                }
                return pooledConnection;
            }
            finally {
                this.connectionPoolListener.waitQueueExited(new ConnectionPoolWaitQueueEvent(this.serverId, Thread.currentThread().getId()));
            }
        }
        finally {
            this.waitQueueSize.decrementAndGet();
        }
    }
    
    @Override
    public void getAsync(final SingleResultCallback<InternalConnection> callback) {
        if (DefaultConnectionPool.LOGGER.isTraceEnabled()) {
            DefaultConnectionPool.LOGGER.trace(String.format("Asynchronously getting a connection from the pool for server %s", this.serverId));
        }
        final SingleResultCallback<InternalConnection> wrappedCallback = ErrorHandlingResultCallback.errorHandlingCallback(callback);
        PooledConnection connection = null;
        try {
            connection = this.getPooledConnection(0L, TimeUnit.MILLISECONDS);
        }
        catch (MongoTimeoutException ex) {}
        if (connection != null) {
            if (DefaultConnectionPool.LOGGER.isTraceEnabled()) {
                DefaultConnectionPool.LOGGER.trace(String.format("Asynchronously opening pooled connection %s to server %s", connection.getDescription().getConnectionId(), this.serverId));
            }
            this.openAsync(connection, wrappedCallback);
        }
        else if (this.waitQueueSize.incrementAndGet() > this.settings.getMaxWaitQueueSize()) {
            this.waitQueueSize.decrementAndGet();
            if (DefaultConnectionPool.LOGGER.isTraceEnabled()) {
                DefaultConnectionPool.LOGGER.trace(String.format("Asynchronously failing to get a pooled connection to %s because the wait queue is full", this.serverId));
            }
            callback.onResult(null, this.createWaitQueueFullException());
        }
        else {
            final long startTimeMillis = System.currentTimeMillis();
            this.connectionPoolListener.waitQueueEntered(new ConnectionPoolWaitQueueEvent(this.serverId, Thread.currentThread().getId()));
            this.getAsyncGetter().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (this.getRemainingWaitTime() <= 0L) {
                            wrappedCallback.onResult(null, DefaultConnectionPool.this.createTimeoutException());
                        }
                        else {
                            final PooledConnection connection = DefaultConnectionPool.this.getPooledConnection(this.getRemainingWaitTime(), TimeUnit.MILLISECONDS);
                            DefaultConnectionPool.this.openAsync(connection, wrappedCallback);
                        }
                    }
                    catch (Throwable t) {
                        wrappedCallback.onResult(null, t);
                    }
                    finally {
                        DefaultConnectionPool.this.waitQueueSize.decrementAndGet();
                        DefaultConnectionPool.this.connectionPoolListener.waitQueueExited(new ConnectionPoolWaitQueueEvent(DefaultConnectionPool.this.serverId, Thread.currentThread().getId()));
                    }
                }
                
                private long getRemainingWaitTime() {
                    return startTimeMillis + DefaultConnectionPool.this.settings.getMaxWaitTime(TimeUnit.MILLISECONDS) - System.currentTimeMillis();
                }
            });
        }
    }
    
    private void openAsync(final PooledConnection pooledConnection, final SingleResultCallback<InternalConnection> callback) {
        if (pooledConnection.opened()) {
            if (DefaultConnectionPool.LOGGER.isTraceEnabled()) {
                DefaultConnectionPool.LOGGER.trace(String.format("Pooled connection %s to server %s is already open", pooledConnection.getDescription().getConnectionId(), this.serverId));
            }
            callback.onResult(pooledConnection, null);
        }
        else {
            if (DefaultConnectionPool.LOGGER.isTraceEnabled()) {
                DefaultConnectionPool.LOGGER.trace(String.format("Pooled connection %s to server %s is not yet open", pooledConnection.getDescription().getConnectionId(), this.serverId));
            }
            pooledConnection.openAsync(new SingleResultCallback<Void>() {
                @Override
                public void onResult(final Void result, final Throwable t) {
                    if (t != null) {
                        if (DefaultConnectionPool.LOGGER.isTraceEnabled()) {
                            DefaultConnectionPool.LOGGER.trace(String.format("Pooled connection %s to server %s failed to open", pooledConnection.getDescription().getConnectionId(), DefaultConnectionPool.this.serverId));
                        }
                        callback.onResult(null, t);
                        DefaultConnectionPool.this.pool.release(pooledConnection.wrapped, true);
                    }
                    else {
                        if (DefaultConnectionPool.LOGGER.isTraceEnabled()) {
                            DefaultConnectionPool.LOGGER.trace(String.format("Pooled connection %s to server %s is now open", pooledConnection.getDescription().getConnectionId(), DefaultConnectionPool.this.serverId));
                        }
                        callback.onResult(pooledConnection, null);
                    }
                }
            });
        }
    }
    
    private synchronized ExecutorService getAsyncGetter() {
        if (this.asyncGetter == null) {
            this.asyncGetter = Executors.newSingleThreadExecutor();
        }
        return this.asyncGetter;
    }
    
    private synchronized void shutdownAsyncGetter() {
        if (this.asyncGetter != null) {
            this.asyncGetter.shutdownNow();
        }
    }
    
    @Override
    public void invalidate() {
        DefaultConnectionPool.LOGGER.debug("Invalidating the connection pool");
        this.generation.incrementAndGet();
    }
    
    @Override
    public void close() {
        if (!this.closed) {
            this.pool.close();
            if (this.sizeMaintenanceTimer != null) {
                this.sizeMaintenanceTimer.shutdownNow();
            }
            this.shutdownAsyncGetter();
            this.closed = true;
            this.connectionPoolListener.connectionPoolClosed(new ConnectionPoolEvent(this.serverId));
        }
    }
    
    public void doMaintenance() {
        if (this.maintenanceTask != null) {
            this.maintenanceTask.run();
        }
    }
    
    private PooledConnection getPooledConnection(final long timeout, final TimeUnit timeUnit) {
        UsageTrackingInternalConnection internalConnection;
        for (internalConnection = this.pool.get(timeout, timeUnit); this.shouldPrune(internalConnection); internalConnection = this.pool.get(timeout, timeUnit)) {
            this.pool.release(internalConnection, true);
        }
        this.connectionPoolListener.connectionCheckedOut(new ConnectionEvent(internalConnection.getDescription().getConnectionId()));
        if (DefaultConnectionPool.LOGGER.isTraceEnabled()) {
            DefaultConnectionPool.LOGGER.trace(String.format("Checked out connection [%s] to server %s", this.getId(internalConnection), this.serverId.getAddress()));
        }
        return new PooledConnection(internalConnection);
    }
    
    private MongoTimeoutException createTimeoutException() {
        return new MongoTimeoutException(String.format("Timed out after %d ms while waiting for a connection to server %s.", this.settings.getMaxWaitTime(TimeUnit.MILLISECONDS), this.serverId.getAddress()));
    }
    
    private MongoWaitQueueFullException createWaitQueueFullException() {
        return new MongoWaitQueueFullException(String.format("Too many threads are already waiting for a connection. Max number of threads (maxWaitQueueSize) of %d has been exceeded.", this.settings.getMaxWaitQueueSize()));
    }
    
    ConcurrentPool<UsageTrackingInternalConnection> getPool() {
        return this.pool;
    }
    
    private Runnable createMaintenanceTask() {
        Runnable newMaintenanceTask = null;
        if (this.shouldPrune() || this.shouldEnsureMinSize()) {
            newMaintenanceTask = new Runnable() {
                @Override
                public synchronized void run() {
                    if (DefaultConnectionPool.this.shouldPrune()) {
                        if (DefaultConnectionPool.LOGGER.isDebugEnabled()) {
                            DefaultConnectionPool.LOGGER.debug(String.format("Pruning pooled connections to %s", DefaultConnectionPool.this.serverId.getAddress()));
                        }
                        DefaultConnectionPool.this.pool.prune();
                    }
                    if (DefaultConnectionPool.this.shouldEnsureMinSize()) {
                        if (DefaultConnectionPool.LOGGER.isDebugEnabled()) {
                            DefaultConnectionPool.LOGGER.debug(String.format("Ensuring minimum pooled connections to %s", DefaultConnectionPool.this.serverId.getAddress()));
                        }
                        DefaultConnectionPool.this.pool.ensureMinSize(DefaultConnectionPool.this.settings.getMinSize());
                    }
                }
            };
        }
        return newMaintenanceTask;
    }
    
    private ExecutorService createTimer() {
        if (this.maintenanceTask == null) {
            return null;
        }
        final ScheduledExecutorService newTimer = Executors.newSingleThreadScheduledExecutor();
        newTimer.scheduleAtFixedRate(this.maintenanceTask, this.settings.getMaintenanceInitialDelay(TimeUnit.MILLISECONDS), this.settings.getMaintenanceFrequency(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        return newTimer;
    }
    
    private boolean shouldEnsureMinSize() {
        return this.settings.getMinSize() > 0;
    }
    
    private boolean shouldPrune() {
        return this.settings.getMaxConnectionIdleTime(TimeUnit.MILLISECONDS) > 0L || this.settings.getMaxConnectionLifeTime(TimeUnit.MILLISECONDS) > 0L;
    }
    
    private boolean shouldPrune(final UsageTrackingInternalConnection connection) {
        return this.fromPreviousGeneration(connection) || this.pastMaxLifeTime(connection) || this.pastMaxIdleTime(connection);
    }
    
    private boolean pastMaxIdleTime(final UsageTrackingInternalConnection connection) {
        return this.expired(connection.getLastUsedAt(), System.currentTimeMillis(), this.settings.getMaxConnectionIdleTime(TimeUnit.MILLISECONDS));
    }
    
    private boolean pastMaxLifeTime(final UsageTrackingInternalConnection connection) {
        return this.expired(connection.getOpenedAt(), System.currentTimeMillis(), this.settings.getMaxConnectionLifeTime(TimeUnit.MILLISECONDS));
    }
    
    private boolean fromPreviousGeneration(final UsageTrackingInternalConnection connection) {
        return this.generation.get() > connection.getGeneration();
    }
    
    private boolean expired(final long startTime, final long curTime, final long maxTime) {
        return maxTime != 0L && curTime - startTime > maxTime;
    }
    
    private void incrementGenerationOnSocketException(final InternalConnection connection, final Throwable t) {
        if (t instanceof MongoSocketException && !(t instanceof MongoSocketReadTimeoutException)) {
            if (DefaultConnectionPool.LOGGER.isWarnEnabled()) {
                DefaultConnectionPool.LOGGER.warn(String.format("Got socket exception on connection [%s] to %s. All connections to %s will be closed.", this.getId(connection), this.serverId.getAddress(), this.serverId.getAddress()));
            }
            this.invalidate();
        }
    }
    
    private ConnectionId getId(final InternalConnection internalConnection) {
        return internalConnection.getDescription().getConnectionId();
    }
    
    static {
        LOGGER = Loggers.getLogger("connection");
    }
    
    private class PooledConnection implements InternalConnection
    {
        private volatile UsageTrackingInternalConnection wrapped;
        
        public PooledConnection(final UsageTrackingInternalConnection wrapped) {
            this.wrapped = Assertions.notNull("wrapped", wrapped);
        }
        
        @Override
        public void open() {
            Assertions.isTrue("open", this.wrapped != null);
            this.wrapped.open();
        }
        
        @Override
        public void openAsync(final SingleResultCallback<Void> callback) {
            Assertions.isTrue("open", this.wrapped != null);
            this.wrapped.openAsync(callback);
        }
        
        @Override
        public void close() {
            if (this.wrapped != null) {
                if (!DefaultConnectionPool.this.closed) {
                    DefaultConnectionPool.this.connectionPoolListener.connectionCheckedIn(new ConnectionEvent(DefaultConnectionPool.this.getId(this.wrapped)));
                    if (DefaultConnectionPool.LOGGER.isTraceEnabled()) {
                        DefaultConnectionPool.LOGGER.trace(String.format("Checked in connection [%s] to server %s", DefaultConnectionPool.this.getId(this.wrapped), DefaultConnectionPool.this.serverId.getAddress()));
                    }
                }
                DefaultConnectionPool.this.pool.release(this.wrapped, this.wrapped.isClosed() || DefaultConnectionPool.this.shouldPrune(this.wrapped));
                this.wrapped = null;
            }
        }
        
        @Override
        public boolean opened() {
            Assertions.isTrue("open", this.wrapped != null);
            return this.wrapped.opened();
        }
        
        @Override
        public boolean isClosed() {
            return this.wrapped == null || this.wrapped.isClosed();
        }
        
        @Override
        public ByteBuf getBuffer(final int capacity) {
            return this.wrapped.getBuffer(capacity);
        }
        
        @Override
        public void sendMessage(final List<ByteBuf> byteBuffers, final int lastRequestId) {
            Assertions.isTrue("open", this.wrapped != null);
            try {
                this.wrapped.sendMessage(byteBuffers, lastRequestId);
            }
            catch (MongoException e) {
                DefaultConnectionPool.this.incrementGenerationOnSocketException(this, e);
                throw e;
            }
        }
        
        @Override
        public ResponseBuffers receiveMessage(final int responseTo) {
            Assertions.isTrue("open", this.wrapped != null);
            try {
                return this.wrapped.receiveMessage(responseTo);
            }
            catch (MongoException e) {
                DefaultConnectionPool.this.incrementGenerationOnSocketException(this, e);
                throw e;
            }
        }
        
        @Override
        public void sendMessageAsync(final List<ByteBuf> byteBuffers, final int lastRequestId, final SingleResultCallback<Void> callback) {
            Assertions.isTrue("open", this.wrapped != null);
            this.wrapped.sendMessageAsync(byteBuffers, lastRequestId, new SingleResultCallback<Void>() {
                @Override
                public void onResult(final Void result, final Throwable t) {
                    if (t != null) {
                        DefaultConnectionPool.this.incrementGenerationOnSocketException(PooledConnection.this, t);
                    }
                    callback.onResult(null, t);
                }
            });
        }
        
        @Override
        public void receiveMessageAsync(final int responseTo, final SingleResultCallback<ResponseBuffers> callback) {
            Assertions.isTrue("open", this.wrapped != null);
            this.wrapped.receiveMessageAsync(responseTo, new SingleResultCallback<ResponseBuffers>() {
                @Override
                public void onResult(final ResponseBuffers result, final Throwable t) {
                    if (t != null) {
                        DefaultConnectionPool.this.incrementGenerationOnSocketException(PooledConnection.this, t);
                    }
                    callback.onResult(result, t);
                }
            });
        }
        
        @Override
        public ConnectionDescription getDescription() {
            Assertions.isTrue("open", this.wrapped != null);
            return this.wrapped.getDescription();
        }
    }
    
    private class UsageTrackingInternalConnectionItemFactory implements ConcurrentPool.ItemFactory<UsageTrackingInternalConnection>
    {
        private final InternalConnectionFactory internalConnectionFactory;
        
        public UsageTrackingInternalConnectionItemFactory(final InternalConnectionFactory internalConnectionFactory) {
            this.internalConnectionFactory = internalConnectionFactory;
        }
        
        @Override
        public UsageTrackingInternalConnection create() {
            final UsageTrackingInternalConnection internalConnection = new UsageTrackingInternalConnection(this.internalConnectionFactory.create(DefaultConnectionPool.this.serverId), DefaultConnectionPool.this.generation.get());
            DefaultConnectionPool.this.connectionPoolListener.connectionAdded(new ConnectionEvent(DefaultConnectionPool.this.getId(internalConnection)));
            return internalConnection;
        }
        
        @Override
        public void close(final UsageTrackingInternalConnection connection) {
            if (!DefaultConnectionPool.this.closed) {
                DefaultConnectionPool.this.connectionPoolListener.connectionRemoved(new ConnectionEvent(DefaultConnectionPool.this.getId(connection)));
            }
            if (DefaultConnectionPool.LOGGER.isInfoEnabled()) {
                DefaultConnectionPool.LOGGER.info(String.format("Closed connection [%s] to %s because %s.", DefaultConnectionPool.this.getId(connection), DefaultConnectionPool.this.serverId.getAddress(), this.getReasonForClosing(connection)));
            }
            connection.close();
        }
        
        private String getReasonForClosing(final UsageTrackingInternalConnection connection) {
            String reason;
            if (connection.isClosed()) {
                reason = "there was a socket exception raised by this connection";
            }
            else if (DefaultConnectionPool.this.fromPreviousGeneration(connection)) {
                reason = "there was a socket exception raised on another connection from this pool";
            }
            else if (DefaultConnectionPool.this.pastMaxLifeTime(connection)) {
                reason = "it is past its maximum allowed life time";
            }
            else if (DefaultConnectionPool.this.pastMaxIdleTime(connection)) {
                reason = "it is past its maximum allowed idle time";
            }
            else {
                reason = "the pool has been closed";
            }
            return reason;
        }
        
        @Override
        public boolean shouldPrune(final UsageTrackingInternalConnection usageTrackingConnection) {
            return DefaultConnectionPool.this.shouldPrune(usageTrackingConnection);
        }
    }
}
