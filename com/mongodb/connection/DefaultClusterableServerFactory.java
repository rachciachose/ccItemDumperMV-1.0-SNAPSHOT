// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.ServerAddress;
import com.mongodb.event.CommandListener;
import com.mongodb.event.ConnectionListener;
import com.mongodb.event.ConnectionPoolListener;
import com.mongodb.MongoCredential;
import java.util.List;

class DefaultClusterableServerFactory implements ClusterableServerFactory
{
    private final ClusterId clusterId;
    private final ClusterSettings clusterSettings;
    private final ServerSettings settings;
    private final ConnectionPoolSettings connectionPoolSettings;
    private final StreamFactory streamFactory;
    private final List<MongoCredential> credentialList;
    private final ConnectionPoolListener connectionPoolListener;
    private final ConnectionListener connectionListener;
    private final StreamFactory heartbeatStreamFactory;
    private final CommandListener commandListener;
    
    public DefaultClusterableServerFactory(final ClusterId clusterId, final ClusterSettings clusterSettings, final ServerSettings settings, final ConnectionPoolSettings connectionPoolSettings, final StreamFactory streamFactory, final StreamFactory heartbeatStreamFactory, final List<MongoCredential> credentialList, final ConnectionListener connectionListener, final ConnectionPoolListener connectionPoolListener, final CommandListener commandListener) {
        this.clusterId = clusterId;
        this.clusterSettings = clusterSettings;
        this.settings = settings;
        this.connectionPoolSettings = connectionPoolSettings;
        this.streamFactory = streamFactory;
        this.credentialList = credentialList;
        this.connectionPoolListener = connectionPoolListener;
        this.connectionListener = connectionListener;
        this.heartbeatStreamFactory = heartbeatStreamFactory;
        this.commandListener = commandListener;
    }
    
    @Override
    public ClusterableServer create(final ServerAddress serverAddress) {
        final ConnectionPool connectionPool = new DefaultConnectionPool(new ServerId(this.clusterId, serverAddress), new InternalStreamConnectionFactory(this.streamFactory, this.credentialList, this.connectionListener), this.connectionPoolSettings, this.connectionPoolListener);
        final ServerMonitorFactory serverMonitorFactory = new DefaultServerMonitorFactory(new ServerId(this.clusterId, serverAddress), this.settings, new InternalStreamConnectionFactory(this.heartbeatStreamFactory, this.credentialList, this.connectionListener), connectionPool);
        return new DefaultServer(serverAddress, this.clusterSettings.getMode(), connectionPool, new DefaultConnectionFactory(), serverMonitorFactory, this.commandListener);
    }
    
    @Override
    public ServerSettings getSettings() {
        return this.settings;
    }
}
