// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.internal.async.ErrorHandlingResultCallback;
import java.util.List;
import org.bson.ByteBuf;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.assertions.Assertions;

class UsageTrackingInternalConnection implements InternalConnection
{
    private volatile long openedAt;
    private volatile long lastUsedAt;
    private final int generation;
    private volatile InternalConnection wrapped;
    
    UsageTrackingInternalConnection(final InternalConnection wrapped, final int generation) {
        this.wrapped = wrapped;
        this.generation = generation;
        this.openedAt = Long.MAX_VALUE;
        this.lastUsedAt = this.openedAt;
    }
    
    @Override
    public void open() {
        Assertions.isTrue("open", this.wrapped != null);
        this.wrapped.open();
        this.openedAt = System.currentTimeMillis();
        this.lastUsedAt = this.openedAt;
    }
    
    @Override
    public void openAsync(final SingleResultCallback<Void> callback) {
        Assertions.isTrue("open", this.wrapped != null);
        this.wrapped.openAsync(new SingleResultCallback<Void>() {
            @Override
            public void onResult(final Void result, final Throwable t) {
                if (t != null) {
                    callback.onResult(null, t);
                }
                else {
                    UsageTrackingInternalConnection.this.openedAt = System.currentTimeMillis();
                    UsageTrackingInternalConnection.this.lastUsedAt = UsageTrackingInternalConnection.this.openedAt;
                    callback.onResult(null, null);
                }
            }
        });
    }
    
    @Override
    public void close() {
        Assertions.isTrue("open", this.wrapped != null);
        this.wrapped.close();
        this.wrapped = null;
    }
    
    @Override
    public boolean opened() {
        Assertions.isTrue("open", this.wrapped != null);
        return this.wrapped.opened();
    }
    
    @Override
    public boolean isClosed() {
        return this.wrapped == null || this.wrapped.isClosed();
    }
    
    @Override
    public ByteBuf getBuffer(final int size) {
        Assertions.isTrue("open", this.wrapped != null);
        return this.wrapped.getBuffer(size);
    }
    
    @Override
    public void sendMessage(final List<ByteBuf> byteBuffers, final int lastRequestId) {
        Assertions.isTrue("open", this.wrapped != null);
        this.wrapped.sendMessage(byteBuffers, lastRequestId);
        this.lastUsedAt = System.currentTimeMillis();
    }
    
    @Override
    public ResponseBuffers receiveMessage(final int responseTo) {
        Assertions.isTrue("open", this.wrapped != null);
        final ResponseBuffers responseBuffers = this.wrapped.receiveMessage(responseTo);
        this.lastUsedAt = System.currentTimeMillis();
        return responseBuffers;
    }
    
    @Override
    public void sendMessageAsync(final List<ByteBuf> byteBuffers, final int lastRequestId, final SingleResultCallback<Void> callback) {
        Assertions.isTrue("open", this.wrapped != null);
        final SingleResultCallback<Void> wrappedCallback = ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<Void>)new SingleResultCallback<Void>() {
            @Override
            public void onResult(final Void result, final Throwable t) {
                UsageTrackingInternalConnection.this.lastUsedAt = System.currentTimeMillis();
                callback.onResult(result, t);
            }
        });
        this.wrapped.sendMessageAsync(byteBuffers, lastRequestId, wrappedCallback);
    }
    
    @Override
    public void receiveMessageAsync(final int responseTo, final SingleResultCallback<ResponseBuffers> callback) {
        Assertions.isTrue("open", this.wrapped != null);
        final SingleResultCallback<ResponseBuffers> wrappedCallback = ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<ResponseBuffers>)new SingleResultCallback<ResponseBuffers>() {
            @Override
            public void onResult(final ResponseBuffers result, final Throwable t) {
                UsageTrackingInternalConnection.this.lastUsedAt = System.currentTimeMillis();
                callback.onResult(result, t);
            }
        });
        this.wrapped.receiveMessageAsync(responseTo, wrappedCallback);
    }
    
    @Override
    public ConnectionDescription getDescription() {
        Assertions.isTrue("open", this.wrapped != null);
        return this.wrapped.getDescription();
    }
    
    int getGeneration() {
        return this.generation;
    }
    
    long getOpenedAt() {
        return this.openedAt;
    }
    
    long getLastUsedAt() {
        return this.lastUsedAt;
    }
}
