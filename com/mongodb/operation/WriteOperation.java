// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.binding.WriteBinding;

public interface WriteOperation<T>
{
    T execute(final WriteBinding p0);
}
