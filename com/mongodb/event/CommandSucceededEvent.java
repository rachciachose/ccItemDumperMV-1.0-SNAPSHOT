// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import java.util.concurrent.TimeUnit;
import com.mongodb.connection.ConnectionDescription;
import org.bson.BsonDocument;

public final class CommandSucceededEvent extends CommandEvent
{
    private final BsonDocument response;
    private final long elapsedTimeNanos;
    
    public CommandSucceededEvent(final int requestId, final ConnectionDescription connectionDescription, final String commandName, final BsonDocument response, final long elapsedTimeNanos) {
        super(requestId, connectionDescription, commandName);
        this.response = response;
        this.elapsedTimeNanos = elapsedTimeNanos;
    }
    
    public long getElapsedTime(final TimeUnit timeUnit) {
        return timeUnit.convert(this.elapsedTimeNanos, TimeUnit.NANOSECONDS);
    }
    
    public BsonDocument getResponse() {
        return this.response;
    }
}
