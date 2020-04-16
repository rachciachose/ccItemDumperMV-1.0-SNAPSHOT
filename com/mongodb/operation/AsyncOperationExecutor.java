// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.ReadPreference;

public interface AsyncOperationExecutor
{
     <T> void execute(final AsyncReadOperation<T> p0, final ReadPreference p1, final SingleResultCallback<T> p2);
    
     <T> void execute(final AsyncWriteOperation<T> p0, final SingleResultCallback<T> p1);
}
