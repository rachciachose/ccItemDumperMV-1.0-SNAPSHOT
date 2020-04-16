// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonBoolean;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.binding.WriteBinding;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import org.bson.BsonDocument;
import org.bson.codecs.Decoder;
import com.mongodb.MongoNamespace;

public class FindAndDeleteOperation<T> implements AsyncWriteOperation<T>, WriteOperation<T>
{
    private final MongoNamespace namespace;
    private final Decoder<T> decoder;
    private BsonDocument filter;
    private BsonDocument projection;
    private BsonDocument sort;
    private long maxTimeMS;
    
    public FindAndDeleteOperation(final MongoNamespace namespace, final Decoder<T> decoder) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.decoder = Assertions.notNull("decoder", decoder);
    }
    
    public MongoNamespace getNamespace() {
        return this.namespace;
    }
    
    public Decoder<T> getDecoder() {
        return this.decoder;
    }
    
    public BsonDocument getFilter() {
        return this.filter;
    }
    
    public FindAndDeleteOperation<T> filter(final BsonDocument filter) {
        this.filter = filter;
        return this;
    }
    
    public BsonDocument getProjection() {
        return this.projection;
    }
    
    public FindAndDeleteOperation<T> projection(final BsonDocument projection) {
        this.projection = projection;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public FindAndDeleteOperation<T> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    public BsonDocument getSort() {
        return this.sort;
    }
    
    public FindAndDeleteOperation<T> sort(final BsonDocument sort) {
        this.sort = sort;
        return this;
    }
    
    @Override
    public T execute(final WriteBinding binding) {
        return CommandOperationHelper.executeWrappedCommandProtocol(binding, this.namespace.getDatabaseName(), this.getFindAndRemoveDocument(), CommandResultDocumentCodec.create(this.decoder, "value"), FindAndModifyHelper.transformer());
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<T> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, this.namespace.getDatabaseName(), this.getFindAndRemoveDocument(), CommandResultDocumentCodec.create(this.decoder, "value"), FindAndModifyHelper.transformer(), callback);
    }
    
    private BsonDocument getFindAndRemoveDocument() {
        final BsonDocument command = new BsonDocument("findandmodify", new BsonString(this.namespace.getCollectionName()));
        DocumentHelper.putIfNotNull(command, "query", this.getFilter());
        DocumentHelper.putIfNotNull(command, "fields", this.getProjection());
        DocumentHelper.putIfNotNull(command, "sort", this.getSort());
        DocumentHelper.putIfNotZero(command, "maxTimeMS", this.getMaxTime(TimeUnit.MILLISECONDS));
        command.put("remove", BsonBoolean.TRUE);
        return command;
    }
}
