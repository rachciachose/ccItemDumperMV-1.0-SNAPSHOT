// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonBoolean;
import org.bson.BsonInt64;
import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.Function;
import org.bson.codecs.Decoder;
import org.bson.codecs.BsonDocumentCodec;
import com.mongodb.binding.WriteBinding;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import org.bson.BsonDocument;
import java.util.List;
import com.mongodb.MongoNamespace;

public class AggregateToCollectionOperation implements AsyncWriteOperation<Void>, WriteOperation<Void>
{
    private final MongoNamespace namespace;
    private final List<BsonDocument> pipeline;
    private Boolean allowDiskUse;
    private long maxTimeMS;
    
    public AggregateToCollectionOperation(final MongoNamespace namespace, final List<BsonDocument> pipeline) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.pipeline = Assertions.notNull("pipeline", pipeline);
        Assertions.isTrueArgument("pipeline is empty", !pipeline.isEmpty());
        Assertions.isTrueArgument("last stage of pipeline does not contain an output collection", pipeline.get(pipeline.size() - 1).get("$out") != null);
    }
    
    public List<BsonDocument> getPipeline() {
        return this.pipeline;
    }
    
    public Boolean getAllowDiskUse() {
        return this.allowDiskUse;
    }
    
    public AggregateToCollectionOperation allowDiskUse(final Boolean allowDiskUse) {
        this.allowDiskUse = allowDiskUse;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public AggregateToCollectionOperation maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public Void execute(final WriteBinding binding) {
        CommandOperationHelper.executeWrappedCommandProtocol(binding, this.namespace.getDatabaseName(), this.getCommand(), (Decoder<Object>)new BsonDocumentCodec(), (Function<Object, Object>)new OperationHelper.VoidTransformer());
        return null;
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<Void> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, this.namespace.getDatabaseName(), this.getCommand(), (Decoder<Object>)new BsonDocumentCodec(), new OperationHelper.VoidTransformer<Object>(), callback);
    }
    
    private BsonDocument getCommand() {
        final BsonDocument commandDocument = new BsonDocument("aggregate", new BsonString(this.namespace.getCollectionName()));
        commandDocument.put("pipeline", new BsonArray(this.pipeline));
        if (this.maxTimeMS > 0L) {
            commandDocument.put("maxTimeMS", new BsonInt64(this.maxTimeMS));
        }
        if (this.allowDiskUse != null) {
            commandDocument.put("allowDiskUse", BsonBoolean.valueOf(this.allowDiskUse));
        }
        return commandDocument;
    }
}
