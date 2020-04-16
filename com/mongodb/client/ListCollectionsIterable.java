// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import java.util.concurrent.TimeUnit;
import org.bson.conversions.Bson;

public interface ListCollectionsIterable<TResult> extends MongoIterable<TResult>
{
    ListCollectionsIterable<TResult> filter(final Bson p0);
    
    ListCollectionsIterable<TResult> maxTime(final long p0, final TimeUnit p1);
    
    ListCollectionsIterable<TResult> batchSize(final int p0);
}
