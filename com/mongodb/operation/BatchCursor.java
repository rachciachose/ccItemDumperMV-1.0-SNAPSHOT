// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.annotations.NotThreadSafe;
import java.io.Closeable;
import java.util.List;
import java.util.Iterator;

@NotThreadSafe
public interface BatchCursor<T> extends Iterator<List<T>>, Closeable
{
    void close();
    
    boolean hasNext();
    
    List<T> next();
    
    void setBatchSize(final int p0);
    
    int getBatchSize();
    
    List<T> tryNext();
    
    ServerCursor getServerCursor();
    
    ServerAddress getServerAddress();
}
