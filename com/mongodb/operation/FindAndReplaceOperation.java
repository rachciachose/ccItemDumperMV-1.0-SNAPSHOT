// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import java.util.Map;
import com.mongodb.internal.validator.MappedFieldNameValidator;
import com.mongodb.internal.validator.NoOpFieldNameValidator;
import com.mongodb.internal.validator.CollectibleDocumentFieldNameValidator;
import java.util.HashMap;
import org.bson.FieldNameValidator;
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

public class FindAndReplaceOperation<T> implements AsyncWriteOperation<T>, WriteOperation<T>
{
    private final MongoNamespace namespace;
    private final Decoder<T> decoder;
    private final BsonDocument replacement;
    private BsonDocument filter;
    private BsonDocument projection;
    private BsonDocument sort;
    private long maxTimeMS;
    private boolean returnOriginal;
    private boolean upsert;
    
    public FindAndReplaceOperation(final MongoNamespace namespace, final Decoder<T> decoder, final BsonDocument replacement) {
        this.returnOriginal = true;
        this.namespace = Assertions.notNull("namespace", namespace);
        this.decoder = Assertions.notNull("decoder", decoder);
        this.replacement = Assertions.notNull("replacement", replacement);
    }
    
    public MongoNamespace getNamespace() {
        return this.namespace;
    }
    
    public Decoder<T> getDecoder() {
        return this.decoder;
    }
    
    public BsonDocument getReplacement() {
        return this.replacement;
    }
    
    public BsonDocument getFilter() {
        return this.filter;
    }
    
    public FindAndReplaceOperation<T> filter(final BsonDocument filter) {
        this.filter = filter;
        return this;
    }
    
    public BsonDocument getProjection() {
        return this.projection;
    }
    
    public FindAndReplaceOperation<T> projection(final BsonDocument projection) {
        this.projection = projection;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public FindAndReplaceOperation<T> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    public BsonDocument getSort() {
        return this.sort;
    }
    
    public FindAndReplaceOperation<T> sort(final BsonDocument sort) {
        this.sort = sort;
        return this;
    }
    
    public boolean isReturnOriginal() {
        return this.returnOriginal;
    }
    
    public FindAndReplaceOperation<T> returnOriginal(final boolean returnOriginal) {
        this.returnOriginal = returnOriginal;
        return this;
    }
    
    public boolean isUpsert() {
        return this.upsert;
    }
    
    public FindAndReplaceOperation<T> upsert(final boolean upsert) {
        this.upsert = upsert;
        return this;
    }
    
    @Override
    public T execute(final WriteBinding binding) {
        return CommandOperationHelper.executeWrappedCommandProtocol(binding, this.namespace.getDatabaseName(), this.getCommand(), this.getValidator(), CommandResultDocumentCodec.create(this.decoder, "value"), FindAndModifyHelper.transformer());
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<T> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, this.namespace.getDatabaseName(), this.getCommand(), this.getValidator(), CommandResultDocumentCodec.create(this.decoder, "value"), FindAndModifyHelper.transformer(), callback);
    }
    
    private BsonDocument getCommand() {
        final BsonDocument command = new BsonDocument("findandmodify", new BsonString(this.namespace.getCollectionName()));
        DocumentHelper.putIfNotNull(command, "query", this.getFilter());
        DocumentHelper.putIfNotNull(command, "fields", this.getProjection());
        DocumentHelper.putIfNotNull(command, "sort", this.getSort());
        DocumentHelper.putIfTrue(command, "new", !this.isReturnOriginal());
        DocumentHelper.putIfTrue(command, "upsert", this.isUpsert());
        DocumentHelper.putIfNotZero(command, "maxTimeMS", this.getMaxTime(TimeUnit.MILLISECONDS));
        command.put("update", this.getReplacement());
        return command;
    }
    
    private FieldNameValidator getValidator() {
        final Map<String, FieldNameValidator> map = new HashMap<String, FieldNameValidator>();
        map.put("update", new CollectibleDocumentFieldNameValidator());
        return new MappedFieldNameValidator(new NoOpFieldNameValidator(), map);
    }
}
