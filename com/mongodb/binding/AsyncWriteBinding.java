// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

import com.mongodb.async.SingleResultCallback;

public interface AsyncWriteBinding extends ReferenceCounted
{
    void getWriteConnectionSource(final SingleResultCallback<AsyncConnectionSource> p0);
    
    AsyncWriteBinding retain();
}
