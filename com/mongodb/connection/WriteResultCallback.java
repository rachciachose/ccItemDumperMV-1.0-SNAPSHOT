// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.codecs.Decoder;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.WriteConcernResult;
import com.mongodb.async.SingleResultCallback;
import org.bson.BsonDocument;

class WriteResultCallback extends CommandResultBaseCallback<BsonDocument>
{
    private final SingleResultCallback<WriteConcernResult> callback;
    private final MongoNamespace namespace;
    private final RequestMessage nextMessage;
    private final boolean ordered;
    private final WriteConcern writeConcern;
    private final InternalConnection connection;
    
    public WriteResultCallback(final SingleResultCallback<WriteConcernResult> callback, final Decoder<BsonDocument> decoder, final MongoNamespace namespace, final RequestMessage nextMessage, final boolean ordered, final WriteConcern writeConcern, final long requestId, final InternalConnection connection) {
        super(decoder, requestId, connection.getDescription().getServerAddress());
        this.callback = callback;
        this.namespace = namespace;
        this.nextMessage = nextMessage;
        this.ordered = ordered;
        this.writeConcern = writeConcern;
        this.connection = connection;
    }
    
    @Override
    protected void callCallback(final BsonDocument result, final Throwable t) {
        if (t != null) {
            this.callback.onResult(null, t);
        }
        else {
            try {
                final WriteConcernResult writeConcernResult = ProtocolHelper.getWriteResult(result, this.connection.getDescription().getServerAddress());
                if (this.nextMessage != null) {
                    new GenericWriteProtocol(this.namespace, this.nextMessage, this.ordered, this.writeConcern).executeAsync(this.connection, this.callback);
                }
                else {
                    this.callback.onResult(writeConcernResult, null);
                }
            }
            catch (Throwable t2) {
                this.callback.onResult(null, t2);
            }
        }
    }
}
