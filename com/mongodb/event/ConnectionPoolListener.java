// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.annotations.Beta;
import java.util.EventListener;

@Beta
public interface ConnectionPoolListener extends EventListener
{
    void connectionPoolOpened(final ConnectionPoolOpenedEvent p0);
    
    void connectionPoolClosed(final ConnectionPoolEvent p0);
    
    void connectionCheckedOut(final ConnectionEvent p0);
    
    void connectionCheckedIn(final ConnectionEvent p0);
    
    void waitQueueEntered(final ConnectionPoolWaitQueueEvent p0);
    
    void waitQueueExited(final ConnectionPoolWaitQueueEvent p0);
    
    void connectionAdded(final ConnectionEvent p0);
    
    void connectionRemoved(final ConnectionEvent p0);
}
