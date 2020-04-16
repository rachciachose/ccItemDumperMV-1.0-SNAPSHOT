// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

class DefaultConnectionFactory implements ConnectionFactory
{
    @Override
    public Connection create(final InternalConnection internalConnection, final ProtocolExecutor executor, final ClusterConnectionMode clusterConnectionMode) {
        return new DefaultServerConnection(internalConnection, executor, clusterConnectionMode);
    }
    
    @Override
    public AsyncConnection createAsync(final InternalConnection internalConnection, final ProtocolExecutor executor, final ClusterConnectionMode clusterConnectionMode) {
        return new DefaultServerConnection(internalConnection, executor, clusterConnectionMode);
    }
}
