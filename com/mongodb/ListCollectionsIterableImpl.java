// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import org.bson.BsonDocument;
import org.bson.codecs.Decoder;
import com.mongodb.operation.ListCollectionsOperation;
import com.mongodb.operation.BatchCursor;
import com.mongodb.operation.ReadOperation;
import java.util.Collection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCursor;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import org.bson.conversions.Bson;
import com.mongodb.operation.OperationExecutor;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.ListCollectionsIterable;

final class ListCollectionsIterableImpl<TResult> implements ListCollectionsIterable<TResult>
{
    private final String databaseName;
    private final Class<TResult> resultClass;
    private final ReadPreference readPreference;
    private final CodecRegistry codecRegistry;
    private final OperationExecutor executor;
    private Bson filter;
    private int batchSize;
    private long maxTimeMS;
    
    ListCollectionsIterableImpl(final String databaseName, final Class<TResult> resultClass, final CodecRegistry codecRegistry, final ReadPreference readPreference, final OperationExecutor executor) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.resultClass = Assertions.notNull("resultClass", resultClass);
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
        this.executor = Assertions.notNull("executor", executor);
    }
    
    @Override
    public ListCollectionsIterable<TResult> filter(final Bson filter) {
        Assertions.notNull("filter", filter);
        this.filter = filter;
        return this;
    }
    
    @Override
    public ListCollectionsIterable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public ListCollectionsIterable<TResult> batchSize(final int batchSize) {
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
        return new OperationIterable<TResult>(this.createListCollectionsOperation(), this.readPreference, this.executor);
    }
    
    private ListCollectionsOperation<TResult> createListCollectionsOperation() {
        return new ListCollectionsOperation<TResult>(this.databaseName, this.codecRegistry.get(this.resultClass)).filter(this.toBsonDocument(this.filter)).batchSize(this.batchSize).maxTime(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    private BsonDocument toBsonDocument(final Bson document) {
        return (document == null) ? null : document.toBsonDocument(BsonDocument.class, this.codecRegistry);
    }
}
