// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.selector;

import com.mongodb.connection.ServerDescription;
import com.mongodb.connection.ClusterDescription;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.mongodb.assertions.Assertions;
import java.util.List;

public final class CompositeServerSelector implements ServerSelector
{
    private final List<ServerSelector> serverSelectors;
    
    public CompositeServerSelector(final List<? extends ServerSelector> serverSelectors) {
        Assertions.notNull("serverSelectors", serverSelectors);
        if (serverSelectors.isEmpty()) {
            throw new IllegalArgumentException("Server selectors can not be an empty list");
        }
        for (final ServerSelector cur : serverSelectors) {
            if (cur == null) {
                throw new IllegalArgumentException("Can not have a null server selector in the list of composed selectors");
            }
        }
        this.serverSelectors = new ArrayList<ServerSelector>();
        for (final ServerSelector cur : serverSelectors) {
            if (cur instanceof CompositeServerSelector) {
                this.serverSelectors.addAll(((CompositeServerSelector)cur).serverSelectors);
            }
            else {
                this.serverSelectors.add(cur);
            }
        }
    }
    
    @Override
    public List<ServerDescription> select(final ClusterDescription clusterDescription) {
        ClusterDescription curClusterDescription = clusterDescription;
        List<ServerDescription> choices = null;
        for (final ServerSelector cur : this.serverSelectors) {
            choices = cur.select(curClusterDescription);
            curClusterDescription = new ClusterDescription(clusterDescription.getConnectionMode(), clusterDescription.getType(), choices);
        }
        return choices;
    }
    
    @Override
    public String toString() {
        return "{serverSelectors=" + this.serverSelectors + '}';
    }
}
