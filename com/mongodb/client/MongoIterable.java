// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import java.util.Collection;
import com.mongodb.Block;
import com.mongodb.Function;

public interface MongoIterable<TResult> extends Iterable<TResult>
{
    MongoCursor<TResult> iterator();
    
    TResult first();
    
     <U> MongoIterable<U> map(final Function<TResult, U> p0);
    
    void forEach(final Block<? super TResult> p0);
    
     <A extends Collection<? super TResult>> A into(final A p0);
    
    MongoIterable<TResult> batchSize(final int p0);
}
