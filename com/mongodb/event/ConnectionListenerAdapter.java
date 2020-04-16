// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.annotations.Beta;

@Beta
public abstract class ConnectionListenerAdapter implements ConnectionListener
{
    @Override
    public void connectionOpened(final ConnectionEvent event) {
    }
    
    @Override
    public void connectionClosed(final ConnectionEvent event) {
    }
    
    @Override
    public void messagesSent(final ConnectionMessagesSentEvent event) {
    }
    
    @Override
    public void messageReceived(final ConnectionMessageReceivedEvent event) {
    }
}
