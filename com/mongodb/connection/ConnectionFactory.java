// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

interface ConnectionFactory
{
    Connection create(final InternalConnection p0, final ProtocolExecutor p1, final ClusterConnectionMode p2);
    
    AsyncConnection createAsync(final InternalConnection p0, final ProtocolExecutor p1, final ClusterConnectionMode p2);
}
