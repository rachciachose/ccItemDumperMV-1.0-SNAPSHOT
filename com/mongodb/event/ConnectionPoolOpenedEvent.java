// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.connection.ServerId;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.annotations.Beta;

@Beta
public class ConnectionPoolOpenedEvent extends ConnectionPoolEvent
{
    private final ConnectionPoolSettings settings;
    
    public ConnectionPoolOpenedEvent(final ServerId serverId, final ConnectionPoolSettings settings) {
        super(serverId);
        this.settings = settings;
    }
    
    public ConnectionPoolSettings getSettings() {
        return this.settings;
    }
}
