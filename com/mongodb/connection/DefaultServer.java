// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.util.Iterator;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.MongoException;
import com.mongodb.MongoNodeIsRecoveringException;
import com.mongodb.MongoNotPrimaryException;
import com.mongodb.MongoSocketReadTimeoutException;
import com.mongodb.MongoSocketException;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.MongoSecurityException;
import com.mongodb.assertions.Assertions;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import com.mongodb.event.CommandListener;
import java.util.Set;
import com.mongodb.ServerAddress;

class DefaultServer implements ClusterableServer
{
    private final ServerAddress serverAddress;
    private final ConnectionPool connectionPool;
    private final ClusterConnectionMode clusterConnectionMode;
    private final ConnectionFactory connectionFactory;
    private final ServerMonitor serverMonitor;
    private final Set<ChangeListener<ServerDescription>> changeListeners;
    private final ChangeListener<ServerDescription> serverStateListener;
    private final CommandListener commandListener;
    private volatile ServerDescription description;
    private volatile boolean isClosed;
    
    public DefaultServer(final ServerAddress serverAddress, final ClusterConnectionMode clusterConnectionMode, final ConnectionPool connectionPool, final ConnectionFactory connectionFactory, final ServerMonitorFactory serverMonitorFactory, final CommandListener commandListener) {
        this.changeListeners = Collections.newSetFromMap(new ConcurrentHashMap<ChangeListener<ServerDescription>, Boolean>());
        this.commandListener = commandListener;
        Assertions.notNull("serverMonitorFactory", serverMonitorFactory);
        this.clusterConnectionMode = Assertions.notNull("clusterConnectionMode", clusterConnectionMode);
        this.connectionFactory = Assertions.notNull("connectionFactory", connectionFactory);
        this.serverAddress = Assertions.notNull("serverAddress", serverAddress);
        this.connectionPool = Assertions.notNull("connectionPool", connectionPool);
        this.serverStateListener = new DefaultServerStateListener();
        this.description = ServerDescription.builder().state(ServerConnectionState.CONNECTING).address(serverAddress).build();
        (this.serverMonitor = serverMonitorFactory.create(this.serverStateListener)).start();
    }
    
    @Override
    public Connection getConnection() {
        Assertions.isTrue("open", !this.isClosed());
        try {
            return this.connectionFactory.create(this.connectionPool.get(), new DefaultServerProtocolExecutor(), this.clusterConnectionMode);
        }
        catch (MongoSecurityException e) {
            this.invalidate();
            throw e;
        }
    }
    
    @Override
    public void getConnectionAsync(final SingleResultCallback<AsyncConnection> callback) {
        Assertions.isTrue("open", !this.isClosed());
        this.connectionPool.getAsync(new SingleResultCallback<InternalConnection>() {
            @Override
            public void onResult(final InternalConnection result, final Throwable t) {
                if (t instanceof MongoSecurityException) {
                    DefaultServer.this.invalidate();
                }
                if (t != null) {
                    callback.onResult(null, t);
                }
                else {
                    callback.onResult(DefaultServer.this.connectionFactory.createAsync(result, new DefaultServerProtocolExecutor(), DefaultServer.this.clusterConnectionMode), null);
                }
            }
        });
    }
    
    @Override
    public ServerDescription getDescription() {
        Assertions.isTrue("open", !this.isClosed());
        return this.description;
    }
    
    @Override
    public void addChangeListener(final ChangeListener<ServerDescription> changeListener) {
        Assertions.isTrue("open", !this.isClosed());
        this.changeListeners.add(changeListener);
    }
    
    @Override
    public void invalidate() {
        Assertions.isTrue("open", !this.isClosed());
        this.serverStateListener.stateChanged(new ChangeEvent<ServerDescription>(this.description, ServerDescription.builder().state(ServerConnectionState.CONNECTING).address(this.serverAddress).build()));
        this.connectionPool.invalidate();
        this.serverMonitor.invalidate();
    }
    
    @Override
    public void close() {
        if (!this.isClosed()) {
            this.connectionPool.close();
            this.serverMonitor.close();
            this.isClosed = true;
        }
    }
    
    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
    
    @Override
    public void connect() {
        this.serverMonitor.connect();
    }
    
    ConnectionPool getConnectionPool() {
        return this.connectionPool;
    }
    
    private void handleThrowable(final Throwable t) {
        if ((t instanceof MongoSocketException && !(t instanceof MongoSocketReadTimeoutException)) || t instanceof MongoNotPrimaryException || t instanceof MongoNodeIsRecoveringException) {
            this.invalidate();
        }
    }
    
    private class DefaultServerProtocolExecutor implements ProtocolExecutor
    {
        @Override
        public <T> T execute(final Protocol<T> protocol, final InternalConnection connection) {
            try {
                protocol.setCommandListener(DefaultServer.this.commandListener);
                return protocol.execute(connection);
            }
            catch (MongoException e) {
                DefaultServer.this.handleThrowable(e);
                throw e;
            }
        }
        
        @Override
        public <T> void executeAsync(final Protocol<T> protocol, final InternalConnection connection, final SingleResultCallback<T> callback) {
            protocol.executeAsync(connection, ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)new SingleResultCallback<T>() {
                @Override
                public void onResult(final T result, final Throwable t) {
                    if (t != null) {
                        DefaultServer.this.handleThrowable(t);
                    }
                    callback.onResult(result, t);
                }
            }));
        }
    }
    
    private final class DefaultServerStateListener implements ChangeListener<ServerDescription>
    {
        @Override
        public void stateChanged(final ChangeEvent<ServerDescription> event) {
            DefaultServer.this.description = event.getNewValue();
            for (final ChangeListener<ServerDescription> listener : DefaultServer.this.changeListeners) {
                listener.stateChanged(event);
            }
        }
    }
}
