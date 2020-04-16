// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.async.SingleResultCallback;
import java.util.concurrent.TimeUnit;
import java.io.Closeable;

interface ConnectionPool extends Closeable
{
    InternalConnection get();
    
    InternalConnection get(final long p0, final TimeUnit p1);
    
    void getAsync(final SingleResultCallback<InternalConnection> p0);
    
    void invalidate();
    
    void close();
}
