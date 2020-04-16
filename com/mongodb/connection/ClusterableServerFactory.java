// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.ServerAddress;

interface ClusterableServerFactory
{
    ClusterableServer create(final ServerAddress p0);
    
    ServerSettings getSettings();
}
