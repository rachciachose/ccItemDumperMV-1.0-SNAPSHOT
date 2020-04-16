// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

import com.mongodb.connection.AsyncConnection;
import com.mongodb.connection.ServerDescription;
import com.mongodb.connection.Server;
import com.mongodb.selector.WritableServerSelector;
import com.mongodb.selector.ServerSelector;
import com.mongodb.selector.ReadPreferenceServerSelector;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.assertions.Assertions;
import com.mongodb.ReadPreference;
import com.mongodb.connection.Cluster;

public class AsyncClusterBinding extends AbstractReferenceCounted implements AsyncReadWriteBinding
{
    private final Cluster cluster;
    private final ReadPreference readPreference;
    
    public AsyncClusterBinding(final Cluster cluster, final ReadPreference readPreference) {
        this.cluster = Assertions.notNull("cluster", cluster);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
    }
    
    @Override
    public AsyncReadWriteBinding retain() {
        super.retain();
        return this;
    }
    
    @Override
    public ReadPreference getReadPreference() {
        return this.readPreference;
    }
    
    @Override
    public void getReadConnectionSource(final SingleResultCallback<AsyncConnectionSource> callback) {
        this.getAsyncClusterBindingConnectionSource(new ReadPreferenceServerSelector(this.readPreference), callback);
    }
    
    @Override
    public void getWriteConnectionSource(final SingleResultCallback<AsyncConnectionSource> callback) {
        this.getAsyncClusterBindingConnectionSource(new WritableServerSelector(), callback);
    }
    
    private void getAsyncClusterBindingConnectionSource(final ServerSelector serverSelector, final SingleResultCallback<AsyncConnectionSource> callback) {
        this.cluster.selectServerAsync(serverSelector, new SingleResultCallback<Server>() {
            @Override
            public void onResult(final Server result, final Throwable t) {
                if (t != null) {
                    callback.onResult(null, t);
                }
                else {
                    callback.onResult(new AsyncClusterBindingConnectionSource(result), null);
                }
            }
        });
    }
    
    private final class AsyncClusterBindingConnectionSource extends AbstractReferenceCounted implements AsyncConnectionSource
    {
        private final Server server;
        
        private AsyncClusterBindingConnectionSource(final Server server) {
            this.server = server;
            AsyncClusterBinding.this.retain();
        }
        
        @Override
        public ServerDescription getServerDescription() {
            return this.server.getDescription();
        }
        
        @Override
        public void getConnection(final SingleResultCallback<AsyncConnection> callback) {
            this.server.getConnectionAsync(callback);
        }
        
        @Override
        public AsyncConnectionSource retain() {
            super.retain();
            AsyncClusterBinding.this.retain();
            return this;
        }
        
        @Override
        public void release() {
            super.release();
            AsyncClusterBinding.this.release();
        }
    }
}
