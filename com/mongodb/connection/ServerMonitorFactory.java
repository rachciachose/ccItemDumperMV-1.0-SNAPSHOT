// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

interface ServerMonitorFactory
{
    ServerMonitor create(final ChangeListener<ServerDescription> p0);
}
