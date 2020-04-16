// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

import com.mongodb.connection.AsyncConnection;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.connection.ServerDescription;

public interface AsyncConnectionSource extends ReferenceCounted
{
    ServerDescription getServerDescription();
    
    void getConnection(final SingleResultCallback<AsyncConnection> p0);
    
    AsyncConnectionSource retain();
}
