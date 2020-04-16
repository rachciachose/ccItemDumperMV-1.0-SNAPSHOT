// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.connection.QueryResult;
import com.mongodb.connection.ConnectionDescription;
import org.bson.codecs.Codec;
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
import org.bson.BsonDocument;
import org.bson.codecs.Decoder;
import com.mongodb.MongoNamespace;
import com.mongodb.async.AsyncBatchCursor;

public class DistinctOperation<T> implements AsyncReadOperation<AsyncBatchCursor<T>>, ReadOperation<BatchCursor<T>>
{
    private static final String VALUES = "values";
    private final MongoNamespace namespace;
    private final String fieldName;
    private final Decoder<T> decoder;
    private BsonDocument filter;
    private long maxTimeMS;
    
    public DistinctOperation(final MongoNamespace namespace, final String fieldName, final Decoder<T> decoder) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.fieldName = Assertions.notNull("fieldName", fieldName);
        this.decoder = Assertions.notNull("decoder", decoder);
    }
    
    public BsonDocument getFilter() {
        return this.filter;
    }
    
    public DistinctOperation<T> filter(final BsonDocument filter) {
        this.filter = filter;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public DistinctOperation<T> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public BatchCursor<T> execute(final ReadBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>)new OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>() {
            @Override
            public BatchCursor<T> call(final ConnectionSource source, final Connection connection) {
                return CommandOperationHelper.executeWrappedCommandProtocol(binding, DistinctOperation.this.namespace.getDatabaseName(), DistinctOperation.this.getCommand(), DistinctOperation.this.createCommandDecoder(), connection, DistinctOperation.this.transformer(source, connection));
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
                    CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, DistinctOperation.this.namespace.getDatabaseName(), DistinctOperation.this.getCommand(), DistinctOperation.this.createCommandDecoder(), connection, DistinctOperation.this.asyncTransformer(connection.getDescription()), (SingleResultCallback<Object>)OperationHelper.releasingCallback((SingleResultCallback<T>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), source, connection));
                }
            }
        });
    }
    
    private Codec<BsonDocument> createCommandDecoder() {
        return CommandResultDocumentCodec.create(this.decoder, "values");
    }
    
    private QueryResult<T> createQueryResult(final BsonDocument result, final ConnectionDescription description) {
        return new QueryResult<T>(this.namespace, BsonDocumentWrapperHelper.toList(result, "values"), 0L, description.getServerAddress());
    }
    
    private Function<BsonDocument, BatchCursor<T>> transformer(final ConnectionSource source, final Connection connection) {
        return new Function<BsonDocument, BatchCursor<T>>() {
            @Override
            public BatchCursor<T> apply(final BsonDocument result) {
                final QueryResult<T> queryResult = (QueryResult<T>)DistinctOperation.this.createQueryResult(result, connection.getDescription());
                return new QueryBatchCursor<T>(queryResult, 0, 0, DistinctOperation.this.decoder, source);
            }
        };
    }
    
    private Function<BsonDocument, AsyncBatchCursor<T>> asyncTransformer(final ConnectionDescription connectionDescription) {
        return new Function<BsonDocument, AsyncBatchCursor<T>>() {
            @Override
            public AsyncBatchCursor<T> apply(final BsonDocument result) {
                final QueryResult<T> queryResult = (QueryResult<T>)DistinctOperation.this.createQueryResult(result, connectionDescription);
                return new AsyncQueryBatchCursor<T>(queryResult, 0, 0, null);
            }
        };
    }
    
    private BsonDocument getCommand() {
        final BsonDocument cmd = new BsonDocument("distinct", new BsonString(this.namespace.getCollectionName()));
        cmd.put("key", new BsonString(this.fieldName));
        DocumentHelper.putIfNotNull(cmd, "query", this.filter);
        DocumentHelper.putIfNotZero(cmd, "maxTimeMS", this.maxTimeMS);
        return cmd;
    }
}
