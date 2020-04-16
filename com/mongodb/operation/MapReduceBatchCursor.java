// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

public interface MapReduceBatchCursor<T> extends BatchCursor<T>
{
    MapReduceStatistics getStatistics();
}
