// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Loggers;
import com.mongodb.MongoCursorNotFoundException;
import com.mongodb.ServerAddress;
import org.bson.codecs.Decoder;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.MongoNamespace;
import com.mongodb.diagnostics.logging.Logger;

class GetMoreResultCallback<T> extends ResponseCallback
{
    public static final Logger LOGGER;
    private final MongoNamespace namespace;
    private final SingleResultCallback<QueryResult<T>> callback;
    private final Decoder<T> decoder;
    private final long cursorId;
    
    public GetMoreResultCallback(final MongoNamespace namespace, final SingleResultCallback<QueryResult<T>> callback, final Decoder<T> decoder, final long cursorId, final long requestId, final ServerAddress serverAddress) {
        super(requestId, serverAddress);
        this.namespace = namespace;
        this.callback = callback;
        this.decoder = decoder;
        this.cursorId = cursorId;
    }
    
    @Override
    protected void callCallback(final ResponseBuffers responseBuffers, final Throwable t) {
        try {
            if (t != null) {
                this.callback.onResult(null, t);
            }
            else if (responseBuffers.getReplyHeader().isCursorNotFound()) {
                this.callback.onResult(null, new MongoCursorNotFoundException(this.cursorId, this.getServerAddress()));
            }
            else {
                final QueryResult<T> result = new QueryResult<T>(this.namespace, new ReplyMessage<T>(responseBuffers, this.decoder, this.getRequestId()), this.getServerAddress());
                if (GetMoreResultCallback.LOGGER.isDebugEnabled()) {
                    GetMoreResultCallback.LOGGER.debug(String.format("GetMore results received %s documents with cursor %s", result.getResults().size(), result.getCursor()));
                }
                this.callback.onResult(result, null);
            }
        }
        catch (Throwable t2) {
            this.callback.onResult(null, t2);
            try {
                if (responseBuffers != null) {
                    responseBuffers.close();
                }
            }
            catch (Throwable t2) {
                GetMoreResultCallback.LOGGER.debug("GetMore ResponseBuffer close exception", t2);
            }
        }
        finally {
            try {
                if (responseBuffers != null) {
                    responseBuffers.close();
                }
            }
            catch (Throwable t3) {
                GetMoreResultCallback.LOGGER.debug("GetMore ResponseBuffer close exception", t3);
            }
        }
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.getmore");
    }
}
