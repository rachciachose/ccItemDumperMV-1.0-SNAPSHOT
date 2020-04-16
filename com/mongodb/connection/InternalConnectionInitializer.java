// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.async.SingleResultCallback;

interface InternalConnectionInitializer
{
    ConnectionDescription initialize(final InternalConnection p0);
    
    void initializeAsync(final InternalConnection p0, final SingleResultCallback<ConnectionDescription> p1);
}
