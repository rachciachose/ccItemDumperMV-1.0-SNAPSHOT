// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import java.util.concurrent.TimeUnit;

public interface ListIndexesIterable<TResult> extends MongoIterable<TResult>
{
    ListIndexesIterable<TResult> maxTime(final long p0, final TimeUnit p1);
    
    ListIndexesIterable<TResult> batchSize(final int p0);
}
