// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.operation.BatchCursor;
import com.mongodb.operation.ReadOperation;
import java.util.Iterator;
import org.bson.BsonDocument;
import org.bson.codecs.Decoder;
import com.mongodb.operation.FindOperation;
import java.util.Collection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCursor;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import org.bson.conversions.Bson;
import com.mongodb.client.model.FindOptions;
import com.mongodb.operation.OperationExecutor;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.FindIterable;

final class FindIterableImpl<TDocument, TResult> implements FindIterable<TResult>
{
    private final MongoNamespace namespace;
    private final Class<TDocument> documentClass;
    private final Class<TResult> resultClass;
    private final ReadPreference readPreference;
    private final CodecRegistry codecRegistry;
    private final OperationExecutor executor;
    private final FindOptions findOptions;
    private Bson filter;
    
    FindIterableImpl(final MongoNamespace namespace, final Class<TDocument> documentClass, final Class<TResult> resultClass, final CodecRegistry codecRegistry, final ReadPreference readPreference, final OperationExecutor executor, final Bson filter, final FindOptions findOptions) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.documentClass = Assertions.notNull("documentClass", documentClass);
        this.resultClass = Assertions.notNull("resultClass", resultClass);
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
        this.executor = Assertions.notNull("executor", executor);
        this.filter = Assertions.notNull("filter", filter);
        this.findOptions = Assertions.notNull("findOptions", findOptions);
    }
    
    @Override
    public FindIterable<TResult> filter(final Bson filter) {
        this.filter = filter;
        return this;
    }
    
    @Override
    public FindIterable<TResult> limit(final int limit) {
        this.findOptions.limit(limit);
        return this;
    }
    
    @Override
    public FindIterable<TResult> skip(final int skip) {
        this.findOptions.skip(skip);
        return this;
    }
    
    @Override
    public FindIterable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.findOptions.maxTime(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public FindIterable<TResult> batchSize(final int batchSize) {
        this.findOptions.batchSize(batchSize);
        return this;
    }
    
    @Override
    public FindIterable<TResult> modifiers(final Bson modifiers) {
        this.findOptions.modifiers(modifiers);
        return this;
    }
    
    @Override
    public FindIterable<TResult> projection(final Bson projection) {
        this.findOptions.projection(projection);
        return this;
    }
    
    @Override
    public FindIterable<TResult> sort(final Bson sort) {
        this.findOptions.sort(sort);
        return this;
    }
    
    @Override
    public FindIterable<TResult> noCursorTimeout(final boolean noCursorTimeout) {
        this.findOptions.noCursorTimeout(noCursorTimeout);
        return this;
    }
    
    @Override
    public FindIterable<TResult> oplogReplay(final boolean oplogReplay) {
        this.findOptions.oplogReplay(oplogReplay);
        return this;
    }
    
    @Override
    public FindIterable<TResult> partial(final boolean partial) {
        this.findOptions.partial(partial);
        return this;
    }
    
    @Override
    public FindIterable<TResult> cursorType(final CursorType cursorType) {
        this.findOptions.cursorType(cursorType);
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
        return new FindOperationIterable(this.createQueryOperation(), this.readPreference, this.executor);
    }
    
    private FindOperation<TResult> createQueryOperation() {
        return new FindOperation<TResult>(this.namespace, this.codecRegistry.get(this.resultClass)).filter(this.filter.toBsonDocument(this.documentClass, this.codecRegistry)).batchSize(this.findOptions.getBatchSize()).skip(this.findOptions.getSkip()).limit(this.findOptions.getLimit()).maxTime(this.findOptions.getMaxTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS).modifiers(this.toBsonDocument(this.findOptions.getModifiers())).projection(this.toBsonDocument(this.findOptions.getProjection())).sort(this.toBsonDocument(this.findOptions.getSort())).cursorType(this.findOptions.getCursorType()).noCursorTimeout(this.findOptions.isNoCursorTimeout()).oplogReplay(this.findOptions.isOplogReplay()).partial(this.findOptions.isPartial()).slaveOk(this.readPreference.isSlaveOk());
    }
    
    private BsonDocument toBsonDocument(final Bson document) {
        return (document == null) ? null : document.toBsonDocument(this.documentClass, this.codecRegistry);
    }
    
    private final class FindOperationIterable extends OperationIterable<TResult>
    {
        private final ReadPreference readPreference;
        private final OperationExecutor executor;
        
        FindOperationIterable(final FindOperation<TResult> operation, final ReadPreference readPreference, final OperationExecutor executor) {
            super(operation, readPreference, executor);
            this.readPreference = readPreference;
            this.executor = executor;
        }
        
        @Override
        public TResult first() {
            final FindOperation<TResult> findFirstOperation = FindIterableImpl.this.createQueryOperation().batchSize(0).limit(-1);
            final BatchCursor<TResult> batchCursor = this.executor.execute(findFirstOperation, this.readPreference);
            return batchCursor.hasNext() ? batchCursor.next().iterator().next() : null;
        }
    }
}
