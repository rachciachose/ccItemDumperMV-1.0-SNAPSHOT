// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Loggers;
import com.mongodb.bulk.WriteRequest;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.assertions.Assertions;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.bulk.DeleteRequest;
import java.util.List;
import com.mongodb.diagnostics.logging.Logger;

class DeleteCommandProtocol extends WriteCommandProtocol
{
    private static final Logger LOGGER;
    private final List<DeleteRequest> deleteRequests;
    
    public DeleteCommandProtocol(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<DeleteRequest> deletes) {
        super(namespace, ordered, writeConcern);
        this.deleteRequests = Assertions.notNull("removes", deletes);
    }
    
    @Override
    public BulkWriteResult execute(final InternalConnection connection) {
        if (DeleteCommandProtocol.LOGGER.isDebugEnabled()) {
            DeleteCommandProtocol.LOGGER.debug(String.format("Deleting documents from namespace %s on connection [%s] to server %s", this.getNamespace(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
        }
        final BulkWriteResult writeResult = super.execute(connection);
        DeleteCommandProtocol.LOGGER.debug("Delete completed");
        return writeResult;
    }
    
    @Override
    public void executeAsync(final InternalConnection connection, final SingleResultCallback<BulkWriteResult> callback) {
        try {
            if (DeleteCommandProtocol.LOGGER.isDebugEnabled()) {
                DeleteCommandProtocol.LOGGER.debug(String.format("Asynchronously deleting documents from namespace %s on connection [%s] to server %s", this.getNamespace(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
            }
            super.executeAsync(connection, new SingleResultCallback<BulkWriteResult>() {
                @Override
                public void onResult(final BulkWriteResult result, final Throwable t) {
                    if (t != null) {
                        callback.onResult(null, t);
                    }
                    else {
                        DeleteCommandProtocol.LOGGER.debug("Asynchronous delete completed");
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
    protected WriteRequest.Type getType() {
        return WriteRequest.Type.DELETE;
    }
    
    @Override
    protected DeleteCommandMessage createRequestMessage(final MessageSettings messageSettings) {
        return new DeleteCommandMessage(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.deleteRequests, messageSettings);
    }
    
    @Override
    protected Logger getLogger() {
        return DeleteCommandProtocol.LOGGER;
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.delete");
    }
}
