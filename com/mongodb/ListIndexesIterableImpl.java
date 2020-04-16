// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import org.bson.codecs.Decoder;
import com.mongodb.operation.ListIndexesOperation;
import com.mongodb.operation.BatchCursor;
import com.mongodb.operation.ReadOperation;
import java.util.Collection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCursor;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import com.mongodb.operation.OperationExecutor;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.ListIndexesIterable;

final class ListIndexesIterableImpl<TResult> implements ListIndexesIterable<TResult>
{
    private final MongoNamespace namespace;
    private final Class<TResult> resultClass;
    private final ReadPreference readPreference;
    private final CodecRegistry codecRegistry;
    private final OperationExecutor executor;
    private int batchSize;
    private long maxTimeMS;
    
    ListIndexesIterableImpl(final MongoNamespace namespace, final Class<TResult> resultClass, final CodecRegistry codecRegistry, final ReadPreference readPreference, final OperationExecutor executor) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.resultClass = Assertions.notNull("resultClass", resultClass);
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
        this.executor = Assertions.notNull("executor", executor);
    }
    
    @Override
    public ListIndexesIterable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public ListIndexesIterable<TResult> batchSize(final int batchSize) {
        this.batchSize = batchSize;
        return this;
    }
    
    @Override
    public MongoCursor<TResult> iterator() {
        return this.execute().iterator();
    }
    
    @Override
    public TResult first() {
        return this.execute().first();
    }
    
    @Override
    public <U> MongoIterable<U> map(final Function<TResult, U> mapper) {
        return new MappingIterable<Object, U>(this, mapper);
    }
    
    @Override
    public void forEach(final Block<? super TResult> block) {
        this.execute().forEach(block);
    }
    
    @Override
    public <A extends Collection<? super TResult>> A into(final A target) {
        return this.execute().into(target);
    }
    
    private MongoIterable<TResult> execute() {
        return new OperationIterable<TResult>(this.createListIndexesOperation(), this.readPreference, this.executor);
    }
    
    private ListIndexesOperation<TResult> createListIndexesOperation() {
        return new ListIndexesOperation<TResult>(this.namespace, this.codecRegistry.get(this.resultClass)).batchSize(this.batchSize).maxTime(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
}
