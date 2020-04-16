// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.connection.QueryResult;
import org.bson.BsonBoolean;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.connection.ConnectionDescription;
import com.mongodb.ExplainVerbosity;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.binding.AsyncConnectionSource;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncReadBinding;
import com.mongodb.Function;
import com.mongodb.connection.Connection;
import com.mongodb.binding.ConnectionSource;
import com.mongodb.binding.ReadBinding;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.Decoder;
import org.bson.BsonDocument;
import java.util.List;
import com.mongodb.MongoNamespace;
import com.mongodb.async.AsyncBatchCursor;

public class AggregateOperation<T> implements AsyncReadOperation<AsyncBatchCursor<T>>, ReadOperation<BatchCursor<T>>
{
    private static final String RESULT = "result";
    private static final String FIRST_BATCH = "firstBatch";
    private final MongoNamespace namespace;
    private final List<BsonDocument> pipeline;
    private final Decoder<T> decoder;
    private Boolean allowDiskUse;
    private Integer batchSize;
    private long maxTimeMS;
    private Boolean useCursor;
    
    public AggregateOperation(final MongoNamespace namespace, final List<BsonDocument> pipeline, final Decoder<T> decoder) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.pipeline = Assertions.notNull("pipeline", pipeline);
        this.decoder = Assertions.notNull("decoder", decoder);
    }
    
    public List<BsonDocument> getPipeline() {
        return this.pipeline;
    }
    
    public Boolean getAllowDiskUse() {
        return this.allowDiskUse;
    }
    
    public AggregateOperation<T> allowDiskUse(final Boolean allowDiskUse) {
        this.allowDiskUse = allowDiskUse;
        return this;
    }
    
    public Integer getBatchSize() {
        return this.batchSize;
    }
    
    public AggregateOperation<T> batchSize(final Integer batchSize) {
        this.batchSize = batchSize;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public AggregateOperation<T> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    public Boolean getUseCursor() {
        return this.useCursor;
    }
    
    public AggregateOperation<T> useCursor(final Boolean useCursor) {
        this.useCursor = useCursor;
        return this;
    }
    
    @Override
    public BatchCursor<T> execute(final ReadBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>)new OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>() {
            @Override
            public BatchCursor<T> call(final ConnectionSource source, final Connection connection) {
                return CommandOperationHelper.executeWrappedCommandProtocol(binding, AggregateOperation.this.namespace.getDatabaseName(), AggregateOperation.this.asCommandDocument(connection.getDescription()), CommandResultDocumentCodec.create((Decoder<Object>)AggregateOperation.this.decoder, AggregateOperation.this.getFieldNameWithResults(connection.getDescription())), connection, AggregateOperation.this.transformer(source, connection));
            }
        });
    }
    
    @Override
    public void executeAsync(final AsyncReadBinding binding, final SingleResultCallback<AsyncBatchCursor<T>> callback) {
        OperationHelper.withConnection(binding, new OperationHelper.AsyncCallableWithConnectionAndSource() {
            @Override
            public void call(final AsyncConnectionSource source, final AsyncConnection connection, final Throwable t) {
                if (t != null) {
                    ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<Object>)callback).onResult(null, t);
                }
                else {
                    CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, AggregateOperation.this.namespace.getDatabaseName(), AggregateOperation.this.asCommandDocument(connection.getDescription()), CommandResultDocumentCodec.create((Decoder<Object>)AggregateOperation.this.decoder, AggregateOperation.this.getFieldNameWithResults(connection.getDescription())), connection, AggregateOperation.this.asyncTransformer(source, connection), (SingleResultCallback<Object>)OperationHelper.releasingCallback((SingleResultCallback<T>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), source, connection));
                }
            }
        });
    }
    
    public ReadOperation<BsonDocument> asExplainableOperation(final ExplainVerbosity explainVerbosity) {
        return new AggregateExplainOperation(this.namespace, this.pipeline).allowDiskUse(this.allowDiskUse).maxTime(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public AsyncReadOperation<BsonDocument> asExplainableOperationAsync(final ExplainVerbosity explainVerbosity) {
        return new AggregateExplainOperation(this.namespace, this.pipeline).allowDiskUse(this.allowDiskUse).maxTime(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    private boolean isInline(final ConnectionDescription description) {
        return (this.useCursor != null && !this.useCursor) || !OperationHelper.serverIsAtLeastVersionTwoDotSix(description);
    }
    
    private BsonDocument asCommandDocument(final ConnectionDescription description) {
        final BsonDocument commandDocument = new BsonDocument("aggregate", new BsonString(this.namespace.getCollectionName()));
        commandDocument.put("pipeline", new BsonArray(this.pipeline));
        if (this.maxTimeMS > 0L) {
            commandDocument.put("maxTimeMS", new BsonInt64(this.maxTimeMS));
        }
        if ((this.useCursor == null || this.useCursor) && OperationHelper.serverIsAtLeastVersionTwoDotSix(description)) {
            final BsonDocument cursor = new BsonDocument();
            if (this.batchSize != null) {
                cursor.put("batchSize", new BsonInt32(this.batchSize));
            }
            commandDocument.put("cursor", cursor);
        }
        if (this.allowDiskUse != null) {
            commandDocument.put("allowDiskUse", BsonBoolean.valueOf(this.allowDiskUse));
        }
        return commandDocument;
    }
    
    String getFieldNameWithResults(final ConnectionDescription description) {
        return ((this.useCursor == null || this.useCursor) && OperationHelper.serverIsAtLeastVersionTwoDotSix(description)) ? "firstBatch" : "result";
    }
    
    private QueryResult<T> createQueryResult(final BsonDocument result, final ConnectionDescription description) {
        if (this.isInline(description)) {
            return new QueryResult<T>(this.namespace, BsonDocumentWrapperHelper.toList(result, "result"), 0L, description.getServerAddress());
        }
        return OperationHelper.cursorDocumentToQueryResult(result.getDocument("cursor"), description.getServerAddress());
    }
    
    private Function<BsonDocument, BatchCursor<T>> transformer(final ConnectionSource source, final Connection connection) {
        return new Function<BsonDocument, BatchCursor<T>>() {
            @Override
            public BatchCursor<T> apply(final BsonDocument result) {
                final QueryResult<T> queryResult = (QueryResult<T>)AggregateOperation.this.createQueryResult(result, connection.getDescription());
                return new QueryBatchCursor<T>(queryResult, 0, (AggregateOperation.this.batchSize != null) ? AggregateOperation.this.batchSize : 0, AggregateOperation.this.decoder, source);
            }
        };
    }
    
    private Function<BsonDocument, AsyncBatchCursor<T>> asyncTransformer(final AsyncConnectionSource source, final AsyncConnection connection) {
        return new Function<BsonDocument, AsyncBatchCursor<T>>() {
            @Override
            public AsyncBatchCursor<T> apply(final BsonDocument result) {
                final QueryResult<T> queryResult = (QueryResult<T>)AggregateOperation.this.createQueryResult(result, connection.getDescription());
                return new AsyncQueryBatchCursor<T>(queryResult, 0, (AggregateOperation.this.batchSize != null) ? AggregateOperation.this.batchSize : 0, AggregateOperation.this.decoder, source, connection);
            }
        };
    }
}
