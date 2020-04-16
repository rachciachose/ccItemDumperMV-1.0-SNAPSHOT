// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.annotations.Beta;
import java.util.EventListener;

@Beta
public interface ClusterListener extends EventListener
{
    void clusterOpened(final ClusterEvent p0);
    
    void clusterClosed(final ClusterEvent p0);
    
    void clusterDescriptionChanged(final ClusterDescriptionChangedEvent p0);
}
