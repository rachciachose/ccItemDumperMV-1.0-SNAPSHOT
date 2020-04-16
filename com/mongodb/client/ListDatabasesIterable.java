// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import java.util.concurrent.TimeUnit;

public interface ListDatabasesIterable<TResult> extends MongoIterable<TResult>
{
    ListDatabasesIterable<TResult> maxTime(final long p0, final TimeUnit p1);
    
    ListDatabasesIterable<TResult> batchSize(final int p0);
}
