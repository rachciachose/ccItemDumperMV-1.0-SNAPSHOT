// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.annotations.Beta;

@Beta
public interface ChangeListener<T>
{
    void stateChanged(final ChangeEvent<T> p0);
}
