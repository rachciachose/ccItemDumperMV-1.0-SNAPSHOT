// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

public interface CommandListener
{
    void commandStarted(final CommandStartedEvent p0);
    
    void commandSucceeded(final CommandSucceededEvent p0);
    
    void commandFailed(final CommandFailedEvent p0);
}
