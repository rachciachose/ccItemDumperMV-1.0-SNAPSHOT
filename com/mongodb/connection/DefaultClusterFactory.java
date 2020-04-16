// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.event.CommandListener;
import com.mongodb.event.ConnectionListener;
import com.mongodb.event.ConnectionPoolListener;
import com.mongodb.event.ClusterListener;
import com.mongodb.MongoCredential;
import java.util.List;

public final class DefaultClusterFactory implements ClusterFactory
{
    @Override
    public Cluster create(final ClusterSettings settings, final ServerSettings serverSettings, final ConnectionPoolSettings connectionPoolSettings, final StreamFactory streamFactory, final StreamFactory heartbeatStreamFactory, final List<MongoCredential> credentialList, final ClusterListener clusterListener, final ConnectionPoolListener connectionPoolListener, final ConnectionListener connectionListener) {
        return this.create(settings, serverSettings, connectionPoolSettings, streamFactory, heartbeatStreamFactory, credentialList, clusterListener, connectionPoolListener, connectionListener, null);
    }
    
    public Cluster create(final ClusterSettings settings, final ServerSettings serverSettings, final ConnectionPoolSettings connectionPoolSettings, final StreamFactory streamFactory, final StreamFactory heartbeatStreamFactory, final List<MongoCredential> credentialList, final ClusterListener clusterListener, final ConnectionPoolListener connectionPoolListener, final ConnectionListener connectionListener, final CommandListener commandListener) {
        final ClusterId clusterId = new ClusterId(settings.getDescription());
        final ClusterableServerFactory serverFactory = new DefaultClusterableServerFactory(clusterId, settings, serverSettings, connectionPoolSettings, streamFactory, heartbeatStreamFactory, credentialList, (connectionListener != null) ? connectionListener : new NoOpConnectionListener(), (connectionPoolListener != null) ? connectionPoolListener : new NoOpConnectionPoolListener(), commandListener);
        if (settings.getMode() == ClusterConnectionMode.SINGLE) {
            return new SingleServerCluster(clusterId, settings, serverFactory, (clusterListener != null) ? clusterListener : new NoOpClusterListener());
        }
        if (settings.getMode() == ClusterConnectionMode.MULTIPLE) {
            return new MultiServerCluster(clusterId, settings, serverFactory, (clusterListener != null) ? clusterListener : new NoOpClusterListener());
        }
        throw new UnsupportedOperationException("Unsupported cluster mode: " + settings.getMode());
    }
}
