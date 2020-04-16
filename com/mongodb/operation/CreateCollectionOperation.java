// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonBoolean;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.Function;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import org.bson.codecs.Decoder;
import org.bson.codecs.BsonDocumentCodec;
import com.mongodb.binding.WriteBinding;
import com.mongodb.assertions.Assertions;
import org.bson.BsonDocument;

public class CreateCollectionOperation implements AsyncWriteOperation<Void>, WriteOperation<Void>
{
    private final String databaseName;
    private final String collectionName;
    private boolean capped;
    private long sizeInBytes;
    private boolean autoIndex;
    private long maxDocuments;
    private Boolean usePowerOf2Sizes;
    private BsonDocument storageEngineOptions;
    
    public CreateCollectionOperation(final String databaseName, final String collectionName) {
        this.capped = false;
        this.sizeInBytes = 0L;
        this.autoIndex = true;
        this.maxDocuments = 0L;
        this.usePowerOf2Sizes = null;
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.collectionName = Assertions.notNull("collectionName", collectionName);
    }
    
    public String getCollectionName() {
        return this.collectionName;
    }
    
    public boolean isAutoIndex() {
        return this.autoIndex;
    }
    
    public CreateCollectionOperation autoIndex(final boolean autoIndex) {
        this.autoIndex = autoIndex;
        return this;
    }
    
    public long getMaxDocuments() {
        return this.maxDocuments;
    }
    
    public CreateCollectionOperation maxDocuments(final long maxDocuments) {
        this.maxDocuments = maxDocuments;
        return this;
    }
    
    public boolean isCapped() {
        return this.capped;
    }
    
    public CreateCollectionOperation capped(final boolean capped) {
        this.capped = capped;
        return this;
    }
    
    public long getSizeInBytes() {
        return this.sizeInBytes;
    }
    
    public CreateCollectionOperation sizeInBytes(final long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
        return this;
    }
    
    public Boolean isUsePowerOf2Sizes() {
        return this.usePowerOf2Sizes;
    }
    
    public CreateCollectionOperation usePowerOf2Sizes(final Boolean usePowerOf2Sizes) {
        this.usePowerOf2Sizes = usePowerOf2Sizes;
        return this;
    }
    
    public BsonDocument getStorageEngineOptions() {
        return this.storageEngineOptions;
    }
    
    public CreateCollectionOperation storageEngineOptions(final BsonDocument storageEngineOptions) {
        this.storageEngineOptions = storageEngineOptions;
        return this;
    }
    
    @Override
    public Void execute(final WriteBinding binding) {
        CommandOperationHelper.executeWrappedCommandProtocol(binding, this.databaseName, this.asDocument(), (Decoder<Object>)new BsonDocumentCodec());
        return null;
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<Void> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, this.databaseName, this.asDocument(), (Decoder<Object>)new BsonDocumentCodec(), new OperationHelper.VoidTransformer<Object>(), callback);
    }
    
    private BsonDocument asDocument() {
        final BsonDocument document = new BsonDocument("create", new BsonString(this.collectionName));
        document.put("capped", BsonBoolean.valueOf(this.capped));
        if (this.capped) {
            DocumentHelper.putIfNotZero(document, "size", this.sizeInBytes);
            document.put("autoIndexId", BsonBoolean.valueOf(this.autoIndex));
            DocumentHelper.putIfNotZero(document, "max", this.maxDocuments);
        }
        if (this.usePowerOf2Sizes != null) {
            document.put("usePowerOfTwoSizes", BsonBoolean.valueOf(this.usePowerOf2Sizes));
        }
        if (this.storageEngineOptions != null) {
            document.put("storageEngine", this.storageEngineOptions);
        }
        return document;
    }
}
