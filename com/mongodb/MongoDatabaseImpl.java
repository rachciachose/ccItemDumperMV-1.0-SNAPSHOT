// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.operation.CreateCollectionOperation;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.ListCollectionsIterable;
import org.bson.BsonDocument;
import com.mongodb.client.MongoIterable;
import com.mongodb.operation.WriteOperation;
import com.mongodb.operation.DropDatabaseOperation;
import com.mongodb.operation.ReadOperation;
import org.bson.codecs.Decoder;
import com.mongodb.operation.CommandReadOperation;
import org.bson.conversions.Bson;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.assertions.Assertions;
import com.mongodb.operation.OperationExecutor;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.MongoDatabase;

class MongoDatabaseImpl implements MongoDatabase
{
    private final String name;
    private final ReadPreference readPreference;
    private final CodecRegistry codecRegistry;
    private final WriteConcern writeConcern;
    private final OperationExecutor executor;
    
    MongoDatabaseImpl(final String name, final CodecRegistry codecRegistry, final ReadPreference readPreference, final WriteConcern writeConcern, final OperationExecutor executor) {
        this.name = Assertions.notNull("name", name);
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
        this.writeConcern = Assertions.notNull("writeConcern", writeConcern);
        this.executor = Assertions.notNull("executor", executor);
    }
    
    @Override
    public String getName() {
        return this.name;
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
    public MongoDatabase withCodecRegistry(final CodecRegistry codecRegistry) {
        return new MongoDatabaseImpl(this.name, codecRegistry, this.readPreference, this.writeConcern, this.executor);
    }
    
    @Override
    public MongoDatabase withReadPreference(final ReadPreference readPreference) {
        return new MongoDatabaseImpl(this.name, this.codecRegistry, readPreference, this.writeConcern, this.executor);
    }
    
    @Override
    public MongoDatabase withWriteConcern(final WriteConcern writeConcern) {
        return new MongoDatabaseImpl(this.name, this.codecRegistry, this.readPreference, writeConcern, this.executor);
    }
    
    @Override
    public MongoCollection<Document> getCollection(final String collectionName) {
        return this.getCollection(collectionName, Document.class);
    }
    
    @Override
    public <TDocument> MongoCollection<TDocument> getCollection(final String collectionName, final Class<TDocument> documentClass) {
        return new MongoCollectionImpl<TDocument>(new MongoNamespace(this.name, collectionName), documentClass, this.codecRegistry, this.readPreference, this.writeConcern, this.executor);
    }
    
    @Override
    public Document runCommand(final Bson command) {
        return this.runCommand(command, Document.class);
    }
    
    @Override
    public Document runCommand(final Bson command, final ReadPreference readPreference) {
        return this.runCommand(command, readPreference, Document.class);
    }
    
    @Override
    public <TResult> TResult runCommand(final Bson command, final Class<TResult> resultClass) {
        return this.runCommand(command, ReadPreference.primary(), resultClass);
    }
    
    @Override
    public <TResult> TResult runCommand(final Bson command, final ReadPreference readPreference, final Class<TResult> resultClass) {
        Assertions.notNull("readPreference", readPreference);
        return this.executor.execute(new CommandReadOperation<TResult>(this.getName(), this.toBsonDocument(command), this.codecRegistry.get(resultClass)), readPreference);
    }
    
    @Override
    public void drop() {
        this.executor.execute((WriteOperation<Object>)new DropDatabaseOperation(this.name));
    }
    
    @Override
    public MongoIterable<String> listCollectionNames() {
        return new ListCollectionsIterableImpl<BsonDocument>(this.name, BsonDocument.class, MongoClient.getDefaultCodecRegistry(), ReadPreference.primary(), this.executor).map((Function<BsonDocument, String>)new Function<BsonDocument, String>() {
            @Override
            public String apply(final BsonDocument result) {
                return result.getString("name").getValue();
            }
        });
    }
    
    @Override
    public ListCollectionsIterable<Document> listCollections() {
        return this.listCollections(Document.class);
    }
    
    @Override
    public <TResult> ListCollectionsIterable<TResult> listCollections(final Class<TResult> resultClass) {
        return new ListCollectionsIterableImpl<TResult>(this.name, resultClass, this.codecRegistry, ReadPreference.primary(), this.executor);
    }
    
    @Override
    public void createCollection(final String collectionName) {
        this.createCollection(collectionName, new CreateCollectionOptions());
    }
    
    @Override
    public void createCollection(final String collectionName, final CreateCollectionOptions createCollectionOptions) {
        this.executor.execute((WriteOperation<Object>)new CreateCollectionOperation(this.name, collectionName).capped(createCollectionOptions.isCapped()).sizeInBytes(createCollectionOptions.getSizeInBytes()).autoIndex(createCollectionOptions.isAutoIndex()).maxDocuments(createCollectionOptions.getMaxDocuments()).usePowerOf2Sizes(createCollectionOptions.isUsePowerOf2Sizes()).storageEngineOptions(this.toBsonDocument(createCollectionOptions.getStorageEngineOptions())));
    }
    
    private BsonDocument toBsonDocument(final Bson document) {
        return (document == null) ? null : document.toBsonDocument(BsonDocument.class, this.codecRegistry);
    }
}
