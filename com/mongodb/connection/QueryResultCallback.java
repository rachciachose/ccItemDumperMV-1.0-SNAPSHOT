// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Loggers;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.BsonDocument;
import com.mongodb.ServerAddress;
import org.bson.codecs.Decoder;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.MongoNamespace;
import com.mongodb.diagnostics.logging.Logger;

class QueryResultCallback<T> extends ResponseCallback
{
    public static final Logger LOGGER;
    private final MongoNamespace namespace;
    private final SingleResultCallback<QueryResult<T>> callback;
    private final Decoder<T> decoder;
    
    public QueryResultCallback(final MongoNamespace namespace, final SingleResultCallback<QueryResult<T>> callback, final Decoder<T> decoder, final int requestId, final ServerAddress serverAddress) {
        super(requestId, serverAddress);
        this.namespace = namespace;
        this.callback = callback;
        this.decoder = decoder;
    }
    
    @Override
    protected void callCallback(final ResponseBuffers responseBuffers, final Throwable t) {
        try {
            if (t != null) {
                this.callback.onResult(null, t);
            }
            else if (responseBuffers.getReplyHeader().isQueryFailure()) {
                final BsonDocument errorDocument = new ReplyMessage<BsonDocument>(responseBuffers, new BsonDocumentCodec(), this.getRequestId()).getDocuments().get(0);
                this.callback.onResult(null, ProtocolHelper.getQueryFailureException(errorDocument, this.getServerAddress()));
            }
            else {
                final QueryResult<T> result = new QueryResult<T>(this.namespace, new ReplyMessage<T>(responseBuffers, this.decoder, this.getRequestId()), this.getServerAddress());
                if (QueryResultCallback.LOGGER.isDebugEnabled()) {
                    QueryResultCallback.LOGGER.debug(String.format("Query results received %s documents with cursor %s", result.getResults().size(), result.getCursor()));
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
                QueryResultCallback.LOGGER.debug("GetMore ResponseBuffer close exception", t2);
            }
        }
        finally {
            try {
                if (responseBuffers != null) {
                    responseBuffers.close();
                }
            }
            catch (Throwable t3) {
                QueryResultCallback.LOGGER.debug("GetMore ResponseBuffer close exception", t3);
            }
        }
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.query");
    }
}
