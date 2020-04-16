// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.bulk.WriteRequest;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.WriteConcernResult;
import com.mongodb.connection.Connection;
import com.mongodb.assertions.Assertions;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.bulk.UpdateRequest;
import java.util.List;

public class UpdateOperation extends BaseWriteOperation
{
    private final List<UpdateRequest> updates;
    
    public UpdateOperation(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<UpdateRequest> updates) {
        super(namespace, ordered, writeConcern);
        this.updates = Assertions.notNull("update", updates);
    }
    
    public List<UpdateRequest> getUpdateRequests() {
        return this.updates;
    }
    
    @Override
    protected WriteConcernResult executeProtocol(final Connection connection) {
        return connection.update(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.updates);
    }
    
    @Override
    protected void executeProtocolAsync(final AsyncConnection connection, final SingleResultCallback<WriteConcernResult> callback) {
        connection.updateAsync(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.updates, callback);
    }
    
    @Override
    protected BulkWriteResult executeCommandProtocol(final Connection connection) {
        return connection.updateCommand(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.updates);
    }
    
    @Override
    protected void executeCommandProtocolAsync(final AsyncConnection connection, final SingleResultCallback<BulkWriteResult> callback) {
        connection.updateCommandAsync(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.updates, callback);
    }
    
    @Override
    protected WriteRequest.Type getType() {
        return WriteRequest.Type.UPDATE;
    }
    
    @Override
    protected int getCount(final BulkWriteResult bulkWriteResult) {
        return bulkWriteResult.getMatchedCount() + bulkWriteResult.getUpserts().size();
    }
    
    @Override
    protected boolean getUpdatedExisting(final BulkWriteResult bulkWriteResult) {
        return bulkWriteResult.getMatchedCount() > 0;
    }
}
