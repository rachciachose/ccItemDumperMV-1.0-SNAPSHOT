// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;

public interface AsyncWriteOperation<T>
{
    void executeAsync(final AsyncWriteBinding p0, final SingleResultCallback<T> p1);
}
