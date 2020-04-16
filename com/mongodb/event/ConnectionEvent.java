// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.connection.ConnectionId;
import com.mongodb.annotations.Beta;

@Beta
public class ConnectionEvent extends ClusterEvent
{
    private final ConnectionId connectionId;
    
    public ConnectionEvent(final ConnectionId connectionId) {
        super(connectionId.getServerId().getClusterId());
        this.connectionId = connectionId;
    }
    
    public ConnectionId getConnectionId() {
        return this.connectionId;
    }
}
