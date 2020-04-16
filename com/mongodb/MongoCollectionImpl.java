// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BsonDocumentWrapper;
import org.bson.codecs.Codec;
import com.mongodb.bulk.BulkWriteUpsert;
import com.mongodb.bulk.BulkWriteError;
import java.util.Arrays;
import com.mongodb.operation.RenameCollectionOperation;
import com.mongodb.client.model.RenameCollectionOptions;
import com.mongodb.operation.DropIndexOperation;
import org.bson.Document;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.operation.CreateIndexesOperation;
import com.mongodb.bulk.IndexRequest;
import java.util.Collections;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.operation.DropCollectionOperation;
import com.mongodb.operation.FindAndUpdateOperation;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.operation.FindAndReplaceOperation;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import org.bson.codecs.Decoder;
import com.mongodb.operation.FindAndDeleteOperation;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.model.InsertManyOptions;
import java.util.Iterator;
import com.mongodb.operation.WriteOperation;
import com.mongodb.operation.MixedBulkWriteOperation;
import com.mongodb.client.model.DeleteManyModel;
import com.mongodb.bulk.DeleteRequest;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.bulk.UpdateRequest;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.bulk.InsertRequest;
import org.bson.codecs.CollectibleCodec;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.bulk.WriteRequest;
import java.util.ArrayList;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.AggregateIterable;
import java.util.List;
import com.mongodb.client.model.FindOptions;
import com.mongodb.client.FindIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.operation.ReadOperation;
import org.bson.BsonString;
import org.bson.BsonValue;
import java.util.concurrent.TimeUnit;
import com.mongodb.operation.CountOperation;
import org.bson.conversions.Bson;
import com.mongodb.client.model.CountOptions;
import org.bson.BsonDocument;
import com.mongodb.assertions.Assertions;
import com.mongodb.operation.OperationExecutor;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.MongoCollection;

class MongoCollectionImpl<TDocument> implements MongoCollection<TDocument>
{
    private final MongoNamespace namespace;
    private final Class<TDocument> documentClass;
    private final ReadPreference readPreference;
    private final CodecRegistry codecRegistry;
    private final WriteConcern writeConcern;
    private final OperationExecutor executor;
    
    MongoCollectionImpl(final MongoNamespace namespace, final Class<TDocument> documentClass, final CodecRegistry codecRegistry, final ReadPreference readPreference, final WriteConcern writeConcern, final OperationExecutor executor) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.documentClass = Assertions.notNull("documentClass", documentClass);
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
        this.writeConcern = Assertions.notNull("writeConcern", writeConcern);
        this.executor = Assertions.notNull("executor", executor);
    }
    
    @Override
    public MongoNamespace getNamespace() {
        return this.namespace;
    }
    
    @Override
    public Class<TDocument> getDocumentClass() {
        return this.documentClass;
    }
    
    @Override
    public CodecRegistry getCodecRegistry() {
        return this.codecRegistry;
    }
    
    @Override
    public ReadPreference getReadPreference() {
        return this.readPreference;
    }
    
    @Override
    public WriteConcern getWriteConcern() {
        return this.writeConcern;
    }
    
    @Override
    public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(final Class<NewTDocument> clazz) {
        return new MongoCollectionImpl<NewTDocument>(this.namespace, clazz, this.codecRegistry, this.readPreference, this.writeConcern, this.executor);
    }
    
    @Override
    public MongoCollection<TDocument> withCodecRegistry(final CodecRegistry codecRegistry) {
        return new MongoCollectionImpl(this.namespace, (Class<Object>)this.documentClass, codecRegistry, this.readPreference, this.writeConcern, this.executor);
    }
    
    @Override
    public MongoCollection<TDocument> withReadPreference(final ReadPreference readPreference) {
        return new MongoCollectionImpl(this.namespace, (Class<Object>)this.documentClass, this.codecRegistry, readPreference, this.writeConcern, this.executor);
    }
    
    @Override
    public MongoCollection<TDocument> withWriteConcern(final WriteConcern writeConcern) {
        return new MongoCollectionImpl(this.namespace, (Class<Object>)this.documentClass, this.codecRegistry, this.readPreference, writeConcern, this.executor);
    }
    
    @Override
    public long count() {
        return this.count(new BsonDocument(), new CountOptions());
    }
    
    @Override
    public long count(final Bson filter) {
        return this.count(filter, new CountOptions());
    }
    
    @Override
    public long count(final Bson filter, final CountOptions options) {
        final CountOperation operation = new CountOperation(this.namespace).filter(this.toBsonDocument(filter)).skip(options.getSkip()).limit(options.getLimit()).maxTime(options.getMaxTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        if (options.getHint() != null) {
            operation.hint(this.toBsonDocument(options.getHint()));
        }
        else if (options.getHintString() != null) {
            operation.hint(new BsonString(options.getHintString()));
        }
        return this.executor.execute((ReadOperation<Long>)operation, this.readPreference);
    }
    
    @Override
    public <TResult> DistinctIterable<TResult> distinct(final String fieldName, final Class<TResult> resultClass) {
        return this.distinct(fieldName, new BsonDocument(), resultClass);
    }
    
    @Override
    public <TResult> DistinctIterable<TResult> distinct(final String fieldName, final Bson filter, final Class<TResult> resultClass) {
        return new DistinctIterableImpl<Object, TResult>(this.namespace, this.documentClass, resultClass, this.codecRegistry, this.readPreference, this.executor, fieldName, filter);
    }
    
    @Override
    public FindIterable<TDocument> find() {
        return this.find(new BsonDocument(), this.documentClass);
    }
    
    @Override
    public <TResult> FindIterable<TResult> find(final Class<TResult> resultClass) {
        return this.find(new BsonDocument(), resultClass);
    }
    
    @Override
    public FindIterable<TDocument> find(final Bson filter) {
        return this.find(filter, this.documentClass);
    }
    
    @Override
    public <TResult> FindIterable<TResult> find(final Bson filter, final Class<TResult> resultClass) {
        return new FindIterableImpl<Object, TResult>(this.namespace, this.documentClass, resultClass, this.codecRegistry, this.readPreference, this.executor, filter, new FindOptions());
    }
    
    @Override
    public AggregateIterable<TDocument> aggregate(final List<? extends Bson> pipeline) {
        return this.aggregate(pipeline, this.documentClass);
    }
    
    @Override
    public <TResult> AggregateIterable<TResult> aggregate(final List<? extends Bson> pipeline, final Class<TResult> resultClass) {
        return new AggregateIterableImpl<Object, TResult>(this.namespace, this.documentClass, resultClass, this.codecRegistry, this.readPreference, this.executor, pipeline);
    }
    
    @Override
    public MapReduceIterable<TDocument> mapReduce(final String mapFunction, final String reduceFunction) {
        return this.mapReduce(mapFunction, reduceFunction, this.documentClass);
    }
    
    @Override
    public <TResult> MapReduceIterable<TResult> mapReduce(final String mapFunction, final String reduceFunction, final Class<TResult> resultClass) {
        return new MapReduceIterableImpl<Object, TResult>(this.namespace, this.documentClass, resultClass, this.codecRegistry, this.readPreference, this.executor, mapFunction, reduceFunction);
    }
    
    @Override
    public BulkWriteResult bulkWrite(final List<? extends WriteModel<? extends TDocument>> requests) {
        return this.bulkWrite(requests, new BulkWriteOptions());
    }
    
    @Override
    public BulkWriteResult bulkWrite(final List<? extends WriteModel<? extends TDocument>> requests, final BulkWriteOptions options) {
        final List<WriteRequest> writeRequests = new ArrayList<WriteRequest>(requests.size());
        for (final WriteModel<? extends TDocument> writeModel : requests) {
            WriteRequest writeRequest;
            if (writeModel instanceof InsertOneModel) {
                TDocument document = ((InsertOneModel)writeModel).getDocument();
                if (this.getCodec() instanceof CollectibleCodec) {
                    document = ((CollectibleCodec)this.getCodec()).generateIdIfAbsentFromDocument(document);
                }
                writeRequest = new InsertRequest(this.documentToBsonDocument(document));
            }
            else if (writeModel instanceof ReplaceOneModel) {
                final ReplaceOneModel<TDocument> replaceOneModel = (ReplaceOneModel<TDocument>)(ReplaceOneModel)writeModel;
                writeRequest = new UpdateRequest(this.toBsonDocument(replaceOneModel.getFilter()), this.documentToBsonDocument(replaceOneModel.getReplacement()), WriteRequest.Type.REPLACE).upsert(replaceOneModel.getOptions().isUpsert());
            }
            else if (writeModel instanceof UpdateOneModel) {
                final UpdateOneModel<TDocument> updateOneModel = (UpdateOneModel<TDocument>)(UpdateOneModel)writeModel;
                writeRequest = new UpdateRequest(this.toBsonDocument(updateOneModel.getFilter()), this.toBsonDocument(updateOneModel.getUpdate()), WriteRequest.Type.UPDATE).multi(false).upsert(updateOneModel.getOptions().isUpsert());
            }
            else if (writeModel instanceof UpdateManyModel) {
                final UpdateManyModel<TDocument> updateManyModel = (UpdateManyModel<TDocument>)(UpdateManyModel)writeModel;
                writeRequest = new UpdateRequest(this.toBsonDocument(updateManyModel.getFilter()), this.toBsonDocument(updateManyModel.getUpdate()), WriteRequest.Type.UPDATE).multi(true).upsert(updateManyModel.getOptions().isUpsert());
            }
            else if (writeModel instanceof DeleteOneModel) {
                final DeleteOneModel<TDocument> deleteOneModel = (DeleteOneModel<TDocument>)(DeleteOneModel)writeModel;
                writeRequest = new DeleteRequest(this.toBsonDocument(deleteOneModel.getFilter())).multi(false);
            }
            else {
                if (!(writeModel instanceof DeleteManyModel)) {
                    throw new UnsupportedOperationException(String.format("WriteModel of type %s is not supported", writeModel.getClass()));
                }
                final DeleteManyModel<TDocument> deleteManyModel = (DeleteManyModel<TDocument>)(DeleteManyModel)writeModel;
                writeRequest = new DeleteRequest(this.toBsonDocument(deleteManyModel.getFilter())).multi(true);
            }
            writeRequests.add(writeRequest);
        }
        return this.executor.execute((WriteOperation<BulkWriteResult>)new MixedBulkWriteOperation(this.namespace, writeRequests, options.isOrdered(), this.writeConcern));
    }
    
    @Override
    public void insertOne(final TDocument document) {
        TDocument insertDocument = document;
        if (this.getCodec() instanceof CollectibleCodec) {
            insertDocument = ((CollectibleCodec)this.getCodec()).generateIdIfAbsentFromDocument(document);
        }
        this.executeSingleWriteRequest(new InsertRequest(this.documentToBsonDocument(insertDocument)));
    }
    
    @Override
    public void insertMany(final List<? extends TDocument> documents) {
        this.insertMany(documents, new InsertManyOptions());
    }
    
    @Override
    public void insertMany(final List<? extends TDocument> documents, final InsertManyOptions options) {
        final List<InsertRequest> requests = new ArrayList<InsertRequest>(documents.size());
        for (TDocument document : documents) {
            if (this.getCodec() instanceof CollectibleCodec) {
                document = ((CollectibleCodec)this.getCodec()).generateIdIfAbsentFromDocument(document);
            }
            requests.add(new InsertRequest(this.documentToBsonDocument(document)));
        }
        this.executor.execute((WriteOperation<Object>)new MixedBulkWriteOperation(this.namespace, requests, options.isOrdered(), this.writeConcern));
    }
    
    @Override
    public DeleteResult deleteOne(final Bson filter) {
        return this.delete(filter, false);
    }
    
    @Override
    public DeleteResult deleteMany(final Bson filter) {
        return this.delete(filter, true);
    }
    
    @Override
    public UpdateResult replaceOne(final Bson filter, final TDocument replacement) {
        return this.replaceOne(filter, replacement, new UpdateOptions());
    }
    
    @Override
    public UpdateResult replaceOne(final Bson filter, final TDocument replacement, final UpdateOptions updateOptions) {
        return this.toUpdateResult(this.executeSingleWriteRequest(new UpdateRequest(this.toBsonDocument(filter), this.documentToBsonDocument(replacement), WriteRequest.Type.REPLACE).upsert(updateOptions.isUpsert())));
    }
    
    @Override
    public UpdateResult updateOne(final Bson filter, final Bson update) {
        return this.updateOne(filter, update, new UpdateOptions());
    }
    
    @Override
    public UpdateResult updateOne(final Bson filter, final Bson update, final UpdateOptions updateOptions) {
        return this.update(filter, update, updateOptions, false);
    }
    
    @Override
    public UpdateResult updateMany(final Bson filter, final Bson update) {
        return this.updateMany(filter, update, new UpdateOptions());
    }
    
    @Override
    public UpdateResult updateMany(final Bson filter, final Bson update, final UpdateOptions updateOptions) {
        return this.update(filter, update, updateOptions, true);
    }
    
    @Override
    public TDocument findOneAndDelete(final Bson filter) {
        return this.findOneAndDelete(filter, new FindOneAndDeleteOptions());
    }
    
    @Override
    public TDocument findOneAndDelete(final Bson filter, final FindOneAndDeleteOptions options) {
        return this.executor.execute(new FindAndDeleteOperation<TDocument>(this.namespace, this.getCodec()).filter(this.toBsonDocument(filter)).projection(this.toBsonDocument(options.getProjection())).sort(this.toBsonDocument(options.getSort())).maxTime(options.getMaxTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS));
    }
    
    @Override
    public TDocument findOneAndReplace(final Bson filter, final TDocument replacement) {
        return this.findOneAndReplace(filter, replacement, new FindOneAndReplaceOptions());
    }
    
    @Override
    public TDocument findOneAndReplace(final Bson filter, final TDocument replacement, final FindOneAndReplaceOptions options) {
        return this.executor.execute(new FindAndReplaceOperation<TDocument>(this.namespace, this.getCodec(), this.documentToBsonDocument(replacement)).filter(this.toBsonDocument(filter)).projection(this.toBsonDocument(options.getProjection())).sort(this.toBsonDocument(options.getSort())).returnOriginal(options.getReturnDocument() == ReturnDocument.BEFORE).upsert(options.isUpsert()).maxTime(options.getMaxTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS));
    }
    
    @Override
    public TDocument findOneAndUpdate(final Bson filter, final Bson update) {
        return this.findOneAndUpdate(filter, update, new FindOneAndUpdateOptions());
    }
    
    @Override
    public TDocument findOneAndUpdate(final Bson filter, final Bson update, final FindOneAndUpdateOptions options) {
        return this.executor.execute(new FindAndUpdateOperation<TDocument>(this.namespace, this.getCodec(), this.toBsonDocument(update)).filter(this.toBsonDocument(filter)).projection(this.toBsonDocument(options.getProjection())).sort(this.toBsonDocument(options.getSort())).returnOriginal(options.getReturnDocument() == ReturnDocument.BEFORE).upsert(options.isUpsert()).maxTime(options.getMaxTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS));
    }
    
    @Override
    public void drop() {
        this.executor.execute((WriteOperation<Object>)new DropCollectionOperation(this.namespace));
    }
    
    @Override
    public String createIndex(final Bson keys) {
        return this.createIndex(keys, new IndexOptions());
    }
    
    @Override
    public String createIndex(final Bson keys, final IndexOptions indexOptions) {
        return this.createIndexes(Collections.singletonList(new IndexModel(keys, indexOptions))).get(0);
    }
    
    @Override
    public List<String> createIndexes(final List<IndexModel> indexes) {
        Assertions.notNull("indexes", indexes);
        final List<IndexRequest> indexRequests = new ArrayList<IndexRequest>(indexes.size());
        for (final IndexModel model : indexes) {
            indexRequests.add(new IndexRequest(this.toBsonDocument(model.getKeys())).name(model.getOptions().getName()).background(model.getOptions().isBackground()).unique(model.getOptions().isUnique()).sparse(model.getOptions().isSparse()).expireAfter(model.getOptions().getExpireAfter(TimeUnit.SECONDS), TimeUnit.SECONDS).version(model.getOptions().getVersion()).weights(this.toBsonDocument(model.getOptions().getWeights())).defaultLanguage(model.getOptions().getDefaultLanguage()).languageOverride(model.getOptions().getLanguageOverride()).textVersion(model.getOptions().getTextVersion()).sphereVersion(model.getOptions().getSphereVersion()).bits(model.getOptions().getBits()).min(model.getOptions().getMin()).max(model.getOptions().getMax()).bucketSize(model.getOptions().getBucketSize()).storageEngine(this.toBsonDocument(model.getOptions().getStorageEngine())));
        }
        final CreateIndexesOperation createIndexesOperation = new CreateIndexesOperation(this.getNamespace(), indexRequests);
        this.executor.execute((WriteOperation<Object>)createIndexesOperation);
        return createIndexesOperation.getIndexNames();
    }
    
    @Override
    public ListIndexesIterable<Document> listIndexes() {
        return this.listIndexes(Document.class);
    }
    
    @Override
    public <TResult> ListIndexesIterable<TResult> listIndexes(final Class<TResult> resultClass) {
        return new ListIndexesIterableImpl<TResult>(this.getNamespace(), resultClass, this.codecRegistry, ReadPreference.primary(), this.executor);
    }
    
    @Override
    public void dropIndex(final String indexName) {
        this.executor.execute((WriteOperation<Object>)new DropIndexOperation(this.namespace, indexName));
    }
    
    @Override
    public void dropIndex(final Bson keys) {
        this.executor.execute((WriteOperation<Object>)new DropIndexOperation(this.namespace, keys.toBsonDocument(BsonDocument.class, this.codecRegistry)));
    }
    
    @Override
    public void dropIndexes() {
        this.dropIndex("*");
    }
    
    @Override
    public void renameCollection(final MongoNamespace newCollectionNamespace) {
        this.renameCollection(newCollectionNamespace, new RenameCollectionOptions());
    }
    
    @Override
    public void renameCollection(final MongoNamespace newCollectionNamespace, final RenameCollectionOptions renameCollectionOptions) {
        this.executor.execute((WriteOperation<Object>)new RenameCollectionOperation(this.getNamespace(), newCollectionNamespace).dropTarget(renameCollectionOptions.isDropTarget()));
    }
    
    private DeleteResult delete(final Bson filter, final boolean multi) {
        final BulkWriteResult result = this.executeSingleWriteRequest(new DeleteRequest(this.toBsonDocument(filter)).multi(multi));
        if (result.wasAcknowledged()) {
            return DeleteResult.acknowledged(result.getDeletedCount());
        }
        return DeleteResult.unacknowledged();
    }
    
    private UpdateResult update(final Bson filter, final Bson update, final UpdateOptions updateOptions, final boolean multi) {
        return this.toUpdateResult(this.executeSingleWriteRequest(new UpdateRequest(this.toBsonDocument(filter), this.toBsonDocument(update), WriteRequest.Type.UPDATE).upsert(updateOptions.isUpsert()).multi(multi)));
    }
    
    private BulkWriteResult executeSingleWriteRequest(final WriteRequest request) {
        try {
            return this.executor.execute((WriteOperation<BulkWriteResult>)new MixedBulkWriteOperation(this.namespace, Arrays.asList(request), true, this.writeConcern));
        }
        catch (MongoBulkWriteException e) {
            if (e.getWriteErrors().isEmpty()) {
                throw new MongoWriteConcernException(e.getWriteConcernError(), e.getServerAddress());
            }
            throw new MongoWriteException(new WriteError(e.getWriteErrors().get(0)), e.getServerAddress());
        }
    }
    
    private UpdateResult toUpdateResult(final BulkWriteResult result) {
        if (result.wasAcknowledged()) {
            final Long modifiedCount = result.isModifiedCountAvailable() ? ((long)result.getModifiedCount()) : null;
            final BsonValue upsertedId = result.getUpserts().isEmpty() ? null : result.getUpserts().get(0).getId();
            return UpdateResult.acknowledged(result.getMatchedCount(), modifiedCount, upsertedId);
        }
        return UpdateResult.unacknowledged();
    }
    
    private Codec<TDocument> getCodec() {
        return this.codecRegistry.get(this.documentClass);
    }
    
    private BsonDocument documentToBsonDocument(final TDocument document) {
        return BsonDocumentWrapper.asBsonDocument(document, this.codecRegistry);
    }
    
    private BsonDocument toBsonDocument(final Bson bson) {
        return (bson == null) ? null : bson.toBsonDocument(this.documentClass, this.codecRegistry);
    }
}
