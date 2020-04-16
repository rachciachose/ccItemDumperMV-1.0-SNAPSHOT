// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.management;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import javax.management.ObjectName;
import com.mongodb.event.ConnectionPoolWaitQueueEvent;
import com.mongodb.event.ConnectionEvent;
import com.mongodb.event.ConnectionPoolEvent;
import com.mongodb.event.ConnectionPoolOpenedEvent;
import java.util.concurrent.ConcurrentHashMap;
import com.mongodb.connection.ServerId;
import java.util.concurrent.ConcurrentMap;
import com.mongodb.annotations.Beta;
import com.mongodb.event.ConnectionPoolListener;

@Beta
public class JMXConnectionPoolListener implements ConnectionPoolListener
{
    private final ConcurrentMap<ServerId, ConnectionPoolStatistics> map;
    
    public JMXConnectionPoolListener() {
        this.map = new ConcurrentHashMap<ServerId, ConnectionPoolStatistics>();
    }
    
    @Override
    public void connectionPoolOpened(final ConnectionPoolOpenedEvent event) {
        final ConnectionPoolStatistics statistics = new ConnectionPoolStatistics(event);
        this.map.put(event.getServerId(), statistics);
        MBeanServerFactory.getMBeanServer().registerMBean(statistics, this.getMBeanObjectName(event.getServerId()));
    }
    
    @Override
    public void connectionPoolClosed(final ConnectionPoolEvent event) {
        this.map.remove(event.getServerId());
        MBeanServerFactory.getMBeanServer().unregisterMBean(this.getMBeanObjectName(event.getServerId()));
    }
    
    @Override
    public void connectionCheckedOut(final ConnectionEvent event) {
        final ConnectionPoolStatistics statistics = this.getStatistics(event);
        if (statistics != null) {
            statistics.connectionCheckedOut(event);
        }
    }
    
    @Override
    public void connectionCheckedIn(final ConnectionEvent event) {
        final ConnectionPoolStatistics statistics = this.getStatistics(event);
        if (statistics != null) {
            statistics.connectionCheckedIn(event);
        }
    }
    
    @Override
    public void waitQueueEntered(final ConnectionPoolWaitQueueEvent event) {
        final ConnectionPoolListener statistics = this.getStatistics(event);
        if (statistics != null) {
            statistics.waitQueueEntered(event);
        }
    }
    
    @Override
    public void waitQueueExited(final ConnectionPoolWaitQueueEvent event) {
        final ConnectionPoolListener statistics = this.getStatistics(event);
        if (statistics != null) {
            statistics.waitQueueExited(event);
        }
    }
    
    @Override
    public void connectionAdded(final ConnectionEvent event) {
        final ConnectionPoolStatistics statistics = this.getStatistics(event);
        if (statistics != null) {
            statistics.connectionAdded(event);
        }
    }
    
    @Override
    public void connectionRemoved(final ConnectionEvent event) {
        final ConnectionPoolStatistics statistics = this.getStatistics(event);
        if (statistics != null) {
            statistics.connectionRemoved(event);
        }
    }
    
    String getMBeanObjectName(final ServerId serverId) {
        String name = String.format("org.mongodb.driver:type=ConnectionPool,clusterId=%s,host=%s,port=%s", this.ensureValidValue(serverId.getClusterId().getValue()), this.ensureValidValue(serverId.getAddress().getHost()), serverId.getAddress().getPort());
        if (serverId.getClusterId().getDescription() != null) {
            name = String.format("%s,description=%s", name, this.ensureValidValue(serverId.getClusterId().getDescription()));
        }
        return name;
    }
    
    ConnectionPoolStatisticsMBean getMBean(final ServerId serverId) {
        return this.getStatistics(serverId);
    }
    
    private ConnectionPoolStatistics getStatistics(final ConnectionEvent event) {
        return this.getStatistics(event.getConnectionId().getServerId());
    }
    
    private ConnectionPoolListener getStatistics(final ConnectionPoolEvent event) {
        return this.getStatistics(event.getServerId());
    }
    
    private ConnectionPoolStatistics getStatistics(final ServerId serverId) {
        return this.map.get(serverId);
    }
    
    private String ensureValidValue(final String value) {
        if (this.containsQuotableCharacter(value)) {
            return ObjectName.quote(value);
        }
        return value;
    }
    
    private boolean containsQuotableCharacter(final String value) {
        if (value == null || value.length() == 0) {
            return false;
        }
        final List<String> quoteableCharacters = Arrays.asList(",", ":", "?", "*", "=", "\"", "\\", "\n");
        for (final String quotable : quoteableCharacters) {
            if (value.contains(quotable)) {
                return true;
            }
        }
        return false;
    }
}
