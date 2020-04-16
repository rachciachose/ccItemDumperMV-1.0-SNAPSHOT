// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import java.util.ArrayList;
import com.mongodb.client.FindIterable;
import org.bson.BsonValue;
import com.mongodb.operation.BatchCursor;
import com.mongodb.operation.ReadOperation;
import org.bson.codecs.Decoder;
import com.mongodb.operation.AggregateOperation;
import com.mongodb.client.model.FindOptions;
import com.mongodb.operation.WriteOperation;
import com.mongodb.operation.AggregateToCollectionOperation;
import org.bson.BsonDocument;
import java.util.Collection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCursor;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import org.bson.conversions.Bson;
import java.util.List;
import com.mongodb.operation.OperationExecutor;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.AggregateIterable;

class AggregateIterableImpl<TDocument, TResult> implements AggregateIterable<TResult>
{
    private final MongoNamespace namespace;
    private final Class<TDocument> documentClass;
    private final Class<TResult> resultClass;
    private final ReadPreference readPreference;
    private final CodecRegistry codecRegistry;
    private final OperationExecutor executor;
    private final List<? extends Bson> pipeline;
    private Boolean allowDiskUse;
    private Integer batchSize;
    private long maxTimeMS;
    private Boolean useCursor;
    
    AggregateIterableImpl(final MongoNamespace namespace, final Class<TDocument> documentClass, final Class<TResult> resultClass, final CodecRegistry codecRegistry, final ReadPreference readPreference, final OperationExecutor executor, final List<? extends Bson> pipeline) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.documentClass = Assertions.notNull("documentClass", documentClass);
        this.resultClass = Assertions.notNull("resultClass", resultClass);
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
        this.executor = Assertions.notNull("executor", executor);
        this.pipeline = Assertions.notNull("pipeline", pipeline);
    }
    
    @Override
    public AggregateIterable<TResult> allowDiskUse(final Boolean allowDiskUse) {
        this.allowDiskUse = allowDiskUse;
        return this;
    }
    
    @Override
    public AggregateIterable<TResult> batchSize(final int batchSize) {
        this.batchSize = batchSize;
        return this;
    }
    
    @Override
    public AggregateIterable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public AggregateIterable<TResult> useCursor(final Boolean useCursor) {
        this.useCursor = useCursor;
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
        final List<BsonDocument> aggregateList = this.createBsonDocumentList(this.pipeline);
        final BsonValue outCollection = (aggregateList.size() == 0) ? null : aggregateList.get(aggregateList.size() - 1).get("$out");
        if (outCollection != null) {
            final AggregateToCollectionOperation operation = new AggregateToCollectionOperation(this.namespace, aggregateList).maxTime(this.maxTimeMS, TimeUnit.MILLISECONDS).allowDiskUse(this.allowDiskUse);
            this.executor.execute((WriteOperation<Object>)operation);
            final FindIterable<TResult> findOperation = new FindIterableImpl<Object, TResult>(new MongoNamespace(this.namespace.getDatabaseName(), outCollection.asString().getValue()), this.documentClass, this.resultClass, this.codecRegistry, this.readPreference, this.executor, new BsonDocument(), new FindOptions());
            if (this.batchSize != null) {
                findOperation.batchSize((int)this.batchSize);
            }
            return findOperation;
        }
        return new OperationIterable<TResult>(new AggregateOperation<TResult>(this.namespace, aggregateList, this.codecRegistry.get(this.resultClass)).maxTime(this.maxTimeMS, TimeUnit.MILLISECONDS).allowDiskUse(this.allowDiskUse).batchSize(this.batchSize).useCursor(this.useCursor), this.readPreference, this.executor);
    }
    
    private List<BsonDocument> createBsonDocumentList(final List<? extends Bson> pipeline) {
        final List<BsonDocument> aggregateList = new ArrayList<BsonDocument>(pipeline.size());
        for (final Bson obj : pipeline) {
            aggregateList.add(obj.toBsonDocument(this.documentClass, this.codecRegistry));
        }
        return aggregateList;
    }
}
