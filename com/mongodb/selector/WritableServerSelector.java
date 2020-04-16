// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.selector;

import com.mongodb.connection.ServerDescription;
import java.util.List;
import com.mongodb.connection.ClusterDescription;

public final class WritableServerSelector implements ServerSelector
{
    @Override
    public List<ServerDescription> select(final ClusterDescription clusterDescription) {
        return clusterDescription.getPrimaries();
    }
    
    @Override
    public String toString() {
        return "WritableServerSelector";
    }
}
