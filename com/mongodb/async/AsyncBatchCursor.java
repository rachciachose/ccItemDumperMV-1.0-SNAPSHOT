// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.async;

import java.util.List;
import java.io.Closeable;

public interface AsyncBatchCursor<T> extends Closeable
{
    void next(final SingleResultCallback<List<T>> p0);
    
    void setBatchSize(final int p0);
    
    int getBatchSize();
    
    boolean isClosed();
    
    void close();
}
