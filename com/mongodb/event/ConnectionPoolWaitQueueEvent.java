// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.connection.ServerId;
import com.mongodb.annotations.Beta;

@Beta
public class ConnectionPoolWaitQueueEvent extends ConnectionPoolEvent
{
    private final long threadId;
    
    public ConnectionPoolWaitQueueEvent(final ServerId serverId, final long threadId) {
        super(serverId);
        this.threadId = threadId;
    }
    
    public long getThreadId() {
        return this.threadId;
    }
}
