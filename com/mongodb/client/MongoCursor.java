// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.annotations.NotThreadSafe;
import java.io.Closeable;
import java.util.Iterator;

@NotThreadSafe
public interface MongoCursor<TResult> extends Iterator<TResult>, Closeable
{
    void close();
    
    boolean hasNext();
    
    TResult next();
    
    TResult tryNext();
    
    ServerCursor getServerCursor();
    
    ServerAddress getServerAddress();
}
