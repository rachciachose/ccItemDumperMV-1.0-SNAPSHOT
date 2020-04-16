// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.annotations.Beta;

@Beta
public abstract class ClusterListenerAdapter implements ClusterListener
{
    @Override
    public void clusterOpened(final ClusterEvent event) {
    }
    
    @Override
    public void clusterClosed(final ClusterEvent event) {
    }
    
    @Override
    public void clusterDescriptionChanged(final ClusterDescriptionChangedEvent event) {
    }
}
