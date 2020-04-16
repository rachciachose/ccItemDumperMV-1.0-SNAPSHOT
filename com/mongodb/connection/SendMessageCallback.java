// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.io.OutputBuffer;
import com.mongodb.async.SingleResultCallback;

class SendMessageCallback<T> implements SingleResultCallback<Void>
{
    private final OutputBuffer buffer;
    private final InternalConnection connection;
    private final SingleResultCallback<ResponseBuffers> receiveMessageCallback;
    private final int requestId;
    private final SingleResultCallback<T> callback;
    
    SendMessageCallback(final InternalConnection connection, final OutputBuffer buffer, final int requestId, final SingleResultCallback<T> callback, final SingleResultCallback<ResponseBuffers> receiveMessageCallback) {
        this.buffer = buffer;
        this.connection = connection;
        this.callback = callback;
        this.receiveMessageCallback = receiveMessageCallback;
        this.requestId = requestId;
    }
    
    @Override
    public void onResult(final Void result, final Throwable t) {
        this.buffer.close();
        if (t != null) {
            this.callback.onResult(null, t);
        }
        else {
            this.connection.receiveMessageAsync(this.requestId, this.receiveMessageCallback);
        }
    }
}
