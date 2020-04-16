// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import java.util.concurrent.TimeUnit;
import com.mongodb.connection.ConnectionDescription;

public final class CommandFailedEvent extends CommandEvent
{
    private final long elapsedTimeNanos;
    private final Throwable throwable;
    
    public CommandFailedEvent(final int requestId, final ConnectionDescription connectionDescription, final String commandName, final long elapsedTimeNanos, final Throwable throwable) {
        super(requestId, connectionDescription, commandName);
        this.elapsedTimeNanos = elapsedTimeNanos;
        this.throwable = throwable;
    }
    
    public long getElapsedTime(final TimeUnit timeUnit) {
        return timeUnit.convert(this.elapsedTimeNanos, TimeUnit.NANOSECONDS);
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
}
