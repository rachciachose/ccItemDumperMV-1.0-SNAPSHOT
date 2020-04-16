// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.util.concurrent.TimeUnit;
import org.bson.BsonValue;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import com.mongodb.MongoSocketException;
import com.mongodb.diagnostics.logging.Loggers;
import com.mongodb.assertions.Assertions;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import com.mongodb.diagnostics.logging.Logger;
import com.mongodb.annotations.ThreadSafe;

@ThreadSafe
class DefaultServerMonitor implements ServerMonitor
{
    private static final Logger LOGGER;
    private final ServerId serverId;
    private final ChangeListener<ServerDescription> serverStateListener;
    private final InternalConnectionFactory internalConnectionFactory;
    private final ConnectionPool connectionPool;
    private final ServerSettings settings;
    private volatile ServerMonitorRunnable monitor;
    private volatile Thread monitorThread;
    private final Lock lock;
    private final Condition condition;
    private volatile boolean isClosed;
    
    DefaultServerMonitor(final ServerId serverId, final ServerSettings settings, final ChangeListener<ServerDescription> serverStateListener, final InternalConnectionFactory internalConnectionFactory, final ConnectionPool connectionPool) {
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        this.settings = settings;
        this.serverId = serverId;
        this.serverStateListener = serverStateListener;
        this.internalConnectionFactory = internalConnectionFactory;
        this.connectionPool = connectionPool;
        this.monitorThread = this.createMonitorThread();
        this.isClosed = false;
    }
    
    @Override
    public void start() {
        this.monitorThread.start();
    }
    
    @Override
    public void connect() {
        this.lock.lock();
        try {
            this.condition.signal();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void invalidate() {
        Assertions.isTrue("open", !this.isClosed);
        this.monitor.close();
        this.monitorThread.interrupt();
        (this.monitorThread = this.createMonitorThread()).start();
    }
    
    @Override
    public void close() {
        this.monitor.close();
        this.monitorThread.interrupt();
        this.isClosed = true;
    }
    
    Thread createMonitorThread() {
        this.monitor = new ServerMonitorRunnable();
        final Thread monitorThread = new Thread(this.monitor, "cluster-" + this.serverId.getClusterId() + "-" + this.serverId.getAddress());
        monitorThread.setDaemon(true);
        return monitorThread;
    }
    
    static boolean descriptionHasChanged(final ServerDescription previousServerDescription, final ServerDescription currentServerDescription) {
        return !previousServerDescription.equals(currentServerDescription);
    }
    
    static boolean stateHasChanged(final ServerDescription previousServerDescription, final ServerDescription currentServerDescription) {
        return descriptionHasChanged(previousServerDescription, currentServerDescription) || previousServerDescription.getRoundTripTimeNanos() != currentServerDescription.getRoundTripTimeNanos();
    }
    
    static boolean exceptionHasChanged(final Throwable previousException, final Throwable currentException) {
        if (currentException == null) {
            return previousException != null;
        }
        if (previousException == null) {
            return true;
        }
        if (!currentException.getClass().equals(previousException.getClass())) {
            return true;
        }
        if (currentException.getMessage() == null) {
            return previousException.getMessage() != null;
        }
        return !currentException.getMessage().equals(previousException.getMessage());
    }
    
    static {
        LOGGER = Loggers.getLogger("cluster");
    }
    
    class ServerMonitorRunnable implements Runnable
    {
        private volatile boolean monitorIsClosed;
        private final ExponentiallyWeightedMovingAverage averageRoundTripTime;
        
        ServerMonitorRunnable() {
            this.averageRoundTripTime = new ExponentiallyWeightedMovingAverage(0.2);
        }
        
        public void close() {
            this.monitorIsClosed = true;
        }
        
        @Override
        public synchronized void run() {
            InternalConnection connection = null;
            try {
                ServerDescription currentServerDescription = this.getConnectingServerDescription(null);
                Throwable currentException = null;
                while (!this.monitorIsClosed) {
                    final ServerDescription previousServerDescription = currentServerDescription;
                    final Throwable previousException = currentException;
                    try {
                        if (connection == null) {
                            connection = DefaultServerMonitor.this.internalConnectionFactory.create(DefaultServerMonitor.this.serverId);
                            try {
                                connection.open();
                            }
                            catch (Throwable t) {
                                connection = null;
                                throw t;
                            }
                        }
                        try {
                            currentServerDescription = this.lookupServerDescription(connection);
                        }
                        catch (MongoSocketException e2) {
                            DefaultServerMonitor.this.connectionPool.invalidate();
                            connection.close();
                            connection = null;
                            connection = DefaultServerMonitor.this.internalConnectionFactory.create(DefaultServerMonitor.this.serverId);
                            try {
                                connection.open();
                            }
                            catch (Throwable t2) {
                                connection = null;
                                throw t2;
                            }
                            try {
                                currentServerDescription = this.lookupServerDescription(connection);
                            }
                            catch (MongoSocketException e1) {
                                connection.close();
                                connection = null;
                                throw e1;
                            }
                        }
                    }
                    catch (Throwable t) {
                        this.averageRoundTripTime.reset();
                        currentException = t;
                        currentServerDescription = this.getConnectingServerDescription(t);
                    }
                    if (!this.monitorIsClosed) {
                        try {
                            this.logStateChange(previousServerDescription, previousException, currentServerDescription, currentException);
                            this.sendStateChangedEvent(previousServerDescription, currentServerDescription);
                        }
                        catch (Throwable t) {
                            DefaultServerMonitor.LOGGER.warn("Exception in monitor thread during notification of server description state change", t);
                        }
                        this.waitForNext();
                    }
                }
            }
            finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }
        
        private ServerDescription getConnectingServerDescription(final Throwable exception) {
            return ServerDescription.builder().type(ServerType.UNKNOWN).state(ServerConnectionState.CONNECTING).address(DefaultServerMonitor.this.serverId.getAddress()).exception(exception).build();
        }
        
        private ServerDescription lookupServerDescription(final InternalConnection connection) {
            if (DefaultServerMonitor.LOGGER.isDebugEnabled()) {
                DefaultServerMonitor.LOGGER.debug(String.format("Checking status of %s", DefaultServerMonitor.this.serverId.getAddress()));
            }
            final long start = System.nanoTime();
            final BsonDocument isMasterResult = CommandHelper.executeCommand("admin", new BsonDocument("ismaster", new BsonInt32(1)), connection);
            this.averageRoundTripTime.addSample(System.nanoTime() - start);
            return DescriptionHelper.createServerDescription(DefaultServerMonitor.this.serverId.getAddress(), isMasterResult, connection.getDescription().getServerVersion(), this.averageRoundTripTime.getAverage());
        }
        
        private void sendStateChangedEvent(final ServerDescription previousServerDescription, final ServerDescription currentServerDescription) {
            if (DefaultServerMonitor.stateHasChanged(previousServerDescription, currentServerDescription)) {
                DefaultServerMonitor.this.serverStateListener.stateChanged(new ChangeEvent<ServerDescription>(previousServerDescription, currentServerDescription));
            }
        }
        
        private void logStateChange(final ServerDescription previousServerDescription, final Throwable previousException, final ServerDescription currentServerDescription, final Throwable currentException) {
            if (DefaultServerMonitor.descriptionHasChanged(previousServerDescription, currentServerDescription) || DefaultServerMonitor.exceptionHasChanged(previousException, currentException)) {
                if (currentException != null) {
                    DefaultServerMonitor.LOGGER.info(String.format("Exception in monitor thread while connecting to server %s", DefaultServerMonitor.this.serverId.getAddress()), currentException);
                }
                else {
                    DefaultServerMonitor.LOGGER.info(String.format("Monitor thread successfully connected to server with description %s", currentServerDescription));
                }
            }
        }
        
        private void waitForNext() {
            try {
                final long timeRemaining = this.waitForSignalOrTimeout();
                if (timeRemaining > 0L) {
                    final long timeWaiting = DefaultServerMonitor.this.settings.getHeartbeatFrequency(TimeUnit.NANOSECONDS) - timeRemaining;
                    final long minimumNanosToWait = DefaultServerMonitor.this.settings.getMinHeartbeatFrequency(TimeUnit.NANOSECONDS);
                    if (timeWaiting < minimumNanosToWait) {
                        final long millisToSleep = TimeUnit.MILLISECONDS.convert(minimumNanosToWait - timeWaiting, TimeUnit.NANOSECONDS);
                        if (millisToSleep > 0L) {
                            Thread.sleep(millisToSleep);
                        }
                    }
                }
            }
            catch (InterruptedException ex) {}
        }
        
        private long waitForSignalOrTimeout() throws InterruptedException {
            DefaultServerMonitor.this.lock.lock();
            try {
                return DefaultServerMonitor.this.condition.awaitNanos(DefaultServerMonitor.this.settings.getHeartbeatFrequency(TimeUnit.NANOSECONDS));
            }
            finally {
                DefaultServerMonitor.this.lock.unlock();
            }
        }
    }
}
