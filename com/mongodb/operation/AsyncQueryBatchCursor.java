// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.internal.async.ErrorHandlingResultCallback;
import java.util.Collections;
import java.util.List;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.assertions.Assertions;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.ServerCursor;
import com.mongodb.connection.QueryResult;
import com.mongodb.binding.AsyncConnectionSource;
import org.bson.codecs.Decoder;
import com.mongodb.MongoNamespace;
import com.mongodb.async.AsyncBatchCursor;

class AsyncQueryBatchCursor<T> implements AsyncBatchCursor<T>
{
    private final MongoNamespace namespace;
    private final int limit;
    private final Decoder<T> decoder;
    private volatile AsyncConnectionSource connectionSource;
    private volatile QueryResult<T> firstBatch;
    private volatile int batchSize;
    private volatile ServerCursor cursor;
    private volatile int count;
    private volatile boolean closed;
    
    AsyncQueryBatchCursor(final QueryResult<T> firstBatch, final int limit, final int batchSize, final Decoder<T> decoder) {
        this(firstBatch, limit, batchSize, decoder, null, null);
    }
    
    AsyncQueryBatchCursor(final QueryResult<T> firstBatch, final int limit, final int batchSize, final Decoder<T> decoder, final AsyncConnectionSource connectionSource, final AsyncConnection connection) {
        this.namespace = firstBatch.getNamespace();
        this.firstBatch = firstBatch;
        this.limit = limit;
        this.batchSize = batchSize;
        this.decoder = decoder;
        this.cursor = firstBatch.getCursor();
        if (this.cursor != null) {
            Assertions.notNull("connectionSource", connectionSource);
            Assertions.notNull("connection", connection);
        }
        if (connectionSource != null) {
            this.connectionSource = connectionSource.retain();
        }
        else {
            this.connectionSource = null;
        }
        this.count += firstBatch.getResults().size();
        if (this.limitReached()) {
            this.killCursor(connection);
        }
    }
    
    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.killCursor(null);
        }
    }
    
    @Override
    public void next(final SingleResultCallback<List<T>> callback) {
        Assertions.isTrue("open", !this.closed);
        if (this.firstBatch != null && !this.firstBatch.getResults().isEmpty()) {
            final List<T> results = this.firstBatch.getResults();
            this.firstBatch = null;
            callback.onResult(results, null);
        }
        else if (this.cursor == null) {
            this.close();
            callback.onResult(null, null);
        }
        else {
            this.getMore(callback);
        }
    }
    
    @Override
    public void setBatchSize(final int batchSize) {
        Assertions.isTrue("open", !this.closed);
        this.batchSize = batchSize;
    }
    
    @Override
    public int getBatchSize() {
        Assertions.isTrue("open", !this.closed);
        return this.batchSize;
    }
    
    @Override
    public boolean isClosed() {
        return this.closed;
    }
    
    private boolean limitReached() {
        return this.limit != 0 && this.count >= this.limit;
    }
    
    private void getMore(final SingleResultCallback<List<T>> callback) {
        this.connectionSource.getConnection(new SingleResultCallback<AsyncConnection>() {
            @Override
            public void onResult(final AsyncConnection connection, final Throwable t) {
                if (t != null) {
                    callback.onResult(null, t);
                }
                else {
                    connection.getMoreAsync(AsyncQueryBatchCursor.this.namespace, AsyncQueryBatchCursor.this.cursor.getId(), CursorHelper.getNumberToReturn(AsyncQueryBatchCursor.this.limit, AsyncQueryBatchCursor.this.batchSize, AsyncQueryBatchCursor.this.count), AsyncQueryBatchCursor.this.decoder, (SingleResultCallback<QueryResult<Object>>)new QueryResultSingleResultCallback(connection, callback));
                }
            }
        });
    }
    
    private void killCursor(final AsyncConnection connection) {
        if (this.cursor != null) {
            final ServerCursor localCursor = this.cursor;
            final AsyncConnectionSource localConnectionSource = this.connectionSource;
            this.cursor = null;
            this.connectionSource = null;
            if (connection != null) {
                connection.retain();
                this.killCursorAsynchronouslyAndReleaseConnectionAndSource(connection, localCursor, localConnectionSource);
            }
            else {
                localConnectionSource.getConnection(new SingleResultCallback<AsyncConnection>() {
                    @Override
                    public void onResult(final AsyncConnection connection, final Throwable connectionException) {
                        if (connectionException == null) {
                            AsyncQueryBatchCursor.this.killCursorAsynchronouslyAndReleaseConnectionAndSource(connection, localCursor, localConnectionSource);
                        }
                    }
                });
            }
        }
        else if (this.connectionSource != null) {
            this.connectionSource.release();
            this.connectionSource = null;
        }
    }
    
    private void killCursorAsynchronouslyAndReleaseConnectionAndSource(final AsyncConnection connection, final ServerCursor localCursor, final AsyncConnectionSource localConnectionSource) {
        connection.killCursorAsync(this.namespace, Collections.singletonList(localCursor.getId()), new SingleResultCallback<Void>() {
            @Override
            public void onResult(final Void result, final Throwable t) {
                connection.release();
                localConnectionSource.release();
            }
        });
    }
    
    private class QueryResultSingleResultCallback implements SingleResultCallback<QueryResult<T>>
    {
        private final AsyncConnection connection;
        private final SingleResultCallback<List<T>> callback;
        
        public QueryResultSingleResultCallback(final AsyncConnection connection, final SingleResultCallback<List<T>> callback) {
            this.connection = connection;
            this.callback = ErrorHandlingResultCallback.errorHandlingCallback(callback);
        }
        
        @Override
        public void onResult(final QueryResult<T> result, final Throwable t) {
            if (t != null) {
                this.connection.release();
                AsyncQueryBatchCursor.this.close();
                this.callback.onResult(null, t);
            }
            else if (result.getResults().isEmpty() && result.getCursor() != null) {
                this.connection.getMoreAsync(AsyncQueryBatchCursor.this.namespace, AsyncQueryBatchCursor.this.cursor.getId(), CursorHelper.getNumberToReturn(AsyncQueryBatchCursor.this.limit, AsyncQueryBatchCursor.this.batchSize, AsyncQueryBatchCursor.this.count), AsyncQueryBatchCursor.this.decoder, (SingleResultCallback<QueryResult<Object>>)this);
            }
            else {
                AsyncQueryBatchCursor.this.cursor = result.getCursor();
                AsyncQueryBatchCursor.this.count += result.getResults().size();
                if (AsyncQueryBatchCursor.this.limitReached()) {
                    AsyncQueryBatchCursor.this.killCursor(this.connection);
                }
                this.connection.release();
                if (result.getResults().isEmpty()) {
                    this.callback.onResult(null, null);
                }
                else {
                    this.callback.onResult(result.getResults(), null);
                }
            }
        }
    }
}
