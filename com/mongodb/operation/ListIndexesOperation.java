// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.codecs.Codec;
import com.mongodb.connection.ServerType;
import org.bson.BsonInt64;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.ReadPreference;
import com.mongodb.connection.ConnectionDescription;
import com.mongodb.connection.QueryResult;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.binding.AsyncConnectionSource;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncReadBinding;
import com.mongodb.MongoCommandException;
import com.mongodb.Function;
import org.bson.BsonDocument;
import com.mongodb.connection.Connection;
import com.mongodb.binding.ConnectionSource;
import com.mongodb.binding.ReadBinding;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.Decoder;
import com.mongodb.MongoNamespace;
import com.mongodb.async.AsyncBatchCursor;

public class ListIndexesOperation<T> implements AsyncReadOperation<AsyncBatchCursor<T>>, ReadOperation<BatchCursor<T>>
{
    private final MongoNamespace namespace;
    private final Decoder<T> decoder;
    private int batchSize;
    private long maxTimeMS;
    
    public ListIndexesOperation(final MongoNamespace namespace, final Decoder<T> decoder) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.decoder = Assertions.notNull("decoder", decoder);
    }
    
    public Integer getBatchSize() {
        return this.batchSize;
    }
    
    public ListIndexesOperation<T> batchSize(final int batchSize) {
        this.batchSize = batchSize;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public ListIndexesOperation<T> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public BatchCursor<T> execute(final ReadBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>)new OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>() {
            @Override
            public BatchCursor<T> call(final ConnectionSource source, final Connection connection) {
                if (OperationHelper.serverIsAtLeastVersionThreeDotZero(connection.getDescription())) {
                    try {
                        return CommandOperationHelper.executeWrappedCommandProtocol(binding, ListIndexesOperation.this.namespace.getDatabaseName(), ListIndexesOperation.this.getCommand(), ListIndexesOperation.this.createCommandDecoder(), connection, ListIndexesOperation.this.transformer(source));
                    }
                    catch (MongoCommandException e) {
                        return CommandOperationHelper.rethrowIfNotNamespaceError(e, (BatchCursor<T>)OperationHelper.createEmptyBatchCursor(ListIndexesOperation.this.namespace, (Decoder<Object>)ListIndexesOperation.this.decoder, source.getServerDescription().getAddress(), ListIndexesOperation.this.batchSize));
                    }
                }
                return new QueryBatchCursor<T>(connection.query(ListIndexesOperation.this.getIndexNamespace(), ListIndexesOperation.this.asQueryDocument(connection.getDescription(), binding.getReadPreference()), null, 0, 0, ListIndexesOperation.this.batchSize, binding.getReadPreference().isSlaveOk(), false, false, false, false, false, ListIndexesOperation.this.decoder), 0, ListIndexesOperation.this.batchSize, ListIndexesOperation.this.decoder, source);
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
                    final SingleResultCallback<AsyncBatchCursor<T>> wrappedCallback = OperationHelper.releasingCallback((SingleResultCallback<AsyncBatchCursor<T>>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), source, connection);
                    if (OperationHelper.serverIsAtLeastVersionThreeDotZero(connection.getDescription())) {
                        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, ListIndexesOperation.this.namespace.getDatabaseName(), ListIndexesOperation.this.getCommand(), ListIndexesOperation.this.createCommandDecoder(), connection, ListIndexesOperation.this.asyncTransformer(source, connection), (SingleResultCallback<Object>)new SingleResultCallback<AsyncBatchCursor<T>>() {
                            @Override
                            public void onResult(final AsyncBatchCursor<T> result, final Throwable t) {
                                if (t != null && !CommandOperationHelper.isNamespaceError(t)) {
                                    wrappedCallback.onResult(null, t);
                                }
                                else {
                                    wrappedCallback.onResult((result != null) ? result : ListIndexesOperation.this.emptyAsyncCursor(source), null);
                                }
                            }
                        });
                    }
                    else {
                        connection.queryAsync(ListIndexesOperation.this.getIndexNamespace(), ListIndexesOperation.this.asQueryDocument(connection.getDescription(), binding.getReadPreference()), null, 0, 0, ListIndexesOperation.this.batchSize, binding.getReadPreference().isSlaveOk(), false, false, false, false, false, ListIndexesOperation.this.decoder, (SingleResultCallback<QueryResult<Object>>)new SingleResultCallback<QueryResult<T>>() {
                            @Override
                            public void onResult(final QueryResult<T> result, final Throwable t) {
                                if (t != null) {
                                    wrappedCallback.onResult(null, t);
                                }
                                else {
                                    wrappedCallback.onResult(new AsyncQueryBatchCursor<T>(result, 0, ListIndexesOperation.this.batchSize, ListIndexesOperation.this.decoder, source, connection), null);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    
    private AsyncBatchCursor<T> emptyAsyncCursor(final AsyncConnectionSource source) {
        return OperationHelper.createEmptyAsyncBatchCursor(this.namespace, this.decoder, source.getServerDescription().getAddress(), this.batchSize);
    }
    
    private BsonDocument asQueryDocument(final ConnectionDescription connectionDescription, final ReadPreference readPreference) {
        final BsonDocument document = new BsonDocument("$query", new BsonDocument("ns", new BsonString(this.namespace.getFullName())));
        if (this.maxTimeMS > 0L) {
            document.put("$maxTimeMS", new BsonInt64(this.maxTimeMS));
        }
        if (connectionDescription.getServerType() == ServerType.SHARD_ROUTER && !readPreference.equals(ReadPreference.primary())) {
            document.put("$readPreference", readPreference.toDocument());
        }
        return document;
    }
    
    private MongoNamespace getIndexNamespace() {
        return new MongoNamespace(this.namespace.getDatabaseName(), "system.indexes");
    }
    
    private BsonDocument getCommand() {
        final BsonDocument command = new BsonDocument("listIndexes", new BsonString(this.namespace.getCollectionName())).append("cursor", CursorHelper.getCursorDocumentFromBatchSize(this.batchSize));
        if (this.maxTimeMS > 0L) {
            command.put("maxTimeMS", new BsonInt64(this.maxTimeMS));
        }
        return command;
    }
    
    private Function<BsonDocument, BatchCursor<T>> transformer(final ConnectionSource source) {
        return new Function<BsonDocument, BatchCursor<T>>() {
            @Override
            public BatchCursor<T> apply(final BsonDocument result) {
                return OperationHelper.cursorDocumentToBatchCursor(result.getDocument("cursor"), ListIndexesOperation.this.decoder, source, ListIndexesOperation.this.batchSize);
            }
        };
    }
    
    private Function<BsonDocument, AsyncBatchCursor<T>> asyncTransformer(final AsyncConnectionSource source, final AsyncConnection connection) {
        return new Function<BsonDocument, AsyncBatchCursor<T>>() {
            @Override
            public AsyncBatchCursor<T> apply(final BsonDocument result) {
                return OperationHelper.cursorDocumentToAsyncBatchCursor(result.getDocument("cursor"), ListIndexesOperation.this.decoder, source, connection, ListIndexesOperation.this.batchSize);
            }
        };
    }
    
    private Codec<BsonDocument> createCommandDecoder() {
        return CommandResultDocumentCodec.create(this.decoder, "firstBatch");
    }
}
