// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.NoSuchElementException;
import java.util.List;
import com.mongodb.operation.BatchCursor;
import com.mongodb.client.MongoCursor;

class MongoBatchCursorAdapter<T> implements MongoCursor<T>
{
    private final BatchCursor<T> batchCursor;
    private List<T> curBatch;
    private int curPos;
    
    public MongoBatchCursorAdapter(final BatchCursor<T> batchCursor) {
        this.batchCursor = batchCursor;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cursors do not support removal");
    }
    
    @Override
    public void close() {
        this.batchCursor.close();
    }
    
    @Override
    public boolean hasNext() {
        return !this.needsNewBatch() || this.batchCursor.hasNext();
    }
    
    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        if (this.needsNewBatch()) {
            this.curBatch = this.batchCursor.next();
            this.curPos = 0;
        }
        return this.curBatch.get(this.curPos++);
    }
    
    @Override
    public T tryNext() {
        if (this.needsNewBatch()) {
            this.curBatch = this.batchCursor.tryNext();
            this.curPos = 0;
        }
        return (this.curBatch == null) ? null : this.curBatch.get(this.curPos++);
    }
    
    @Override
    public ServerCursor getServerCursor() {
        return this.batchCursor.getServerCursor();
    }
    
    @Override
    public ServerAddress getServerAddress() {
        return this.batchCursor.getServerAddress();
    }
    
    private boolean needsNewBatch() {
        return this.curBatch == null || this.curPos == this.curBatch.size();
    }
}
