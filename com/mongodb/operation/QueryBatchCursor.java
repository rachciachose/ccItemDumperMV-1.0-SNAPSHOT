// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import java.util.Collections;
import com.mongodb.ServerAddress;
import java.util.NoSuchElementException;
import com.mongodb.assertions.Assertions;
import com.mongodb.connection.Connection;
import com.mongodb.connection.QueryResult;
import java.util.List;
import com.mongodb.ServerCursor;
import com.mongodb.binding.ConnectionSource;
import org.bson.codecs.Decoder;
import com.mongodb.MongoNamespace;

class QueryBatchCursor<T> implements BatchCursor<T>
{
    private final MongoNamespace namespace;
    private final int limit;
    private final Decoder<T> decoder;
    private final ConnectionSource connectionSource;
    private int batchSize;
    private ServerCursor serverCursor;
    private List<T> nextBatch;
    private int count;
    private boolean closed;
    
    QueryBatchCursor(final QueryResult<T> firstQueryResult, final int limit, final int batchSize, final Decoder<T> decoder) {
        this(firstQueryResult, limit, batchSize, decoder, null);
    }
    
    QueryBatchCursor(final QueryResult<T> firstQueryResult, final int limit, final int batchSize, final Decoder<T> decoder, final ConnectionSource connectionSource) {
        this(firstQueryResult, limit, batchSize, decoder, connectionSource, null);
    }
    
    QueryBatchCursor(final QueryResult<T> firstQueryResult, final int limit, final int batchSize, final Decoder<T> decoder, final ConnectionSource connectionSource, final Connection connection) {
        this.namespace = firstQueryResult.getNamespace();
        this.limit = limit;
        this.batchSize = batchSize;
        this.decoder = Assertions.notNull("decoder", decoder);
        if (firstQueryResult.getCursor() != null) {
            Assertions.notNull("connectionSource", connectionSource);
        }
        if (connectionSource != null) {
            this.connectionSource = connectionSource.retain();
        }
        else {
            this.connectionSource = null;
        }
        this.initFromQueryResult(firstQueryResult);
        if (this.limitReached()) {
            Assertions.notNull("connection", connection);
            this.killCursor(connection);
        }
    }
    
    @Override
    public boolean hasNext() {
        if (this.closed) {
            throw new IllegalStateException("Cursor has been closed");
        }
        if (this.nextBatch != null) {
            return true;
        }
        if (this.limitReached()) {
            return false;
        }
        while (this.serverCursor != null) {
            this.getMore();
            if (this.nextBatch != null) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<T> next() {
        if (this.closed) {
            throw new IllegalStateException("Iterator has been closed");
        }
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final List<T> retVal = this.nextBatch;
        this.nextBatch = null;
        return retVal;
    }
    
    @Override
    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }
    
    @Override
    public int getBatchSize() {
        return this.batchSize;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
    
    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        try {
            this.killCursor();
        }
        finally {
            if (this.connectionSource != null) {
                this.connectionSource.release();
            }
        }
        this.closed = true;
    }
    
    @Override
    public List<T> tryNext() {
        if (this.closed) {
            throw new IllegalStateException("Cursor has been closed");
        }
        if (!this.tryHasNext()) {
            return null;
        }
        return this.next();
    }
    
    boolean tryHasNext() {
        if (this.nextBatch != null) {
            return true;
        }
        if (this.limitReached()) {
            return false;
        }
        if (this.serverCursor != null) {
            this.getMore();
        }
        return this.nextBatch != null;
    }
    
    @Override
    public ServerCursor getServerCursor() {
        if (this.closed) {
            throw new IllegalStateException("Iterator has been closed");
        }
        return this.serverCursor;
    }
    
    @Override
    public ServerAddress getServerAddress() {
        if (this.closed) {
            throw new IllegalStateException("Iterator has been closed");
        }
        return this.connectionSource.getServerDescription().getAddress();
    }
    
    private void getMore() {
        final Connection connection = this.connectionSource.getConnection();
        try {
            final QueryResult<T> nextQueryResult = connection.getMore(this.namespace, this.serverCursor.getId(), CursorHelper.getNumberToReturn(this.limit, this.batchSize, this.count), this.decoder);
            this.initFromQueryResult(nextQueryResult);
            if (this.limitReached()) {
                this.killCursor(connection);
            }
        }
        finally {
            connection.release();
        }
    }
    
    private void initFromQueryResult(final QueryResult<T> queryResult) {
        this.serverCursor = queryResult.getCursor();
        this.nextBatch = (queryResult.getResults().isEmpty() ? null : queryResult.getResults());
        this.count += queryResult.getResults().size();
    }
    
    private boolean limitReached() {
        return this.limit != 0 && this.count >= this.limit;
    }
    
    private void killCursor() {
        if (this.serverCursor != null) {
            final Connection connection = this.connectionSource.getConnection();
            try {
                this.killCursor(connection);
            }
            finally {
                connection.release();
            }
        }
    }
    
    private void killCursor(final Connection connection) {
        if (this.serverCursor != null) {
            connection.killCursor(this.namespace, Collections.singletonList(this.serverCursor.getId()));
            this.serverCursor = null;
        }
    }
}
