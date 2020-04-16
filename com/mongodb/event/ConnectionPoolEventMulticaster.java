// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import com.mongodb.annotations.Beta;

@Beta
public class ConnectionPoolEventMulticaster implements ConnectionPoolListener
{
    private final Set<ConnectionPoolListener> connectionPoolListeners;
    
    public ConnectionPoolEventMulticaster() {
        this.connectionPoolListeners = Collections.newSetFromMap(new ConcurrentHashMap<ConnectionPoolListener, Boolean>());
    }
    
    public void add(final ConnectionPoolListener connectionPoolListener) {
        this.connectionPoolListeners.add(connectionPoolListener);
    }
    
    public void remove(final ConnectionPoolListener connectionPoolListener) {
        this.connectionPoolListeners.remove(connectionPoolListener);
    }
    
    @Override
    public void connectionPoolOpened(final ConnectionPoolOpenedEvent event) {
        for (final ConnectionPoolListener cur : this.connectionPoolListeners) {
            cur.connectionPoolOpened(event);
        }
    }
    
    @Override
    public void connectionPoolClosed(final ConnectionPoolEvent event) {
        for (final ConnectionPoolListener cur : this.connectionPoolListeners) {
            cur.connectionPoolClosed(event);
        }
    }
    
    @Override
    public void connectionCheckedOut(final ConnectionEvent event) {
        for (final ConnectionPoolListener cur : this.connectionPoolListeners) {
            cur.connectionCheckedOut(event);
        }
    }
    
    @Override
    public void connectionCheckedIn(final ConnectionEvent event) {
        for (final ConnectionPoolListener cur : this.connectionPoolListeners) {
            cur.connectionCheckedIn(event);
        }
    }
    
    @Override
    public void waitQueueEntered(final ConnectionPoolWaitQueueEvent event) {
        for (final ConnectionPoolListener cur : this.connectionPoolListeners) {
            cur.waitQueueEntered(event);
        }
    }
    
    @Override
    public void waitQueueExited(final ConnectionPoolWaitQueueEvent event) {
        for (final ConnectionPoolListener cur : this.connectionPoolListeners) {
            cur.waitQueueExited(event);
        }
    }
    
    @Override
    public void connectionAdded(final ConnectionEvent event) {
        for (final ConnectionPoolListener cur : this.connectionPoolListeners) {
            cur.connectionAdded(event);
        }
    }
    
    @Override
    public void connectionRemoved(final ConnectionEvent event) {
        for (final ConnectionPoolListener cur : this.connectionPoolListeners) {
            cur.connectionRemoved(event);
        }
    }
}
