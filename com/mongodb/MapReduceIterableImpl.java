// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import com.mongodb.client.model.FindOptions;
import org.bson.BsonDocument;
import com.mongodb.operation.WriteOperation;
import com.mongodb.operation.MapReduceToCollectionOperation;
import com.mongodb.operation.BatchCursor;
import com.mongodb.operation.ReadOperation;
import org.bson.codecs.Decoder;
import com.mongodb.operation.MapReduceWithInlineResultsOperation;
import org.bson.BsonJavaScript;
import java.util.Collection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCursor;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import com.mongodb.client.model.MapReduceAction;
import org.bson.conversions.Bson;
import com.mongodb.operation.OperationExecutor;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.MapReduceIterable;

class MapReduceIterableImpl<TDocument, TResult> implements MapReduceIterable<TResult>
{
    private final MongoNamespace namespace;
    private final Class<TDocument> documentClass;
    private final Class<TResult> resultClass;
    private final ReadPreference readPreference;
    private final CodecRegistry codecRegistry;
    private final OperationExecutor executor;
    private final String mapFunction;
    private final String reduceFunction;
    private boolean inline;
    private String collectionName;
    private String finalizeFunction;
    private Bson scope;
    private Bson filter;
    private Bson sort;
    private int limit;
    private boolean jsMode;
    private boolean verbose;
    private long maxTimeMS;
    private MapReduceAction action;
    private String databaseName;
    private boolean sharded;
    private boolean nonAtomic;
    private int batchSize;
    
    MapReduceIterableImpl(final MongoNamespace namespace, final Class<TDocument> documentClass, final Class<TResult> resultClass, final CodecRegistry codecRegistry, final ReadPreference readPreference, final OperationExecutor executor, final String mapFunction, final String reduceFunction) {
        this.inline = true;
        this.verbose = true;
        this.action = MapReduceAction.REPLACE;
        this.namespace = Assertions.notNull("namespace", namespace);
        this.documentClass = Assertions.notNull("documentClass", documentClass);
        this.resultClass = Assertions.notNull("resultClass", resultClass);
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
        this.executor = Assertions.notNull("executor", executor);
        this.mapFunction = Assertions.notNull("mapFunction", mapFunction);
        this.reduceFunction = Assertions.notNull("reduceFunction", reduceFunction);
    }
    
    @Override
    public MapReduceIterable<TResult> collectionName(final String collectionName) {
        this.collectionName = Assertions.notNull("collectionName", collectionName);
        this.inline = false;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> finalizeFunction(final String finalizeFunction) {
        this.finalizeFunction = finalizeFunction;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> scope(final Bson scope) {
        this.scope = scope;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> sort(final Bson sort) {
        this.sort = sort;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> filter(final Bson filter) {
        this.filter = filter;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> limit(final int limit) {
        this.limit = limit;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> jsMode(final boolean jsMode) {
        this.jsMode = jsMode;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> verbose(final boolean verbose) {
        this.verbose = verbose;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> action(final MapReduceAction action) {
        this.action = action;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> databaseName(final String databaseName) {
        this.databaseName = databaseName;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> sharded(final boolean sharded) {
        this.sharded = sharded;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> nonAtomic(final boolean nonAtomic) {
        this.nonAtomic = nonAtomic;
        return this;
    }
    
    @Override
    public MapReduceIterable<TResult> batchSize(final int batchSize) {
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
    
    MongoIterable<TResult> execute() {
        if (this.inline) {
            final MapReduceWithInlineResultsOperation<TResult> operation = new MapReduceWithInlineResultsOperation<TResult>(this.namespace, new BsonJavaScript(this.mapFunction), new BsonJavaScript(this.reduceFunction), this.codecRegistry.get(this.resultClass)).filter(this.toBsonDocument(this.filter)).limit(this.limit).maxTime(this.maxTimeMS, TimeUnit.MILLISECONDS).jsMode(this.jsMode).scope(this.toBsonDocument(this.scope)).sort(this.toBsonDocument(this.sort)).verbose(this.verbose);
            if (this.finalizeFunction != null) {
                operation.finalizeFunction(new BsonJavaScript(this.finalizeFunction));
            }
            return new OperationIterable<TResult>(operation, this.readPreference, this.executor);
        }
        final MapReduceToCollectionOperation operation2 = new MapReduceToCollectionOperation(this.namespace, new BsonJavaScript(this.mapFunction), new BsonJavaScript(this.reduceFunction), this.collectionName).filter(this.toBsonDocument(this.filter)).limit(this.limit).maxTime(this.maxTimeMS, TimeUnit.MILLISECONDS).jsMode(this.jsMode).scope(this.toBsonDocument(this.scope)).sort(this.toBsonDocument(this.sort)).verbose(this.verbose).action(this.action.getValue()).nonAtomic(this.nonAtomic).sharded(this.sharded).databaseName(this.databaseName);
        if (this.finalizeFunction != null) {
            operation2.finalizeFunction(new BsonJavaScript(this.finalizeFunction));
        }
        this.executor.execute((WriteOperation<Object>)operation2);
        final String dbName = (this.databaseName != null) ? this.databaseName : this.namespace.getDatabaseName();
        return (MongoIterable<TResult>)new FindIterableImpl<TDocument, Object>(new MongoNamespace(dbName, this.collectionName), this.documentClass, (Class<Object>)this.resultClass, this.codecRegistry, ReadPreference.primary(), this.executor, new BsonDocument(), new FindOptions()).batchSize(this.batchSize);
    }
    
    private BsonDocument toBsonDocument(final Bson document) {
        return (document == null) ? null : document.toBsonDocument(this.documentClass, this.codecRegistry);
    }
}
