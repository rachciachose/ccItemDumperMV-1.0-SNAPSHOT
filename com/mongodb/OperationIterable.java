// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import java.util.Collection;
import com.mongodb.client.MongoCursor;
import com.mongodb.operation.OperationExecutor;
import com.mongodb.operation.BatchCursor;
import com.mongodb.operation.ReadOperation;
import com.mongodb.client.MongoIterable;

class OperationIterable<T> implements MongoIterable<T>
{
    private final ReadOperation<? extends BatchCursor<T>> operation;
    private final ReadPreference readPreference;
    private final OperationExecutor executor;
    
    OperationIterable(final ReadOperation<? extends BatchCursor<T>> operation, final ReadPreference readPreference, final OperationExecutor executor) {
        this.operation = operation;
        this.readPreference = readPreference;
        this.executor = executor;
    }
    
    @Override
    public MongoCursor<T> iterator() {
        return new MongoBatchCursorAdapter<T>(this.executor.execute(this.operation, this.readPreference));
    }
    
    @Override
    public T first() {
        final MongoCursor<T> cursor = this.iterator();
        try {
            if (!cursor.hasNext()) {
                return null;
            }
            return cursor.next();
        }
        finally {
            cursor.close();
        }
    }
    
    @Override
    public <U> MongoIterable<U> map(final Function<T, U> mapper) {
        return new MappingIterable<Object, U>(this, mapper);
    }
    
    @Override
    public void forEach(final Block<? super T> block) {
        final MongoCursor<T> cursor = this.iterator();
        try {
            while (cursor.hasNext()) {
                block.apply((Object)cursor.next());
            }
        }
        finally {
            cursor.close();
        }
    }
    
    @Override
    public <A extends Collection<? super T>> A into(final A target) {
        this.forEach(new Block<T>() {
            @Override
            public void apply(final T t) {
                ((Collection<T>)target).add(t);
            }
        });
        return target;
    }
    
    @Override
    public MongoIterable<T> batchSize(final int batchSize) {
        throw new UnsupportedOperationException();
    }
}
