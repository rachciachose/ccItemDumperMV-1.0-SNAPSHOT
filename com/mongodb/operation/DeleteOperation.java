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
import com.mongodb.bulk.DeleteRequest;
import java.util.List;

public class DeleteOperation extends BaseWriteOperation
{
    private final List<DeleteRequest> deleteRequests;
    
    public DeleteOperation(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<DeleteRequest> deleteRequests) {
        super(namespace, ordered, writeConcern);
        this.deleteRequests = Assertions.notNull("removes", deleteRequests);
    }
    
    public List<DeleteRequest> getDeleteRequests() {
        return this.deleteRequests;
    }
    
    @Override
    protected WriteConcernResult executeProtocol(final Connection connection) {
        return connection.delete(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.deleteRequests);
    }
    
    @Override
    protected void executeProtocolAsync(final AsyncConnection connection, final SingleResultCallback<WriteConcernResult> callback) {
        connection.deleteAsync(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.deleteRequests, callback);
    }
    
    @Override
    protected BulkWriteResult executeCommandProtocol(final Connection connection) {
        return connection.deleteCommand(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.deleteRequests);
    }
    
    @Override
    protected void executeCommandProtocolAsync(final AsyncConnection connection, final SingleResultCallback<BulkWriteResult> callback) {
        connection.deleteCommandAsync(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.deleteRequests, callback);
    }
    
    @Override
    protected WriteRequest.Type getType() {
        return WriteRequest.Type.DELETE;
    }
    
    @Override
    protected int getCount(final BulkWriteResult bulkWriteResult) {
        return bulkWriteResult.getDeletedCount();
    }
}
