// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.connection.ServerId;
import com.mongodb.annotations.Beta;

@Beta
public class ConnectionPoolEvent extends ClusterEvent
{
    private final ServerId serverId;
    
    public ConnectionPoolEvent(final ServerId serverId) {
        super(serverId.getClusterId());
        this.serverId = serverId;
    }
    
    public ServerId getServerId() {
        return this.serverId;
    }
}
