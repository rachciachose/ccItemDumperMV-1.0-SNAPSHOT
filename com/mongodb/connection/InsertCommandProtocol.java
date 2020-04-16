// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Loggers;
import com.mongodb.bulk.WriteRequest;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.bulk.InsertRequest;
import java.util.List;
import com.mongodb.diagnostics.logging.Logger;

class InsertCommandProtocol extends WriteCommandProtocol
{
    private static final Logger LOGGER;
    private final List<InsertRequest> insertRequests;
    
    public InsertCommandProtocol(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<InsertRequest> insertRequests) {
        super(namespace, ordered, writeConcern);
        this.insertRequests = insertRequests;
    }
    
    @Override
    public BulkWriteResult execute(final InternalConnection connection) {
        if (InsertCommandProtocol.LOGGER.isDebugEnabled()) {
            InsertCommandProtocol.LOGGER.debug(String.format("Inserting %d documents into namespace %s on connection [%s] to server %s", this.insertRequests.size(), this.getNamespace(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
        }
        final BulkWriteResult writeResult = super.execute(connection);
        InsertCommandProtocol.LOGGER.debug("Insert completed");
        return writeResult;
    }
    
    @Override
    public void executeAsync(final InternalConnection connection, final SingleResultCallback<BulkWriteResult> callback) {
        try {
            if (InsertCommandProtocol.LOGGER.isDebugEnabled()) {
                InsertCommandProtocol.LOGGER.debug(String.format("Asynchronously inserting %d documents into namespace %s on connection [%s] to server %s", this.insertRequests.size(), this.getNamespace(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
            }
            super.executeAsync(connection, new SingleResultCallback<BulkWriteResult>() {
                @Override
                public void onResult(final BulkWriteResult result, final Throwable t) {
                    if (t != null) {
                        callback.onResult(null, t);
                    }
                    else {
                        InsertCommandProtocol.LOGGER.debug("Asynchronous insert completed");
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
        return WriteRequest.Type.INSERT;
    }
    
    @Override
    protected InsertCommandMessage createRequestMessage(final MessageSettings messageSettings) {
        return new InsertCommandMessage(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.insertRequests, messageSettings);
    }
    
    @Override
    protected Logger getLogger() {
        return InsertCommandProtocol.LOGGER;
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.insert");
    }
}
