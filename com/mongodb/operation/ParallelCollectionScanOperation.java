// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonInt32;
import org.bson.BsonString;
import com.mongodb.ServerAddress;
import org.bson.BsonArray;
import java.util.Iterator;
import com.mongodb.connection.QueryResult;
import org.bson.BsonValue;
import java.util.ArrayList;
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
import com.mongodb.assertions.Assertions;
import org.bson.codecs.Decoder;
import com.mongodb.MongoNamespace;
import com.mongodb.async.AsyncBatchCursor;
import java.util.List;

public class ParallelCollectionScanOperation<T> implements AsyncReadOperation<List<AsyncBatchCursor<T>>>, ReadOperation<List<BatchCursor<T>>>
{
    private final MongoNamespace namespace;
    private final int numCursors;
    private int batchSize;
    private final Decoder<T> decoder;
    
    public ParallelCollectionScanOperation(final MongoNamespace namespace, final int numCursors, final Decoder<T> decoder) {
        this.batchSize = 0;
        this.namespace = Assertions.notNull("namespace", namespace);
        Assertions.isTrue("numCursors >= 1", numCursors >= 1);
        this.numCursors = numCursors;
        this.decoder = Assertions.notNull("decoder", decoder);
    }
    
    public int getNumCursors() {
        return this.numCursors;
    }
    
    public int getBatchSize() {
        return this.batchSize;
    }
    
    public ParallelCollectionScanOperation<T> batchSize(final int batchSize) {
        Assertions.isTrue("batchSize >= 0", batchSize >= 0);
        this.batchSize = batchSize;
        return this;
    }
    
    @Override
    public List<BatchCursor<T>> execute(final ReadBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnectionAndSource<List<BatchCursor<T>>>)new OperationHelper.CallableWithConnectionAndSource<List<BatchCursor<T>>>() {
            @Override
            public List<BatchCursor<T>> call(final ConnectionSource source, final Connection connection) {
                return CommandOperationHelper.executeWrappedCommandProtocol(binding, ParallelCollectionScanOperation.this.namespace.getDatabaseName(), ParallelCollectionScanOperation.this.getCommand(), CommandResultDocumentCodec.create((Decoder<Object>)ParallelCollectionScanOperation.this.decoder, "firstBatch"), connection, ParallelCollectionScanOperation.this.transformer(source));
            }
        });
    }
    
    @Override
    public void executeAsync(final AsyncReadBinding binding, final SingleResultCallback<List<AsyncBatchCursor<T>>> callback) {
        OperationHelper.withConnection(binding, new OperationHelper.AsyncCallableWithConnectionAndSource() {
            @Override
            public void call(final AsyncConnectionSource source, final AsyncConnection connection, final Throwable t) {
                if (t != null) {
                    ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<Object>)callback).onResult(null, t);
                }
                else {
                    CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, ParallelCollectionScanOperation.this.namespace.getDatabaseName(), ParallelCollectionScanOperation.this.getCommand(), CommandResultDocumentCodec.create((Decoder<Object>)ParallelCollectionScanOperation.this.decoder, "firstBatch"), connection, ParallelCollectionScanOperation.this.asyncTransformer(source, connection), (SingleResultCallback<Object>)OperationHelper.releasingCallback((SingleResultCallback<T>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), source, connection));
                }
            }
        });
    }
    
    private Function<BsonDocument, List<BatchCursor<T>>> transformer(final ConnectionSource source) {
        return new Function<BsonDocument, List<BatchCursor<T>>>() {
            @Override
            public List<BatchCursor<T>> apply(final BsonDocument result) {
                final List<BatchCursor<T>> cursors = new ArrayList<BatchCursor<T>>();
                for (final BsonValue cursorValue : ParallelCollectionScanOperation.this.getCursorDocuments(result)) {
                    cursors.add(new QueryBatchCursor<T>(ParallelCollectionScanOperation.this.createQueryResult(ParallelCollectionScanOperation.this.getCursorDocument(cursorValue.asDocument()), source.getServerDescription().getAddress()), 0, ParallelCollectionScanOperation.this.getBatchSize(), ParallelCollectionScanOperation.this.decoder, source));
                }
                return cursors;
            }
        };
    }
    
    private Function<BsonDocument, List<AsyncBatchCursor<T>>> asyncTransformer(final AsyncConnectionSource source, final AsyncConnection connection) {
        return new Function<BsonDocument, List<AsyncBatchCursor<T>>>() {
            @Override
            public List<AsyncBatchCursor<T>> apply(final BsonDocument result) {
                final List<AsyncBatchCursor<T>> cursors = new ArrayList<AsyncBatchCursor<T>>();
                for (final BsonValue cursorValue : ParallelCollectionScanOperation.this.getCursorDocuments(result)) {
                    cursors.add(new AsyncQueryBatchCursor<T>(ParallelCollectionScanOperation.this.createQueryResult(ParallelCollectionScanOperation.this.getCursorDocument(cursorValue.asDocument()), source.getServerDescription().getAddress()), 0, ParallelCollectionScanOperation.this.getBatchSize(), ParallelCollectionScanOperation.this.decoder, source, connection));
                }
                return cursors;
            }
        };
    }
    
    private BsonArray getCursorDocuments(final BsonDocument result) {
        return result.getArray("cursors");
    }
    
    private BsonDocument getCursorDocument(final BsonDocument cursorDocument) {
        return cursorDocument.getDocument("cursor");
    }
    
    private QueryResult<T> createQueryResult(final BsonDocument cursorDocument, final ServerAddress serverAddress) {
        return OperationHelper.cursorDocumentToQueryResult(cursorDocument, serverAddress);
    }
    
    private BsonDocument getCommand() {
        return new BsonDocument("parallelCollectionScan", new BsonString(this.namespace.getCollectionName())).append("numCursors", new BsonInt32(this.getNumCursors()));
    }
}
