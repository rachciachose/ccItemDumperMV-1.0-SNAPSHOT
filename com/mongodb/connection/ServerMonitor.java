// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

interface ServerMonitor
{
    void start();
    
    void connect();
    
    void invalidate();
    
    void close();
}
