// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.connection.ConnectionId;
import com.mongodb.annotations.Beta;

@Beta
public class ConnectionMessagesSentEvent extends ConnectionEvent
{
    private final int requestId;
    private final int size;
    
    public ConnectionMessagesSentEvent(final ConnectionId connectionId, final int requestId, final int size) {
        super(connectionId);
        this.requestId = requestId;
        this.size = size;
    }
    
    public int getRequestId() {
        return this.requestId;
    }
    
    public int getSize() {
        return this.size;
    }
}
