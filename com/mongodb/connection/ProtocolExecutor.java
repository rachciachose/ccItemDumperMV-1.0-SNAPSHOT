// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.async.SingleResultCallback;

interface ProtocolExecutor
{
     <T> T execute(final Protocol<T> p0, final InternalConnection p1);
    
     <T> void executeAsync(final Protocol<T> p0, final InternalConnection p1, final SingleResultCallback<T> p2);
}
