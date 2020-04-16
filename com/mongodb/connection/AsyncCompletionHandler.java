// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

public interface AsyncCompletionHandler<T>
{
    void completed(final T p0);
    
    void failed(final Throwable p0);
}
