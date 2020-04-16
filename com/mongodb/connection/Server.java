// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.annotations.ThreadSafe;

@ThreadSafe
public interface Server
{
    ServerDescription getDescription();
    
    Connection getConnection();
    
    void getConnectionAsync(final SingleResultCallback<AsyncConnection> p0);
}
