// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Loggers;
import org.bson.BsonDouble;
import java.util.List;
import org.bson.BsonArray;
import java.util.Collections;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonInt64;
import org.bson.io.OutputBuffer;
import org.bson.io.BsonOutput;
import com.mongodb.async.SingleResultCallback;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.BsonDocument;
import com.mongodb.MongoCursorNotFoundException;
import com.mongodb.event.CommandListener;
import com.mongodb.MongoNamespace;
import org.bson.codecs.Decoder;
import com.mongodb.diagnostics.logging.Logger;

class GetMoreProtocol<T> implements Protocol<QueryResult<T>>
{
    public static final Logger LOGGER;
    private static final String COMMAND_NAME = "getMore";
    private final Decoder<T> resultDecoder;
    private final MongoNamespace namespace;
    private final long cursorId;
    private final int numberToReturn;
    private CommandListener commandListener;
    
    public GetMoreProtocol(final MongoNamespace namespace, final long cursorId, final int numberToReturn, final Decoder<T> resultDecoder) {
        this.namespace = namespace;
        this.cursorId = cursorId;
        this.numberToReturn = numberToReturn;
        this.resultDecoder = resultDecoder;
    }
    
    @Override
    public QueryResult<T> execute(final InternalConnection connection) {
        if (GetMoreProtocol.LOGGER.isDebugEnabled()) {
            GetMoreProtocol.LOGGER.debug(String.format("Getting more documents from namespace %s with cursor %d on connection [%s] to server %s", this.namespace, this.cursorId, connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
        }
        final long startTimeNanos = System.nanoTime();
        GetMoreMessage message = null;
        QueryResult<T> queryResult = null;
        try {
            message = this.sendMessage(connection);
            final ResponseBuffers responseBuffers = connection.receiveMessage(message.getId());
            try {
                if (responseBuffers.getReplyHeader().isCursorNotFound()) {
                    throw new MongoCursorNotFoundException(message.getCursorId(), connection.getDescription().getServerAddress());
                }
                if (responseBuffers.getReplyHeader().isQueryFailure()) {
                    final BsonDocument errorDocument = new ReplyMessage<BsonDocument>(responseBuffers, new BsonDocumentCodec(), message.getId()).getDocuments().get(0);
                    throw ProtocolHelper.getQueryFailureException(errorDocument, connection.getDescription().getServerAddress());
                }
                queryResult = new QueryResult<T>(this.namespace, new ReplyMessage<T>(responseBuffers, this.resultDecoder, message.getId()), connection.getDescription().getServerAddress());
                if (this.commandListener != null) {
                    ProtocolHelper.sendCommandSucceededEvent(message, "getMore", this.asGetMoreCommandResponseDocument(queryResult, responseBuffers), connection.getDescription(), startTimeNanos, this.commandListener);
                }
            }
            finally {
                responseBuffers.close();
            }
            GetMoreProtocol.LOGGER.debug("Get-more completed");
            return queryResult;
        }
        catch (RuntimeException e) {
            if (this.commandListener != null) {
                ProtocolHelper.sendCommandFailedEvent(message, "getMore", connection.getDescription(), startTimeNanos, e, this.commandListener);
            }
            throw e;
        }
    }
    
    @Override
    public void executeAsync(final InternalConnection connection, final SingleResultCallback<QueryResult<T>> callback) {
        try {
            if (GetMoreProtocol.LOGGER.isDebugEnabled()) {
                GetMoreProtocol.LOGGER.debug(String.format("Asynchronously getting more documents from namespace %s with cursor %d on connection [%s] to server %s", this.namespace, this.cursorId, connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
            }
            final ByteBufferBsonOutput bsonOutput = new ByteBufferBsonOutput(connection);
            final GetMoreMessage message = new GetMoreMessage(this.namespace.getFullName(), this.cursorId, this.numberToReturn);
            ProtocolHelper.encodeMessage(message, bsonOutput);
            final SingleResultCallback<ResponseBuffers> receiveCallback = new GetMoreResultCallback<Object>(this.namespace, (SingleResultCallback<QueryResult<?>>)callback, this.resultDecoder, this.cursorId, message.getId(), connection.getDescription().getServerAddress());
            connection.sendMessageAsync(bsonOutput.getByteBuffers(), message.getId(), new SendMessageCallback<Object>(connection, bsonOutput, message.getId(), callback, receiveCallback));
        }
        catch (Throwable t) {
            callback.onResult(null, t);
        }
    }
    
    @Override
    public void setCommandListener(final CommandListener commandListener) {
        this.commandListener = commandListener;
    }
    
    private GetMoreMessage sendMessage(final InternalConnection connection) {
        final ByteBufferBsonOutput bsonOutput = new ByteBufferBsonOutput(connection);
        try {
            final GetMoreMessage message = new GetMoreMessage(this.namespace.getFullName(), this.cursorId, this.numberToReturn);
            if (this.commandListener != null) {
                ProtocolHelper.sendCommandStartedEvent(message, this.namespace.getDatabaseName(), "getMore", this.asGetMoreCommandDocument(), connection.getDescription(), this.commandListener);
            }
            message.encode(bsonOutput);
            connection.sendMessage(bsonOutput.getByteBuffers(), message.getId());
            return message;
        }
        finally {
            bsonOutput.close();
        }
    }
    
    private BsonDocument asGetMoreCommandDocument() {
        return new BsonDocument("getMore", new BsonInt64(this.cursorId)).append("collection", new BsonString(this.namespace.getCollectionName())).append("batchSize", new BsonInt32(this.numberToReturn));
    }
    
    private BsonDocument asGetMoreCommandResponseDocument(final QueryResult<T> queryResult, final ResponseBuffers responseBuffers) {
        List<ByteBufBsonDocument> rawResultDocuments = Collections.emptyList();
        if (responseBuffers.getReplyHeader().getNumberReturned() != 0) {
            responseBuffers.getBodyByteBuffer().position(0);
            rawResultDocuments = ByteBufBsonDocument.create(responseBuffers);
        }
        final BsonDocument cursorDocument = new BsonDocument("id", (queryResult.getCursor() == null) ? new BsonInt64(0L) : new BsonInt64(queryResult.getCursor().getId())).append("ns", new BsonString(this.namespace.getFullName())).append("nextBatch", new BsonArray(rawResultDocuments));
        return new BsonDocument("cursor", cursorDocument).append("ok", new BsonDouble(1.0));
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.getmore");
    }
}
