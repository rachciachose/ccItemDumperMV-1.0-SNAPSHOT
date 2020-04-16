// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

import com.mongodb.connection.Connection;
import com.mongodb.connection.ServerDescription;
import com.mongodb.connection.Server;
import com.mongodb.selector.WritableServerSelector;
import com.mongodb.selector.ServerSelector;
import com.mongodb.selector.ReadPreferenceServerSelector;
import com.mongodb.assertions.Assertions;
import com.mongodb.ReadPreference;
import com.mongodb.connection.Cluster;

public class ClusterBinding extends AbstractReferenceCounted implements ReadWriteBinding
{
    private final Cluster cluster;
    private final ReadPreference readPreference;
    
    public ClusterBinding(final Cluster cluster, final ReadPreference readPreference) {
        this.cluster = Assertions.notNull("cluster", cluster);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
    }
    
    @Override
    public ReadWriteBinding retain() {
        super.retain();
        return this;
    }
    
    @Override
    public ReadPreference getReadPreference() {
        return this.readPreference;
    }
    
    @Override
    public ConnectionSource getReadConnectionSource() {
        return new ClusterBindingConnectionSource((ServerSelector)new ReadPreferenceServerSelector(this.readPreference));
    }
    
    @Override
    public ConnectionSource getWriteConnectionSource() {
        return new ClusterBindingConnectionSource((ServerSelector)new WritableServerSelector());
    }
    
    private final class ClusterBindingConnectionSource extends AbstractReferenceCounted implements ConnectionSource
    {
        private final Server server;
        
        private ClusterBindingConnectionSource(final ServerSelector serverSelector) {
            this.server = ClusterBinding.this.cluster.selectServer(serverSelector);
            ClusterBinding.this.retain();
        }
        
        @Override
        public ServerDescription getServerDescription() {
            return this.server.getDescription();
        }
        
        @Override
        public Connection getConnection() {
            return this.server.getConnection();
        }
        
        @Override
        public ConnectionSource retain() {
            super.retain();
            ClusterBinding.this.retain();
            return this;
        }
        
        @Override
        public void release() {
            super.release();
            ClusterBinding.this.release();
        }
    }
}
