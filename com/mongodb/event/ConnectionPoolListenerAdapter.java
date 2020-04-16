// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.annotations.Beta;

@Beta
public abstract class ConnectionPoolListenerAdapter implements ConnectionPoolListener
{
    @Override
    public void connectionPoolOpened(final ConnectionPoolOpenedEvent event) {
    }
    
    @Override
    public void connectionPoolClosed(final ConnectionPoolEvent event) {
    }
    
    @Override
    public void connectionCheckedOut(final ConnectionEvent event) {
    }
    
    @Override
    public void connectionCheckedIn(final ConnectionEvent event) {
    }
    
    @Override
    public void waitQueueEntered(final ConnectionPoolWaitQueueEvent event) {
    }
    
    @Override
    public void waitQueueExited(final ConnectionPoolWaitQueueEvent event) {
    }
    
    @Override
    public void connectionAdded(final ConnectionEvent event) {
    }
    
    @Override
    public void connectionRemoved(final ConnectionEvent event) {
    }
}
