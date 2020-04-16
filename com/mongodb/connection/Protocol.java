// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.event.CommandListener;
import com.mongodb.async.SingleResultCallback;

interface Protocol<T>
{
    T execute(final InternalConnection p0);
    
    void executeAsync(final InternalConnection p0, final SingleResultCallback<T> p1);
    
    void setCommandListener(final CommandListener p0);
}
