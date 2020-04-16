// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.binding.ReadBinding;

public interface ReadOperation<T>
{
    T execute(final ReadBinding p0);
}
