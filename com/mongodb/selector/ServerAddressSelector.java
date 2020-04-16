// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.selector;

import java.util.Collections;
import java.util.Arrays;
import com.mongodb.connection.ServerDescription;
import java.util.List;
import com.mongodb.connection.ClusterDescription;
import com.mongodb.assertions.Assertions;
import com.mongodb.ServerAddress;

public class ServerAddressSelector implements ServerSelector
{
    private final ServerAddress serverAddress;
    
    public ServerAddressSelector(final ServerAddress serverAddress) {
        this.serverAddress = Assertions.notNull("serverAddress", serverAddress);
    }
    
    public ServerAddress getServerAddress() {
        return this.serverAddress;
    }
    
    @Override
    public List<ServerDescription> select(final ClusterDescription clusterDescription) {
        if (clusterDescription.getByServerAddress(this.serverAddress) != null) {
            return Arrays.asList(clusterDescription.getByServerAddress(this.serverAddress));
        }
        return Collections.emptyList();
    }
    
    @Override
    public String toString() {
        return "ServerAddressSelector{serverAddress=" + this.serverAddress + '}';
    }
}
