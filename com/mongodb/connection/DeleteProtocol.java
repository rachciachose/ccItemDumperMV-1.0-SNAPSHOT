// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Loggers;
import org.bson.BsonArray;
import java.util.Collections;
import org.bson.BsonInt32;
import org.bson.BsonValue;
import org.bson.BsonDocument;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.WriteConcernResult;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.bulk.DeleteRequest;
import java.util.List;
import com.mongodb.diagnostics.logging.Logger;

class DeleteProtocol extends WriteProtocol
{
    private static final Logger LOGGER;
    private final List<DeleteRequest> deletes;
    
    public DeleteProtocol(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<DeleteRequest> deletes) {
        super(namespace, ordered, writeConcern);
        this.deletes = deletes;
    }
    
    @Override
    public WriteConcernResult execute(final InternalConnection connection) {
        if (DeleteProtocol.LOGGER.isDebugEnabled()) {
            DeleteProtocol.LOGGER.debug(String.format("Deleting documents from namespace %s on connection [%s] to server %s", this.getNamespace(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
        }
        final WriteConcernResult writeConcernResult = super.execute(connection);
        DeleteProtocol.LOGGER.debug("Delete completed");
        return writeConcernResult;
    }
    
    @Override
    public void executeAsync(final InternalConnection connection, final SingleResultCallback<WriteConcernResult> callback) {
        try {
            if (DeleteProtocol.LOGGER.isDebugEnabled()) {
                DeleteProtocol.LOGGER.debug(String.format("Asynchronously deleting documents in namespace %s on connection [%s] to server %s", this.getNamespace(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
            }
            super.executeAsync(connection, new SingleResultCallback<WriteConcernResult>() {
                @Override
                public void onResult(final WriteConcernResult result, final Throwable t) {
                    if (t != null) {
                        callback.onResult(null, t);
                    }
                    else {
                        DeleteProtocol.LOGGER.debug("Asynchronous delete completed");
                        callback.onResult(result, null);
                    }
                }
            });
        }
        catch (Throwable t) {
            callback.onResult(null, t);
        }
    }
    
    @Override
    protected BsonDocument getAsWriteCommand(final ByteBufferBsonOutput bsonOutput, final int firstDocumentPosition) {
        final BsonDocument deleteDocument = new BsonDocument("q", ByteBufBsonDocument.createOne(bsonOutput, firstDocumentPosition)).append("limit", this.deletes.get(0).isMulti() ? new BsonInt32(0) : new BsonInt32(1));
        return this.getBaseCommandDocument().append("deletes", new BsonArray(Collections.singletonList(deleteDocument)));
    }
    
    @Override
    protected String getCommandName() {
        return "delete";
    }
    
    @Override
    protected RequestMessage createRequestMessage(final MessageSettings settings) {
        return new DeleteMessage(this.getNamespace().getFullName(), this.deletes, settings);
    }
    
    @Override
    protected void appendToWriteCommandResponseDocument(final RequestMessage curMessage, final RequestMessage nextMessage, final WriteConcernResult writeConcernResult, final BsonDocument response) {
        response.append("n", new BsonInt32(writeConcernResult.getCount()));
    }
    
    @Override
    protected Logger getLogger() {
        return DeleteProtocol.LOGGER;
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.delete");
    }
}
