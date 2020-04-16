// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import org.bson.codecs.Decoder;
import com.mongodb.operation.ListDatabasesOperation;
import com.mongodb.operation.BatchCursor;
import com.mongodb.operation.ReadOperation;
import java.util.Collection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCursor;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import com.mongodb.operation.OperationExecutor;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.ListDatabasesIterable;

final class ListDatabasesIterableImpl<TResult> implements ListDatabasesIterable<TResult>
{
    private final Class<TResult> resultClass;
    private final ReadPreference readPreference;
    private final CodecRegistry codecRegistry;
    private final OperationExecutor executor;
    private long maxTimeMS;
    
    ListDatabasesIterableImpl(final Class<TResult> resultClass, final CodecRegistry codecRegistry, final ReadPreference readPreference, final OperationExecutor executor) {
        this.resultClass = Assertions.notNull("clazz", resultClass);
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
        this.executor = Assertions.notNull("executor", executor);
    }
    
    @Override
    public ListDatabasesIterableImpl<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
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
    
    @Override
    public ListDatabasesIterable<TResult> batchSize(final int batchSize) {
        return this;
    }
    
    private MongoIterable<TResult> execute() {
        return new OperationIterable<TResult>(this.createListCollectionsOperation(), this.readPreference, this.executor);
    }
    
    private ListDatabasesOperation<TResult> createListCollectionsOperation() {
        return new ListDatabasesOperation<TResult>(this.codecRegistry.get(this.resultClass)).maxTime(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
}
