// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.connection.ConnectionId;
import com.mongodb.annotations.Beta;

@Beta
public class ConnectionMessageReceivedEvent extends ConnectionEvent
{
    private final int responseTo;
    private final int size;
    
    public ConnectionMessageReceivedEvent(final ConnectionId connectionId, final int responseTo, final int size) {
        super(connectionId);
        this.responseTo = responseTo;
        this.size = size;
    }
    
    public int getResponseTo() {
        return this.responseTo;
    }
    
    public int getSize() {
        return this.size;
    }
}
