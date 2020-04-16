// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.ReadPreference;

public interface OperationExecutor
{
     <T> T execute(final ReadOperation<T> p0, final ReadPreference p1);
    
     <T> T execute(final WriteOperation<T> p0);
}
