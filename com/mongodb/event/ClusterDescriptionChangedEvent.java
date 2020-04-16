// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.connection.ClusterId;
import com.mongodb.connection.ClusterDescription;
import com.mongodb.annotations.Beta;

@Beta
public class ClusterDescriptionChangedEvent extends ClusterEvent
{
    private final ClusterDescription clusterDescription;
    
    public ClusterDescriptionChangedEvent(final ClusterId clusterId, final ClusterDescription clusterDescription) {
        super(clusterId);
        this.clusterDescription = clusterDescription;
    }
    
    public ClusterDescription getClusterDescription() {
        return this.clusterDescription;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ClusterDescriptionChangedEvent that = (ClusterDescriptionChangedEvent)o;
        return this.getClusterId().equals(that.getClusterId()) && this.clusterDescription.equals(that.clusterDescription);
    }
    
    @Override
    public int hashCode() {
        return this.clusterDescription.hashCode();
    }
}
