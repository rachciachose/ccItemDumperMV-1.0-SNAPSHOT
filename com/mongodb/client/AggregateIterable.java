// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import java.util.concurrent.TimeUnit;

public interface AggregateIterable<TResult> extends MongoIterable<TResult>
{
    AggregateIterable<TResult> allowDiskUse(final Boolean p0);
    
    AggregateIterable<TResult> batchSize(final int p0);
    
    AggregateIterable<TResult> maxTime(final long p0, final TimeUnit p1);
    
    AggregateIterable<TResult> useCursor(final Boolean p0);
}
