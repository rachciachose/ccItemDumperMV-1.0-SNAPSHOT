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
import com.mongodb.bulk.InsertRequest;
import java.util.List;

public class InsertOperation extends BaseWriteOperation
{
    private final List<InsertRequest> insertRequests;
    
    public InsertOperation(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<InsertRequest> insertRequests) {
        super(namespace, ordered, writeConcern);
        this.insertRequests = Assertions.notNull("insertRequests", insertRequests);
    }
    
    public List<InsertRequest> getInsertRequests() {
        return this.insertRequests;
    }
    
    @Override
    protected WriteConcernResult executeProtocol(final Connection connection) {
        return connection.insert(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.insertRequests);
    }
    
    @Override
    protected void executeProtocolAsync(final AsyncConnection connection, final SingleResultCallback<WriteConcernResult> callback) {
        connection.insertAsync(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.insertRequests, callback);
    }
    
    @Override
    protected BulkWriteResult executeCommandProtocol(final Connection connection) {
        return connection.insertCommand(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.insertRequests);
    }
    
    @Override
    protected void executeCommandProtocolAsync(final AsyncConnection connection, final SingleResultCallback<BulkWriteResult> callback) {
        connection.insertCommandAsync(this.getNamespace(), this.isOrdered(), this.getWriteConcern(), this.insertRequests, callback);
    }
    
    @Override
    protected WriteRequest.Type getType() {
        return WriteRequest.Type.INSERT;
    }
    
    @Override
    protected int getCount(final BulkWriteResult bulkWriteResult) {
        return 0;
    }
}
