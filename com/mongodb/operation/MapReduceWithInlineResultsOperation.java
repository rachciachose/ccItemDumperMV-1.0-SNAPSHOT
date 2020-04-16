// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonNull;
import com.mongodb.connection.ConnectionDescription;
import org.bson.BsonBoolean;
import org.bson.BsonInt32;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.connection.QueryResult;
import org.bson.codecs.BsonDocumentCodec;
import com.mongodb.ExplainVerbosity;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
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
import org.bson.BsonJavaScript;
import com.mongodb.MongoNamespace;

public class MapReduceWithInlineResultsOperation<T> implements AsyncReadOperation<MapReduceAsyncBatchCursor<T>>, ReadOperation<MapReduceBatchCursor<T>>
{
    private final MongoNamespace namespace;
    private final BsonJavaScript mapFunction;
    private final BsonJavaScript reduceFunction;
    private final Decoder<T> decoder;
    private BsonJavaScript finalizeFunction;
    private BsonDocument scope;
    private BsonDocument filter;
    private BsonDocument sort;
    private int limit;
    private boolean jsMode;
    private boolean verbose;
    private long maxTimeMS;
    
    public MapReduceWithInlineResultsOperation(final MongoNamespace namespace, final BsonJavaScript mapFunction, final BsonJavaScript reduceFunction, final Decoder<T> decoder) {
        this.namespace = Assertions.notNull("namespace", namespace);
        this.mapFunction = Assertions.notNull("mapFunction", mapFunction);
        this.reduceFunction = Assertions.notNull("reduceFunction", reduceFunction);
        this.decoder = Assertions.notNull("decoder", decoder);
    }
    
    public BsonJavaScript getMapFunction() {
        return this.mapFunction;
    }
    
    public BsonJavaScript getReduceFunction() {
        return this.reduceFunction;
    }
    
    public BsonJavaScript getFinalizeFunction() {
        return this.finalizeFunction;
    }
    
    public MapReduceWithInlineResultsOperation<T> finalizeFunction(final BsonJavaScript finalizeFunction) {
        this.finalizeFunction = finalizeFunction;
        return this;
    }
    
    public BsonDocument getScope() {
        return this.scope;
    }
    
    public MapReduceWithInlineResultsOperation<T> scope(final BsonDocument scope) {
        this.scope = scope;
        return this;
    }
    
    public BsonDocument getFilter() {
        return this.filter;
    }
    
    public MapReduceWithInlineResultsOperation<T> filter(final BsonDocument filter) {
        this.filter = filter;
        return this;
    }
    
    public BsonDocument getSort() {
        return this.sort;
    }
    
    public MapReduceWithInlineResultsOperation<T> sort(final BsonDocument sort) {
        this.sort = sort;
        return this;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public MapReduceWithInlineResultsOperation<T> limit(final int limit) {
        this.limit = limit;
        return this;
    }
    
    public boolean isJsMode() {
        return this.jsMode;
    }
    
    public MapReduceWithInlineResultsOperation<T> jsMode(final boolean jsMode) {
        this.jsMode = jsMode;
        return this;
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public MapReduceWithInlineResultsOperation<T> verbose(final boolean verbose) {
        this.verbose = verbose;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public MapReduceWithInlineResultsOperation<T> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public MapReduceBatchCursor<T> execute(final ReadBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnectionAndSource<MapReduceBatchCursor<T>>)new OperationHelper.CallableWithConnectionAndSource<MapReduceBatchCursor<T>>() {
            @Override
            public MapReduceBatchCursor<T> call(final ConnectionSource source, final Connection connection) {
                return CommandOperationHelper.executeWrappedCommandProtocol(binding, MapReduceWithInlineResultsOperation.this.namespace.getDatabaseName(), MapReduceWithInlineResultsOperation.this.getCommand(), CommandResultDocumentCodec.create((Decoder<Object>)MapReduceWithInlineResultsOperation.this.decoder, "results"), connection, MapReduceWithInlineResultsOperation.this.transformer(source, connection));
            }
        });
    }
    
    @Override
    public void executeAsync(final AsyncReadBinding binding, final SingleResultCallback<MapReduceAsyncBatchCursor<T>> callback) {
        OperationHelper.withConnection(binding, new OperationHelper.AsyncCallableWithConnection() {
            @Override
            public void call(final AsyncConnection connection, final Throwable t) {
                if (t != null) {
                    ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<Object>)callback).onResult(null, t);
                }
                else {
                    CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, MapReduceWithInlineResultsOperation.this.namespace.getDatabaseName(), MapReduceWithInlineResultsOperation.this.getCommand(), CommandResultDocumentCodec.create((Decoder<Object>)MapReduceWithInlineResultsOperation.this.decoder, "results"), connection, MapReduceWithInlineResultsOperation.this.asyncTransformer(connection), (SingleResultCallback<Object>)OperationHelper.releasingCallback((SingleResultCallback<T>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), connection));
                }
            }
        });
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
    
    private Function<BsonDocument, MapReduceBatchCursor<T>> transformer(final ConnectionSource source, final Connection connection) {
        return new Function<BsonDocument, MapReduceBatchCursor<T>>() {
            @Override
            public MapReduceBatchCursor<T> apply(final BsonDocument result) {
                return new MapReduceInlineResultsCursor<T>(MapReduceWithInlineResultsOperation.this.createQueryResult(result, connection.getDescription()), MapReduceWithInlineResultsOperation.this.decoder, source, MapReduceHelper.createStatistics(result));
            }
        };
    }
    
    private Function<BsonDocument, MapReduceAsyncBatchCursor<T>> asyncTransformer(final AsyncConnection connection) {
        return new Function<BsonDocument, MapReduceAsyncBatchCursor<T>>() {
            @Override
            public MapReduceAsyncBatchCursor<T> apply(final BsonDocument result) {
                return new MapReduceInlineResultsAsyncCursor<T>(MapReduceWithInlineResultsOperation.this.createQueryResult(result, connection.getDescription()), MapReduceWithInlineResultsOperation.this.decoder, MapReduceHelper.createStatistics(result));
            }
        };
    }
    
    private BsonDocument getCommand() {
        final BsonDocument commandDocument = new BsonDocument("mapreduce", new BsonString(this.namespace.getCollectionName())).append("map", this.getMapFunction()).append("reduce", this.getReduceFunction()).append("out", new BsonDocument("inline", new BsonInt32(1))).append("query", asValueOrNull(this.getFilter())).append("sort", asValueOrNull(this.getSort())).append("finalize", asValueOrNull(this.getFinalizeFunction())).append("scope", asValueOrNull(this.getScope())).append("verbose", BsonBoolean.valueOf(this.isVerbose()));
        DocumentHelper.putIfNotZero(commandDocument, "limit", this.getLimit());
        DocumentHelper.putIfNotZero(commandDocument, "maxTimeMS", this.getMaxTime(TimeUnit.MILLISECONDS));
        DocumentHelper.putIfTrue(commandDocument, "jsMode", this.isJsMode());
        return commandDocument;
    }
    
    private QueryResult<T> createQueryResult(final BsonDocument result, final ConnectionDescription description) {
        return new QueryResult<T>(this.namespace, BsonDocumentWrapperHelper.toList(result, "results"), 0L, description.getServerAddress());
    }
    
    private static BsonValue asValueOrNull(final BsonValue value) {
        return (value == null) ? BsonNull.VALUE : value;
    }
}
