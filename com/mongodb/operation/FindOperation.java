// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.connection.ServerType;
import org.bson.BsonInt64;
import com.mongodb.ReadPreference;
import com.mongodb.connection.ConnectionDescription;
import org.bson.BsonBoolean;
import org.bson.BsonValue;
import java.util.Map;
import org.bson.codecs.BsonDocumentCodec;
import com.mongodb.MongoInternalException;
import java.util.List;
import com.mongodb.ExplainVerbosity;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.binding.AsyncConnectionSource;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncReadBinding;
import com.mongodb.connection.QueryResult;
import com.mongodb.connection.Connection;
import com.mongodb.binding.ConnectionSource;
import com.mongodb.binding.ReadBinding;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import com.mongodb.CursorType;
import org.bson.BsonDocument;
import org.bson.codecs.Decoder;
import com.mongodb.MongoNamespace;
import com.mongodb.async.AsyncBatchCursor;

public class FindOperation<T> implements AsyncReadOperation<AsyncBatchCursor<T>>, ReadOperation<BatchCursor<T>>
{
    private final MongoNamespace namespace;
    private final Decoder<T> decoder;
    private BsonDocument filter;
    private int batchSize;
    private int limit;
    private BsonDocument modifiers;
    private BsonDocument projection;
    private long maxTimeMS;
    private int skip;
    private BsonDocument sort;
    private CursorType cursorType;
    private boolean slaveOk;
    private boolean oplogReplay;
    private boolean noCursorTimeout;
    private boolean partial;
    
    public FindOperation(final MongoNamespace namespace, final Decoder<T> decoder) {
        this.cursorType = CursorType.NonTailable;
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
    
    public FindOperation<T> filter(final BsonDocument filter) {
        this.filter = filter;
        return this;
    }
    
    public int getBatchSize() {
        return this.batchSize;
    }
    
    public FindOperation<T> batchSize(final int batchSize) {
        this.batchSize = batchSize;
        return this;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public FindOperation<T> limit(final int limit) {
        this.limit = limit;
        return this;
    }
    
    public BsonDocument getModifiers() {
        return this.modifiers;
    }
    
    public FindOperation<T> modifiers(final BsonDocument modifiers) {
        this.modifiers = modifiers;
        return this;
    }
    
    public BsonDocument getProjection() {
        return this.projection;
    }
    
    public FindOperation<T> projection(final BsonDocument projection) {
        this.projection = projection;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public FindOperation<T> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    public int getSkip() {
        return this.skip;
    }
    
    public FindOperation<T> skip(final int skip) {
        this.skip = skip;
        return this;
    }
    
    public BsonDocument getSort() {
        return this.sort;
    }
    
    public FindOperation<T> sort(final BsonDocument sort) {
        this.sort = sort;
        return this;
    }
    
    public CursorType getCursorType() {
        return this.cursorType;
    }
    
    public FindOperation<T> cursorType(final CursorType cursorType) {
        this.cursorType = Assertions.notNull("cursorType", cursorType);
        return this;
    }
    
    public boolean isSlaveOk() {
        return this.slaveOk;
    }
    
    public FindOperation<T> slaveOk(final boolean slaveOk) {
        this.slaveOk = slaveOk;
        return this;
    }
    
    public boolean isOplogReplay() {
        return this.oplogReplay;
    }
    
    public FindOperation<T> oplogReplay(final boolean oplogReplay) {
        this.oplogReplay = oplogReplay;
        return this;
    }
    
    public boolean isNoCursorTimeout() {
        return this.noCursorTimeout;
    }
    
    public FindOperation<T> noCursorTimeout(final boolean noCursorTimeout) {
        this.noCursorTimeout = noCursorTimeout;
        return this;
    }
    
    public boolean isPartial() {
        return this.partial;
    }
    
    public FindOperation<T> partial(final boolean partial) {
        this.partial = partial;
        return this;
    }
    
    @Override
    public BatchCursor<T> execute(final ReadBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>)new OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>() {
            @Override
            public BatchCursor<T> call(final ConnectionSource source, final Connection connection) {
                final QueryResult<T> queryResult = connection.query(FindOperation.this.namespace, FindOperation.this.asDocument(connection.getDescription(), binding.getReadPreference()), FindOperation.this.projection, FindOperation.this.skip, FindOperation.this.limit, FindOperation.this.batchSize, FindOperation.this.isSlaveOk() || binding.getReadPreference().isSlaveOk(), FindOperation.this.isTailableCursor(), FindOperation.this.isAwaitData(), FindOperation.this.isNoCursorTimeout(), FindOperation.this.isPartial(), FindOperation.this.isOplogReplay(), FindOperation.this.decoder);
                return new QueryBatchCursor<T>(queryResult, FindOperation.this.limit, FindOperation.this.batchSize, FindOperation.this.decoder, source, connection);
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
                    connection.queryAsync(FindOperation.this.namespace, FindOperation.this.asDocument(connection.getDescription(), binding.getReadPreference()), FindOperation.this.projection, FindOperation.this.skip, FindOperation.this.limit, FindOperation.this.batchSize, FindOperation.this.isSlaveOk() || binding.getReadPreference().isSlaveOk(), FindOperation.this.isTailableCursor(), FindOperation.this.isAwaitData(), FindOperation.this.isNoCursorTimeout(), FindOperation.this.isPartial(), FindOperation.this.isOplogReplay(), FindOperation.this.decoder, (SingleResultCallback<QueryResult<Object>>)new SingleResultCallback<QueryResult<T>>() {
                        @Override
                        public void onResult(final QueryResult<T> result, final Throwable t) {
                            if (t != null) {
                                wrappedCallback.onResult(null, t);
                            }
                            else {
                                wrappedCallback.onResult(new AsyncQueryBatchCursor<T>(result, FindOperation.this.limit, FindOperation.this.batchSize, FindOperation.this.decoder, source, connection), null);
                            }
                        }
                    });
                }
            }
        });
    }
    
    public ReadOperation<BsonDocument> asExplainableOperation(final ExplainVerbosity explainVerbosity) {
        final FindOperation<BsonDocument> explainableFindOperation = this.createExplainableQueryOperation();
        return new ReadOperation<BsonDocument>() {
            @Override
            public BsonDocument execute(final ReadBinding binding) {
                return (BsonDocument)explainableFindOperation.execute(binding).next().iterator().next();
            }
        };
    }
    
    public AsyncReadOperation<BsonDocument> asExplainableOperationAsync(final ExplainVerbosity explainVerbosity) {
        final FindOperation<BsonDocument> explainableFindOperation = this.createExplainableQueryOperation();
        return new AsyncReadOperation<BsonDocument>() {
            @Override
            public void executeAsync(final AsyncReadBinding binding, final SingleResultCallback<BsonDocument> callback) {
                explainableFindOperation.executeAsync(binding, new SingleResultCallback<AsyncBatchCursor<BsonDocument>>() {
                    @Override
                    public void onResult(final AsyncBatchCursor<BsonDocument> result, final Throwable t) {
                        if (t != null) {
                            callback.onResult(null, t);
                        }
                        else {
                            result.next(new SingleResultCallback<List<BsonDocument>>() {
                                @Override
                                public void onResult(final List<BsonDocument> result, final Throwable t) {
                                    if (t != null) {
                                        callback.onResult(null, t);
                                    }
                                    else if (result.size() == 0) {
                                        callback.onResult(null, new MongoInternalException("Unexpected explain result size"));
                                    }
                                    else {
                                        callback.onResult(result.get(0), null);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        };
    }
    
    private FindOperation<BsonDocument> createExplainableQueryOperation() {
        final FindOperation<BsonDocument> explainFindOperation = new FindOperation<BsonDocument>(this.namespace, new BsonDocumentCodec());
        final BsonDocument explainModifiers = new BsonDocument();
        if (this.modifiers != null) {
            explainModifiers.putAll(this.modifiers);
        }
        explainModifiers.append("$explain", BsonBoolean.TRUE);
        return explainFindOperation.filter(this.filter).projection(this.projection).sort(this.sort).skip(this.skip).limit(Math.abs(this.limit) * -1).modifiers(explainModifiers);
    }
    
    private BsonDocument asDocument(final ConnectionDescription connectionDescription, final ReadPreference readPreference) {
        BsonDocument document = new BsonDocument();
        if (this.sort != null) {
            document.put("$orderby", this.sort);
        }
        if (this.maxTimeMS > 0L) {
            document.put("$maxTimeMS", new BsonInt64(this.maxTimeMS));
        }
        if (connectionDescription.getServerType() == ServerType.SHARD_ROUTER && !readPreference.equals(ReadPreference.primary())) {
            document.put("$readPreference", readPreference.toDocument());
        }
        if (this.modifiers != null) {
            document.putAll(this.modifiers);
        }
        if (document.isEmpty()) {
            document = ((this.filter != null) ? this.filter : new BsonDocument());
        }
        else {
            document.put("$query", (this.filter != null) ? this.filter : new BsonDocument());
        }
        return document;
    }
    
    private boolean isTailableCursor() {
        return this.cursorType.isTailable();
    }
    
    private boolean isAwaitData() {
        return this.cursorType == CursorType.TailableAwait;
    }
}
