// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import com.mongodb.client.model.MapReduceAction;
import java.util.concurrent.TimeUnit;
import org.bson.conversions.Bson;

public interface MapReduceIterable<TResult> extends MongoIterable<TResult>
{
    MapReduceIterable<TResult> collectionName(final String p0);
    
    MapReduceIterable<TResult> finalizeFunction(final String p0);
    
    MapReduceIterable<TResult> scope(final Bson p0);
    
    MapReduceIterable<TResult> sort(final Bson p0);
    
    MapReduceIterable<TResult> filter(final Bson p0);
    
    MapReduceIterable<TResult> limit(final int p0);
    
    MapReduceIterable<TResult> jsMode(final boolean p0);
    
    MapReduceIterable<TResult> verbose(final boolean p0);
    
    MapReduceIterable<TResult> maxTime(final long p0, final TimeUnit p1);
    
    MapReduceIterable<TResult> action(final MapReduceAction p0);
    
    MapReduceIterable<TResult> databaseName(final String p0);
    
    MapReduceIterable<TResult> sharded(final boolean p0);
    
    MapReduceIterable<TResult> nonAtomic(final boolean p0);
    
    MapReduceIterable<TResult> batchSize(final int p0);
}
