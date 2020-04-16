// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.MongoInternalException;
import com.mongodb.MongoException;
import com.mongodb.MongoInterruptedException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

class FutureAsyncCompletionHandler<T> implements AsyncCompletionHandler<T>
{
    private final CountDownLatch latch;
    private volatile T result;
    private volatile Throwable error;
    
    FutureAsyncCompletionHandler() {
        this.latch = new CountDownLatch(1);
    }
    
    @Override
    public void completed(final T result) {
        this.result = result;
        this.latch.countDown();
    }
    
    @Override
    public void failed(final Throwable t) {
        this.error = t;
        this.latch.countDown();
    }
    
    public void getOpen() throws IOException {
        this.get("Opening");
    }
    
    public void getWrite() throws IOException {
        this.get("Writing to");
    }
    
    public T getRead() throws IOException {
        return this.get("Reading from");
    }
    
    private T get(final String prefix) throws IOException {
        try {
            this.latch.await();
        }
        catch (InterruptedException e) {
            throw new MongoInterruptedException(prefix + " the AsynchronousSocketChannelStream failed", e);
        }
        if (this.error == null) {
            return this.result;
        }
        if (this.error instanceof IOException) {
            throw (IOException)this.error;
        }
        if (this.error instanceof MongoException) {
            throw (MongoException)this.error;
        }
        throw new MongoInternalException(prefix + " the AsynchronousSocketChannelStream failed", this.error);
    }
}
