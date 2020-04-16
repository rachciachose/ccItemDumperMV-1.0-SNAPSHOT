// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.Function;
import com.mongodb.binding.AsyncReadBinding;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.connection.Connection;
import com.mongodb.binding.WriteBinding;
import com.mongodb.binding.ReadBinding;
import java.util.Arrays;
import com.mongodb.connection.ServerVersion;
import com.mongodb.connection.ConnectionDescription;
import com.mongodb.async.SingleResultCallback;
import org.bson.BsonInt64;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.binding.AsyncConnectionSource;
import com.mongodb.binding.ConnectionSource;
import org.bson.BsonDocument;
import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.connection.QueryResult;
import java.util.Collections;
import com.mongodb.ServerAddress;
import org.bson.codecs.Decoder;
import com.mongodb.MongoNamespace;

final class OperationHelper
{
    static <T> QueryBatchCursor<T> createEmptyBatchCursor(final MongoNamespace namespace, final Decoder<T> decoder, final ServerAddress serverAddress, final int batchSize) {
        return new QueryBatchCursor<T>(new QueryResult<T>(namespace, Collections.emptyList(), 0L, serverAddress), 0, batchSize, decoder);
    }
    
    static <T> AsyncBatchCursor<T> createEmptyAsyncBatchCursor(final MongoNamespace namespace, final Decoder<T> decoder, final ServerAddress serverAddress, final int batchSize) {
        return new AsyncQueryBatchCursor<T>(new QueryResult<T>(namespace, Collections.emptyList(), 0L, serverAddress), 0, batchSize, decoder);
    }
    
    static <T> BatchCursor<T> cursorDocumentToBatchCursor(final BsonDocument cursorDocument, final Decoder<T> decoder, final ConnectionSource source, final int batchSize) {
        return new QueryBatchCursor<T>(cursorDocumentToQueryResult(cursorDocument, source.getServerDescription().getAddress()), 0, batchSize, decoder, source);
    }
    
    static <T> AsyncBatchCursor<T> cursorDocumentToAsyncBatchCursor(final BsonDocument cursorDocument, final Decoder<T> decoder, final AsyncConnectionSource source, final AsyncConnection connection, final int batchSize) {
        return new AsyncQueryBatchCursor<T>(cursorDocumentToQueryResult(cursorDocument, source.getServerDescription().getAddress()), 0, batchSize, decoder, source, connection);
    }
    
    static <T> QueryResult<T> cursorDocumentToQueryResult(final BsonDocument cursorDocument, final ServerAddress serverAddress) {
        final long cursorId = ((BsonInt64)cursorDocument.get("id")).getValue();
        final MongoNamespace queryResultNamespace = new MongoNamespace(cursorDocument.getString("ns").getValue());
        return new QueryResult<T>(queryResultNamespace, BsonDocumentWrapperHelper.toList(cursorDocument, "firstBatch"), cursorId, serverAddress);
    }
    
    static <T> SingleResultCallback<T> releasingCallback(final SingleResultCallback<T> wrapped, final AsyncConnection connection) {
        return new ConnectionReleasingWrappedCallback<T>(wrapped, null, connection);
    }
    
    static <T> SingleResultCallback<T> releasingCallback(final SingleResultCallback<T> wrapped, final AsyncConnectionSource source, final AsyncConnection connection) {
        return new ConnectionReleasingWrappedCallback<T>(wrapped, source, connection);
    }
    
    static boolean serverIsAtLeastVersionTwoDotSix(final ConnectionDescription description) {
        return serverIsAtLeastVersion(description, new ServerVersion(2, 6));
    }
    
    static boolean serverIsAtLeastVersionThreeDotZero(final ConnectionDescription description) {
        return serverIsAtLeastVersion(description, new ServerVersion(Arrays.asList(3, 0, 0)));
    }
    
    static boolean serverIsAtLeastVersion(final ConnectionDescription description, final ServerVersion serverVersion) {
        return description.getServerVersion().compareTo(serverVersion) >= 0;
    }
    
    static <T> T withConnection(final ReadBinding binding, final CallableWithConnection<T> callable) {
        final ConnectionSource source = binding.getReadConnectionSource();
        try {
            return withConnectionSource(source, callable);
        }
        finally {
            source.release();
        }
    }
    
    static <T> T withConnection(final ReadBinding binding, final CallableWithConnectionAndSource<T> callable) {
        final ConnectionSource source = binding.getReadConnectionSource();
        try {
            return withConnectionSource(source, callable);
        }
        finally {
            source.release();
        }
    }
    
    static <T> T withConnection(final WriteBinding binding, final CallableWithConnection<T> callable) {
        final ConnectionSource source = binding.getWriteConnectionSource();
        try {
            return withConnectionSource(source, callable);
        }
        finally {
            source.release();
        }
    }
    
    static <T> T withConnectionSource(final ConnectionSource source, final CallableWithConnection<T> callable) {
        final Connection connection = source.getConnection();
        try {
            return callable.call(connection);
        }
        finally {
            connection.release();
        }
    }
    
    static <T> T withConnectionSource(final ConnectionSource source, final CallableWithConnectionAndSource<T> callable) {
        final Connection connection = source.getConnection();
        try {
            return callable.call(source, connection);
        }
        finally {
            connection.release();
        }
    }
    
    static void withConnection(final AsyncWriteBinding binding, final AsyncCallableWithConnection callable) {
        binding.getWriteConnectionSource(ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<AsyncConnectionSource>)new AsyncCallableWithConnectionCallback(callable)));
    }
    
    static void withConnection(final AsyncReadBinding binding, final AsyncCallableWithConnection callable) {
        binding.getReadConnectionSource(ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<AsyncConnectionSource>)new AsyncCallableWithConnectionCallback(callable)));
    }
    
    static void withConnection(final AsyncReadBinding binding, final AsyncCallableWithConnectionAndSource callable) {
        binding.getReadConnectionSource(ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<AsyncConnectionSource>)new AsyncCallableWithConnectionAndSourceCallback(callable)));
    }
    
    private static void withConnectionSource(final AsyncConnectionSource source, final AsyncCallableWithConnection callable) {
        source.getConnection(new SingleResultCallback<AsyncConnection>() {
            @Override
            public void onResult(final AsyncConnection connection, final Throwable t) {
                source.release();
                if (t != null) {
                    callable.call(null, t);
                }
                else {
                    callable.call(connection, null);
                }
            }
        });
    }
    
    private static void withConnectionSource(final AsyncConnectionSource source, final AsyncCallableWithConnectionAndSource callable) {
        source.getConnection(new SingleResultCallback<AsyncConnection>() {
            @Override
            public void onResult(final AsyncConnection result, final Throwable t) {
                callable.call(source, result, t);
            }
        });
    }
    
    static class IdentityTransformer<T> implements Function<T, T>
    {
        @Override
        public T apply(final T t) {
            return t;
        }
    }
    
    static class VoidTransformer<T> implements Function<T, Void>
    {
        @Override
        public Void apply(final T t) {
            return null;
        }
    }
    
    private static class ConnectionReleasingWrappedCallback<T> implements SingleResultCallback<T>
    {
        private final SingleResultCallback<T> wrapped;
        private final AsyncConnectionSource source;
        private final AsyncConnection connection;
        
        ConnectionReleasingWrappedCallback(final SingleResultCallback<T> wrapped, final AsyncConnectionSource source, final AsyncConnection connection) {
            this.wrapped = wrapped;
            this.source = source;
            this.connection = connection;
        }
        
        @Override
        public void onResult(final T result, final Throwable t) {
            if (this.connection != null) {
                this.connection.release();
            }
            if (this.source != null) {
                this.source.release();
            }
            this.wrapped.onResult(result, t);
        }
    }
    
    private static class AsyncCallableWithConnectionCallback implements SingleResultCallback<AsyncConnectionSource>
    {
        private final AsyncCallableWithConnection callable;
        
        public AsyncCallableWithConnectionCallback(final AsyncCallableWithConnection callable) {
            this.callable = callable;
        }
        
        @Override
        public void onResult(final AsyncConnectionSource source, final Throwable t) {
            if (t != null) {
                this.callable.call(null, t);
            }
            else {
                withConnectionSource(source, this.callable);
            }
        }
    }
    
    private static class AsyncCallableWithConnectionAndSourceCallback implements SingleResultCallback<AsyncConnectionSource>
    {
        private final AsyncCallableWithConnectionAndSource callable;
        
        public AsyncCallableWithConnectionAndSourceCallback(final AsyncCallableWithConnectionAndSource callable) {
            this.callable = callable;
        }
        
        @Override
        public void onResult(final AsyncConnectionSource source, final Throwable t) {
            if (t != null) {
                this.callable.call(null, null, t);
            }
            else {
                withConnectionSource(source, this.callable);
            }
        }
    }
    
    interface AsyncCallableWithConnectionAndSource
    {
        void call(final AsyncConnectionSource p0, final AsyncConnection p1, final Throwable p2);
    }
    
    interface AsyncCallableWithConnection
    {
        void call(final AsyncConnection p0, final Throwable p1);
    }
    
    interface CallableWithConnectionAndSource<T>
    {
        T call(final ConnectionSource p0, final Connection p1);
    }
    
    interface CallableWithConnection<T>
    {
        T call(final Connection p0);
    }
}
