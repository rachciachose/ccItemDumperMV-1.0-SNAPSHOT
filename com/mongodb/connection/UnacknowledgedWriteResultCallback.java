// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.WriteConcern;
import org.bson.io.OutputBuffer;
import com.mongodb.MongoNamespace;
import com.mongodb.WriteConcernResult;
import com.mongodb.async.SingleResultCallback;

class UnacknowledgedWriteResultCallback implements SingleResultCallback<Void>
{
    private final SingleResultCallback<WriteConcernResult> callback;
    private final MongoNamespace namespace;
    private final RequestMessage nextMessage;
    private final OutputBuffer writtenBuffer;
    private final boolean ordered;
    private final InternalConnection connection;
    
    UnacknowledgedWriteResultCallback(final SingleResultCallback<WriteConcernResult> callback, final MongoNamespace namespace, final RequestMessage nextMessage, final boolean ordered, final OutputBuffer writtenBuffer, final InternalConnection connection) {
        this.callback = callback;
        this.namespace = namespace;
        this.nextMessage = nextMessage;
        this.ordered = ordered;
        this.connection = connection;
        this.writtenBuffer = writtenBuffer;
    }
    
    @Override
    public void onResult(final Void result, final Throwable t) {
        this.writtenBuffer.close();
        if (t != null) {
            this.callback.onResult(null, t);
        }
        else if (this.nextMessage != null) {
            new GenericWriteProtocol(this.namespace, this.nextMessage, this.ordered, WriteConcern.UNACKNOWLEDGED).executeAsync(this.connection, this.callback);
        }
        else {
            this.callback.onResult(WriteConcernResult.unacknowledged(), null);
        }
    }
}
