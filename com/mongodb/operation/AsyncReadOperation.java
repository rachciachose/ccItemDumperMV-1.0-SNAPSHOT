// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncReadBinding;

public interface AsyncReadOperation<T>
{
    void executeAsync(final AsyncReadBinding p0, final SingleResultCallback<T> p1);
}
