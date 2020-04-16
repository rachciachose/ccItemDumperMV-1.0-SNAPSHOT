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
public class ClusterEventMulticaster implements ClusterListener
{
    private final Set<ClusterListener> clusterListeners;
    
    public ClusterEventMulticaster() {
        this.clusterListeners = Collections.newSetFromMap(new ConcurrentHashMap<ClusterListener, Boolean>());
    }
    
    public void add(final ClusterListener clusterListener) {
        this.clusterListeners.add(clusterListener);
    }
    
    public void remove(final ClusterListener clusterListener) {
        this.clusterListeners.remove(clusterListener);
    }
    
    @Override
    public void clusterOpened(final ClusterEvent event) {
        for (final ClusterListener cur : this.clusterListeners) {
            cur.clusterOpened(event);
        }
    }
    
    @Override
    public void clusterClosed(final ClusterEvent event) {
        for (final ClusterListener cur : this.clusterListeners) {
            cur.clusterClosed(event);
        }
    }
    
    @Override
    public void clusterDescriptionChanged(final ClusterDescriptionChangedEvent event) {
        for (final ClusterListener cur : this.clusterListeners) {
            cur.clusterDescriptionChanged(event);
        }
    }
}
