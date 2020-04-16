// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.selector.ServerSelector;
import java.io.Closeable;

public interface Cluster extends Closeable
{
    ClusterDescription getDescription();
    
    Server selectServer(final ServerSelector p0);
    
    void selectServerAsync(final ServerSelector p0, final SingleResultCallback<Server> p1);
    
    void close();
    
    boolean isClosed();
}
