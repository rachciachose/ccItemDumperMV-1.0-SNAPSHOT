// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.annotations.Beta;
import java.util.EventListener;

@Beta
public interface ConnectionListener extends EventListener
{
    void connectionOpened(final ConnectionEvent p0);
    
    void connectionClosed(final ConnectionEvent p0);
    
    void messagesSent(final ConnectionMessagesSentEvent p0);
    
    void messageReceived(final ConnectionMessageReceivedEvent p0);
}
