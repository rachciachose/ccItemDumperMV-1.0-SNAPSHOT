// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

interface ClusterableServer extends Server
{
    void addChangeListener(final ChangeListener<ServerDescription> p0);
    
    void invalidate();
    
    void close();
    
    boolean isClosed();
    
    void connect();
}
