// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import java.util.concurrent.TimeUnit;
import org.bson.conversions.Bson;

public interface DistinctIterable<TResult> extends MongoIterable<TResult>
{
    DistinctIterable<TResult> filter(final Bson p0);
    
    DistinctIterable<TResult> maxTime(final long p0, final TimeUnit p1);
    
    DistinctIterable<TResult> batchSize(final int p0);
}
