// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.management;

import com.mongodb.event.ConnectionPoolWaitQueueEvent;
import com.mongodb.event.ConnectionEvent;
import com.mongodb.event.ConnectionPoolOpenedEvent;
import java.util.concurrent.atomic.AtomicInteger;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.ServerAddress;
import com.mongodb.event.ConnectionPoolListenerAdapter;

final class ConnectionPoolStatistics extends ConnectionPoolListenerAdapter implements ConnectionPoolStatisticsMBean
{
    private final ServerAddress serverAddress;
    private final ConnectionPoolSettings settings;
    private final AtomicInteger size;
    private final AtomicInteger checkedOutCount;
    private final AtomicInteger waitQueueSize;
    
    public ConnectionPoolStatistics(final ConnectionPoolOpenedEvent event) {
        this.size = new AtomicInteger();
        this.checkedOutCount = new AtomicInteger();
        this.waitQueueSize = new AtomicInteger();
        this.serverAddress = event.getServerId().getAddress();
        this.settings = event.getSettings();
    }
    
    @Override
    public String getHost() {
        return this.serverAddress.getHost();
    }
    
    @Override
    public int getPort() {
        return this.serverAddress.getPort();
    }
    
    @Override
    public int getMinSize() {
        return this.settings.getMinSize();
    }
    
    @Override
    public int getMaxSize() {
        return this.settings.getMaxSize();
    }
    
    @Override
    public int getSize() {
        return this.size.get();
    }
    
    @Override
    public int getCheckedOutCount() {
        return this.checkedOutCount.get();
    }
    
    @Override
    public int getWaitQueueSize() {
        return this.waitQueueSize.get();
    }
    
    @Override
    public void connectionCheckedOut(final ConnectionEvent event) {
        this.checkedOutCount.incrementAndGet();
    }
    
    @Override
    public void connectionCheckedIn(final ConnectionEvent event) {
        this.checkedOutCount.decrementAndGet();
    }
    
    @Override
    public void connectionAdded(final ConnectionEvent event) {
        this.size.incrementAndGet();
    }
    
    @Override
    public void connectionRemoved(final ConnectionEvent event) {
        this.size.decrementAndGet();
    }
    
    @Override
    public void waitQueueEntered(final ConnectionPoolWaitQueueEvent event) {
        this.waitQueueSize.incrementAndGet();
    }
    
    @Override
    public void waitQueueExited(final ConnectionPoolWaitQueueEvent event) {
        this.waitQueueSize.decrementAndGet();
    }
}
