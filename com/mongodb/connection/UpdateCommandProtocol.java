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
import com.mongodb.bulk.UpdateRequest;
import java.util.List;
import com.mongodb.diagnostics.logging.Logger;

class UpdateCommandProtocol extends WriteCommandProtocol
{
    private static final Logger LOGGER;
    private final List<UpdateRequest> updates;
    
    public UpdateCommandProtocol(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<UpdateRequest> updates) {
        super(namespace, ordered, writeConcern);
        this.updates = Assertions.notNull("update", updates);
    }
    
    @Override
    public BulkWriteResult execute(final InternalConnection connection) {
        if (UpdateCommandProtocol.LOGGER.isDebugEnabled()) {
            UpdateCommandProtocol.LOGGER.debug(String.format("Updating documents in namespace %s on connection [%s] to server %s", this.getNamespace(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
        }
        final BulkWriteResult writeResult = super.execute(connection);
        UpdateCommandProtocol.LOGGER.debug("Update completed");
        return writeResult;
    }
    
    @Override
    public void executeAsync(final InternalConnection connection, final SingleResultCallback<BulkWriteResult> callback) {
        try {
            if (UpdateCommandProtocol.LOGGER.isDebugEnabled()) {
                UpdateCommandProtocol.LOGGER.debug(String.format("Asynchronously updating documents in namespace %s on connection [%s] to server %s", this.getNamespace(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
            }
            super.executeAsync(connection, new SingleResultCallback<BulkWriteResult>() {
                @Override
                public void onResult(final BulkWriteResult result, final Throwable t) {
                    if (t != null) {
                        callback.onResult(null, t);
                    }
                    else {
                        UpdateCommandProtocol.LOGGER.debug("Asynchronous update completed");
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
        return WriteRequest.Type.UPDATE;
    }
    
    @Override
    protected UpdateCommandMessage createRequestMessage(final MessageSettings messageSettings) {
        return new UpdateCommandMessage(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.updates, messageSettings);
    }
    
    @Override
    protected Logger getLogger() {
        return UpdateCommandProtocol.LOGGER;
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.update");
    }
}
