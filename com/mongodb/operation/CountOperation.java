// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonString;
import com.mongodb.Function;
import com.mongodb.ExplainVerbosity;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncReadBinding;
import org.bson.codecs.Decoder;
import org.bson.codecs.BsonDocumentCodec;
import com.mongodb.binding.ReadBinding;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import org.bson.BsonValue;
import org.bson.BsonDocument;
import com.mongodb.MongoNamespace;

public class CountOperation implements AsyncReadOperation<Long>, ReadOperation<Long>
{
    private final MongoNamespace namespace;
    private BsonDocument filter;
    private BsonValue hint;
    private long skip;
    private long limit;
    private long maxTimeMS;
    
    public CountOperation(final MongoNamespace namespace) {
        this.namespace = Assertions.notNull("namespace", namespace);
    }
    
    public BsonDocument getFilter() {
        return this.filter;
    }
    
    public CountOperation filter(final BsonDocument filter) {
        this.filter = filter;
        return this;
    }
    
    public BsonValue getHint() {
        return this.hint;
    }
    
    public CountOperation hint(final BsonValue hint) {
        this.hint = hint;
        return this;
    }
    
    public long getLimit() {
        return this.limit;
    }
    
    public CountOperation limit(final long limit) {
        this.limit = limit;
        return this;
    }
    
    public long getSkip() {
        return this.skip;
    }
    
    public CountOperation skip(final long skip) {
        this.skip = skip;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public CountOperation maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public Long execute(final ReadBinding binding) {
        return CommandOperationHelper.executeWrappedCommandProtocol(binding, this.namespace.getDatabaseName(), this.getCommand(), new BsonDocumentCodec(), this.transformer());
    }
    
    @Override
    public void executeAsync(final AsyncReadBinding binding, final SingleResultCallback<Long> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, this.namespace.getDatabaseName(), this.getCommand(), new BsonDocumentCodec(), this.transformer(), callback);
    }
    
    public ReadOperation<BsonDocument> asExplainableOperation(final ExplainVerbosity explainVerbosity) {
        return this.createExplainableOperation(explainVerbosity);
    }
    
    public AsyncReadOperation<BsonDocument> asExplainableOperationAsync(final ExplainVerbosity explainVerbosity) {
        return this.createExplainableOperation(explainVerbosity);
    }
    
    private CommandReadOperation<BsonDocument> createExplainableOperation(final ExplainVerbosity explainVerbosity) {
        return new CommandReadOperation<BsonDocument>(this.namespace.getDatabaseName(), ExplainHelper.asExplainCommand(this.getCommand(), explainVerbosity), new BsonDocumentCodec());
    }
    
    private Function<BsonDocument, Long> transformer() {
        return new Function<BsonDocument, Long>() {
            @Override
            public Long apply(final BsonDocument result) {
                return result.getNumber("n").longValue();
            }
        };
    }
    
    private BsonDocument getCommand() {
        final BsonDocument document = new BsonDocument("count", new BsonString(this.namespace.getCollectionName()));
        DocumentHelper.putIfNotNull(document, "query", this.filter);
        DocumentHelper.putIfNotZero(document, "limit", this.limit);
        DocumentHelper.putIfNotZero(document, "skip", this.skip);
        DocumentHelper.putIfNotNull(document, "hint", this.hint);
        DocumentHelper.putIfNotZero(document, "maxTimeMS", this.maxTimeMS);
        return document;
    }
}
