// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonInt64;
import org.bson.BsonValue;
import org.bson.BsonInt32;
import com.mongodb.MongoNamespace;
import com.mongodb.connection.ConnectionDescription;
import com.mongodb.connection.QueryResult;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.binding.AsyncConnectionSource;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncReadBinding;
import com.mongodb.Function;
import org.bson.BsonDocument;
import com.mongodb.connection.Connection;
import com.mongodb.binding.ConnectionSource;
import com.mongodb.binding.ReadBinding;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.Decoder;
import com.mongodb.async.AsyncBatchCursor;

public class ListDatabasesOperation<T> implements AsyncReadOperation<AsyncBatchCursor<T>>, ReadOperation<BatchCursor<T>>
{
    private final Decoder<T> decoder;
    private long maxTimeMS;
    
    public ListDatabasesOperation(final Decoder<T> decoder) {
        this.decoder = Assertions.notNull("decoder", decoder);
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public ListDatabasesOperation<T> maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public BatchCursor<T> execute(final ReadBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>)new OperationHelper.CallableWithConnectionAndSource<BatchCursor<T>>() {
            @Override
            public BatchCursor<T> call(final ConnectionSource source, final Connection connection) {
                return CommandOperationHelper.executeWrappedCommandProtocol(binding, "admin", ListDatabasesOperation.this.getCommand(), CommandResultDocumentCodec.create((Decoder<Object>)ListDatabasesOperation.this.decoder, "databases"), connection, ListDatabasesOperation.this.transformer(source, connection));
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
                    CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, "admin", ListDatabasesOperation.this.getCommand(), CommandResultDocumentCodec.create((Decoder<Object>)ListDatabasesOperation.this.decoder, "databases"), connection, ListDatabasesOperation.this.asyncTransformer(source, connection), (SingleResultCallback<Object>)OperationHelper.releasingCallback((SingleResultCallback<T>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), connection));
                }
            }
        });
    }
    
    private Function<BsonDocument, BatchCursor<T>> transformer(final ConnectionSource source, final Connection connection) {
        return new Function<BsonDocument, BatchCursor<T>>() {
            @Override
            public BatchCursor<T> apply(final BsonDocument result) {
                return new QueryBatchCursor<T>(ListDatabasesOperation.this.createQueryResult(result, connection.getDescription()), 0, 0, ListDatabasesOperation.this.decoder, source);
            }
        };
    }
    
    private Function<BsonDocument, AsyncBatchCursor<T>> asyncTransformer(final AsyncConnectionSource source, final AsyncConnection connection) {
        return new Function<BsonDocument, AsyncBatchCursor<T>>() {
            @Override
            public AsyncBatchCursor<T> apply(final BsonDocument result) {
                return new AsyncQueryBatchCursor<T>(ListDatabasesOperation.this.createQueryResult(result, connection.getDescription()), 0, 0, ListDatabasesOperation.this.decoder, source, connection);
            }
        };
    }
    
    private QueryResult<T> createQueryResult(final BsonDocument result, final ConnectionDescription description) {
        return new QueryResult<T>(null, BsonDocumentWrapperHelper.toList(result, "databases"), 0L, description.getServerAddress());
    }
    
    private BsonDocument getCommand() {
        final BsonDocument command = new BsonDocument("listDatabases", new BsonInt32(1));
        if (this.maxTimeMS > 0L) {
            command.put("maxTimeMS", new BsonInt64(this.maxTimeMS));
        }
        return command;
    }
}
