// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.MongoCredential;

abstract class Authenticator
{
    private final MongoCredential credential;
    
    Authenticator(final MongoCredential credential) {
        this.credential = credential;
    }
    
    MongoCredential getCredential() {
        return this.credential;
    }
    
    abstract void authenticate(final InternalConnection p0, final ConnectionDescription p1);
    
    abstract void authenticateAsync(final InternalConnection p0, final ConnectionDescription p1, final SingleResultCallback<Void> p2);
}
