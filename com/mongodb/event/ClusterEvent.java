// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.connection.ClusterId;
import com.mongodb.annotations.Beta;

@Beta
public class ClusterEvent
{
    private final ClusterId clusterId;
    
    public ClusterEvent(final ClusterId clusterId) {
        this.clusterId = clusterId;
    }
    
    public ClusterId getClusterId() {
        return this.clusterId;
    }
}
