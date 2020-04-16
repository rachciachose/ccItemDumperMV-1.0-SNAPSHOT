// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.async.AsyncBatchCursor;

public interface MapReduceAsyncBatchCursor<T> extends AsyncBatchCursor<T>
{
    MapReduceStatistics getStatistics();
}
