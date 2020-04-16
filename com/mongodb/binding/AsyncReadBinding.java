// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.ReadPreference;

public interface AsyncReadBinding extends ReferenceCounted
{
    ReadPreference getReadPreference();
    
    void getReadConnectionSource(final SingleResultCallback<AsyncConnectionSource> p0);
    
    AsyncReadBinding retain();
}
