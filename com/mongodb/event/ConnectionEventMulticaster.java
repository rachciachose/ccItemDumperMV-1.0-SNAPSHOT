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
public class ConnectionEventMulticaster implements ConnectionListener
{
    private final Set<ConnectionListener> connectionListeners;
    
    public ConnectionEventMulticaster() {
        this.connectionListeners = Collections.newSetFromMap(new ConcurrentHashMap<ConnectionListener, Boolean>());
    }
    
    public void add(final ConnectionListener connectionListener) {
        this.connectionListeners.add(connectionListener);
    }
    
    public void remove(final ConnectionListener connectionListener) {
        this.connectionListeners.remove(connectionListener);
    }
    
    @Override
    public void connectionOpened(final ConnectionEvent event) {
        for (final ConnectionListener cur : this.connectionListeners) {
            cur.connectionOpened(event);
        }
    }
    
    @Override
    public void connectionClosed(final ConnectionEvent event) {
        for (final ConnectionListener cur : this.connectionListeners) {
            cur.connectionClosed(event);
        }
    }
    
    @Override
    public void messagesSent(final ConnectionMessagesSentEvent event) {
        for (final ConnectionListener cur : this.connectionListeners) {
            cur.messagesSent(event);
        }
    }
    
    @Override
    public void messageReceived(final ConnectionMessageReceivedEvent event) {
        for (final ConnectionListener cur : this.connectionListeners) {
            cur.messageReceived(event);
        }
    }
}
