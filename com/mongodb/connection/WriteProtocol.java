// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Logger;
import java.util.Map;
import org.bson.BsonBoolean;
import org.bson.io.OutputBuffer;
import com.mongodb.async.SingleResultCallback;
import java.util.List;
import org.bson.BsonArray;
import java.util.Collections;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonInt32;
import com.mongodb.WriteConcernException;
import org.bson.codecs.Decoder;
import org.bson.BsonDocument;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.io.BsonOutput;
import com.mongodb.event.CommandListener;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.WriteConcernResult;

abstract class WriteProtocol implements Protocol<WriteConcernResult>
{
    private final MongoNamespace namespace;
    private final boolean ordered;
    private final WriteConcern writeConcern;
    private CommandListener commandListener;
    
    public WriteProtocol(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern) {
        this.namespace = namespace;
        this.ordered = ordered;
        this.writeConcern = writeConcern;
    }
    
    @Override
    public void setCommandListener(final CommandListener commandListener) {
        this.commandListener = commandListener;
    }
    
    @Override
    public WriteConcernResult execute(final InternalConnection connection) {
        WriteConcernResult writeConcernResult = null;
        RequestMessage nextMessage = null;
        do {
            final long startTimeNanos = System.nanoTime();
            boolean sentCommandStartedEvent = false;
            final ByteBufferBsonOutput bsonOutput = new ByteBufferBsonOutput(connection);
            RequestMessage.EncodingMetadata encodingMetadata;
            int messageId;
            try {
                if (nextMessage == null) {
                    nextMessage = this.createRequestMessage(ProtocolHelper.getMessageSettings(connection.getDescription()));
                }
                encodingMetadata = nextMessage.encodeWithMetadata(bsonOutput);
                if (this.commandListener != null) {
                    ProtocolHelper.sendCommandStartedEvent(nextMessage, this.namespace.getDatabaseName(), this.getCommandName(), this.getAsWriteCommand(bsonOutput, encodingMetadata.getFirstDocumentPosition()), connection.getDescription(), this.commandListener);
                    sentCommandStartedEvent = true;
                }
                messageId = nextMessage.getId();
                if (this.shouldAcknowledge(encodingMetadata)) {
                    final CommandMessage getLastErrorMessage = new CommandMessage(new MongoNamespace(this.getNamespace().getDatabaseName(), "$cmd").getFullName(), this.createGetLastErrorCommandDocument(), false, ProtocolHelper.getMessageSettings(connection.getDescription()));
                    getLastErrorMessage.encode(bsonOutput);
                    messageId = getLastErrorMessage.getId();
                }
                connection.sendMessage(bsonOutput.getByteBuffers(), messageId);
            }
            catch (RuntimeException e) {
                if (this.commandListener != null && sentCommandStartedEvent) {
                    ProtocolHelper.sendCommandFailedEvent(nextMessage, this.getCommandName(), connection.getDescription(), 0L, e, this.commandListener);
                }
                throw e;
            }
            finally {
                bsonOutput.close();
            }
            if (this.shouldAcknowledge(encodingMetadata)) {
                final ResponseBuffers responseBuffers = connection.receiveMessage(messageId);
                try {
                    final ReplyMessage<BsonDocument> replyMessage = new ReplyMessage<BsonDocument>(responseBuffers, new BsonDocumentCodec(), messageId);
                    writeConcernResult = ProtocolHelper.getWriteResult(replyMessage.getDocuments().get(0), connection.getDescription().getServerAddress());
                }
                catch (WriteConcernException e2) {
                    if (this.commandListener != null) {
                        ProtocolHelper.sendCommandSucceededEvent(nextMessage, this.getCommandName(), this.getResponseDocument(nextMessage, encodingMetadata.getNextMessage(), e2.getWriteConcernResult(), e2), connection.getDescription(), 0L, this.commandListener);
                    }
                    if (this.writeConcern.isAcknowledged()) {
                        throw e2;
                    }
                    break;
                }
                catch (RuntimeException e3) {
                    if (this.commandListener != null) {
                        ProtocolHelper.sendCommandFailedEvent(nextMessage, this.getCommandName(), connection.getDescription(), 0L, e3, this.commandListener);
                    }
                    throw e3;
                }
                finally {
                    responseBuffers.close();
                }
            }
            if (this.commandListener != null) {
                ProtocolHelper.sendCommandSucceededEvent(nextMessage, this.getCommandName(), this.getResponseDocument(nextMessage, encodingMetadata.getNextMessage(), writeConcernResult, null), connection.getDescription(), startTimeNanos, this.commandListener);
            }
            nextMessage = encodingMetadata.getNextMessage();
        } while (nextMessage != null);
        return this.writeConcern.isAcknowledged() ? writeConcernResult : WriteConcernResult.unacknowledged();
    }
    
    private BsonDocument getResponseDocument(final RequestMessage curMessage, final RequestMessage nextMessage, final WriteConcernResult writeConcernResult, final WriteConcernException writeConcernException) {
        final BsonDocument response = new BsonDocument("ok", new BsonInt32(1));
        if (this.writeConcern.isAcknowledged()) {
            if (writeConcernException == null) {
                this.appendToWriteCommandResponseDocument(curMessage, nextMessage, writeConcernResult, response);
            }
            else {
                response.put("n", new BsonInt32(0));
                final BsonDocument writeErrorDocument = new BsonDocument("index", new BsonInt32(0)).append("code", new BsonInt32(writeConcernException.getErrorCode()));
                if (writeConcernException.getErrorMessage() != null) {
                    writeErrorDocument.append("errmsg", new BsonString(writeConcernException.getErrorMessage()));
                }
                response.put("writeErrors", new BsonArray(Collections.singletonList(writeErrorDocument)));
            }
        }
        return response;
    }
    
    protected abstract void appendToWriteCommandResponseDocument(final RequestMessage p0, final RequestMessage p1, final WriteConcernResult p2, final BsonDocument p3);
    
    @Override
    public void executeAsync(final InternalConnection connection, final SingleResultCallback<WriteConcernResult> callback) {
        try {
            final ByteBufferBsonOutput bsonOutput = new ByteBufferBsonOutput(connection);
            final RequestMessage requestMessage = this.createRequestMessage(ProtocolHelper.getMessageSettings(connection.getDescription()));
            final RequestMessage nextMessage = ProtocolHelper.encodeMessage(requestMessage, bsonOutput);
            if (this.writeConcern.isAcknowledged()) {
                final CommandMessage getLastErrorMessage = new CommandMessage(new MongoNamespace(this.getNamespace().getDatabaseName(), "$cmd").getFullName(), this.createGetLastErrorCommandDocument(), false, ProtocolHelper.getMessageSettings(connection.getDescription()));
                ProtocolHelper.encodeMessage(getLastErrorMessage, bsonOutput);
                final SingleResultCallback<ResponseBuffers> recieveCallback = new WriteResultCallback(callback, new BsonDocumentCodec(), this.getNamespace(), nextMessage, this.ordered, this.writeConcern, getLastErrorMessage.getId(), connection);
                connection.sendMessageAsync(bsonOutput.getByteBuffers(), getLastErrorMessage.getId(), new SendMessageCallback<Object>(connection, bsonOutput, getLastErrorMessage.getId(), callback, recieveCallback));
            }
            else {
                connection.sendMessageAsync(bsonOutput.getByteBuffers(), requestMessage.getId(), new UnacknowledgedWriteResultCallback(callback, this.getNamespace(), nextMessage, this.ordered, bsonOutput, connection));
            }
        }
        catch (Throwable t) {
            callback.onResult(null, t);
        }
    }
    
    protected abstract BsonDocument getAsWriteCommand(final ByteBufferBsonOutput p0, final int p1);
    
    protected BsonDocument getBaseCommandDocument() {
        final BsonDocument baseCommandDocument = new BsonDocument(this.getCommandName(), new BsonString(this.getNamespace().getCollectionName())).append("ordered", BsonBoolean.valueOf(this.isOrdered()));
        if (!this.writeConcern.isServerDefault()) {
            baseCommandDocument.append("writeConcern", this.writeConcern.asDocument());
        }
        return baseCommandDocument;
    }
    
    protected abstract String getCommandName();
    
    private boolean shouldAcknowledge(final RequestMessage.EncodingMetadata encodingMetadata) {
        return this.writeConcern.isAcknowledged() || (this.isOrdered() && encodingMetadata.getNextMessage() != null);
    }
    
    private BsonDocument createGetLastErrorCommandDocument() {
        final BsonDocument command = new BsonDocument("getlasterror", new BsonInt32(1));
        command.putAll(this.writeConcern.asDocument());
        return command;
    }
    
    protected abstract RequestMessage createRequestMessage(final MessageSettings p0);
    
    protected MongoNamespace getNamespace() {
        return this.namespace;
    }
    
    protected boolean isOrdered() {
        return this.ordered;
    }
    
    protected WriteConcern getWriteConcern() {
        return this.writeConcern;
    }
    
    protected abstract Logger getLogger();
}
