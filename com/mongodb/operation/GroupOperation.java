// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.connection.ConnectionDescription;
import com.mongodb.connection.QueryResult;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncReadBinding;
import com.mongodb.Function;
import com.mongodb.connection.Connection;
import com.mongodb.binding.ConnectionSource;
import com.mongodb.binding.ReadBinding;
import com.mongodb.assertions.Assertions;
import org.bson.BsonDocument;
import org.bson.BsonJavaScript;
import org.bson.codecs.Decoder;
import com.mongodb.MongoNamespace;
import com.mongodb.async.AsyncBatchCursor;

public class GroupOperation<T> implements AsyncReadOperation<AsyncBatchCursor<T>>, ReadOperation<BatchCursor<T>>
{
    private final MongoNamespace namespace;
    private final Decoder<T> decoder;
    private final BsonJavaScript reduceFunction;
    private final BsonDocument initial;
    private BsonDocument key;
    private BsonJavaScript keyFunction;
    private BsonDocument filter;
    private BsonJavaScript finalizeFunction;
    
    public GroupOperation(final MongoNamespace namespace, final BsonJavaScript reduceFunction, final BsonDocument initial, final Decoder<T> decoder) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.reduceFunction = Assertions.notNull("reduceFunction", reduceFunction);
        this.initial = Assertions.notNull("initial", initial);
        this.decoder = Assertions.notNull("decoder", decoder);
    }
    
    public BsonDocument getKey() {
        return this.key;
    }
    
    public GroupOperation<T> key(final BsonDocument key) {
        this.key = key;
        return this;
    }
    
    public BsonJavaScript getKeyFunction() {
        return this.keyFunction;
    }
    
    public GroupOperation<T> keyFunction(final BsonJavaScript keyFunction) {
        this.keyFunction = keyFunction;
        return this;
    }
    
    public BsonDocument getInitial() {
        return this.initial;
    }
    
    public BsonJavaScript getReduceFunction() {
        return this.reduceFunction;
    }
    
    public BsonDocument getFilter() {
        return this.filter;
    }
    
    public GroupOperation<T> filter(final BsonDocument filter) {
        this.filter = filter;
        return this;
    }
    
    public BsonJavaScript getFinalizeFunction() {
        return this.finalizeFunction;
    }
    
    public GroupOperation<T> finalizeFunction(final BsonJavaScript finalizeFunction) {
        this.finalizeFunction = finalizeFunction;
        return this;
    }
    
    @Override
    public BatchCursor<T> execute(final ReadBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>)new OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>() {
            @Override
            public BatchCursor<T> call(final ConnectionSource connectionSource, final Connection connection) {
                return CommandOperationHelper.executeWrappedCommandProtocol(binding, GroupOperation.this.namespace.getDatabaseName(), GroupOperation.this.getCommand(), CommandResultDocumentCodec.create((Decoder<Object>)GroupOperation.this.decoder, "retval"), connection, GroupOperation.this.transformer(connectionSource, connection));
            }
        });
    }
    
    @Override
    public void executeAsync(final AsyncReadBinding binding, final SingleResultCallback<AsyncBatchCursor<T>> callback) {
        OperationHelper.withConnection(binding, new OperationHelper.AsyncCallableWithConnection() {
            @Override
            public void call(final AsyncConnection connection, final Throwable t) {
                if (t != null) {
                    ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<Object>)callback).onResult(null, t);
                }
                else {
                    CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, GroupOperation.this.namespace.getDatabaseName(), GroupOperation.this.getCommand(), CommandResultDocumentCodec.create((Decoder<Object>)GroupOperation.this.decoder, "retval"), connection, GroupOperation.this.asyncTransformer(connection), (SingleResultCallback<Object>)OperationHelper.releasingCallback((SingleResultCallback<T>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), connection));
                }
            }
        });
    }
    
    private BsonDocument getCommand() {
        final BsonDocument document = new BsonDocument("ns", new BsonString(this.namespace.getCollectionName()));
        if (this.getKey() != null) {
            document.put("key", this.getKey());
        }
        else if (this.getKeyFunction() != null) {
            document.put("$keyf", this.getKeyFunction());
        }
        document.put("initial", this.getInitial());
        document.put("$reduce", this.getReduceFunction());
        if (this.getFinalizeFunction() != null) {
            document.put("finalize", this.getFinalizeFunction());
        }
        if (this.getFilter() != null) {
            document.put("cond", this.getFilter());
        }
        return new BsonDocument("group", document);
    }
    
    private Function<BsonDocument, BatchCursor<T>> transformer(final ConnectionSource source, final Connection connection) {
        return new Function<BsonDocument, BatchCursor<T>>() {
            @Override
            public BatchCursor<T> apply(final BsonDocument result) {
                return new QueryBatchCursor<T>(GroupOperation.this.createQueryResult(result, connection.getDescription()), 0, 0, GroupOperation.this.decoder, source);
            }
        };
    }
    
    private Function<BsonDocument, AsyncBatchCursor<T>> asyncTransformer(final AsyncConnection connection) {
        return new Function<BsonDocument, AsyncBatchCursor<T>>() {
            @Override
            public AsyncBatchCursor<T> apply(final BsonDocument result) {
                return new AsyncQueryBatchCursor<T>(GroupOperation.this.createQueryResult(result, connection.getDescription()), 0, 0, GroupOperation.this.decoder);
            }
        };
    }
    
    private QueryResult<T> createQueryResult(final BsonDocument result, final ConnectionDescription description) {
        return new QueryResult<T>(this.namespace, BsonDocumentWrapperHelper.toList(result, "retval"), 0L, description.getServerAddress());
    }
}
