// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.bulk.BulkWriteUpsert;
import org.bson.BsonBoolean;
import com.mongodb.bulk.WriteRequest;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import java.util.Map;
import org.bson.BsonDocument;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.DuplicateKeyException;
import com.mongodb.ErrorCategory;
import com.mongodb.WriteConcernException;
import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.connection.Connection;
import com.mongodb.binding.WriteBinding;
import com.mongodb.assertions.Assertions;
import com.mongodb.MongoNamespace;
import com.mongodb.WriteConcern;
import com.mongodb.WriteConcernResult;

public abstract class BaseWriteOperation implements AsyncWriteOperation<WriteConcernResult>, WriteOperation<WriteConcernResult>
{
    private final WriteConcern writeConcern;
    private final MongoNamespace namespace;
    private final boolean ordered;
    
    public BaseWriteOperation(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern) {
        this.ordered = ordered;
        this.namespace = Assertions.notNull("namespace", namespace);
        this.writeConcern = Assertions.notNull("writeConcern", writeConcern);
    }
    
    public MongoNamespace getNamespace() {
        return this.namespace;
    }
    
    public WriteConcern getWriteConcern() {
        return this.writeConcern;
    }
    
    public boolean isOrdered() {
        return this.ordered;
    }
    
    @Override
    public WriteConcernResult execute(final WriteBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnection<WriteConcernResult>)new OperationHelper.CallableWithConnection<WriteConcernResult>() {
            @Override
            public WriteConcernResult call(final Connection connection) {
                try {
                    if (BaseWriteOperation.this.writeConcern.isAcknowledged() && OperationHelper.serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                        return BaseWriteOperation.this.translateBulkWriteResult(BaseWriteOperation.this.executeCommandProtocol(connection));
                    }
                    return BaseWriteOperation.this.executeProtocol(connection);
                }
                catch (MongoBulkWriteException e) {
                    throw BaseWriteOperation.this.convertBulkWriteException(e);
                }
            }
        });
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<WriteConcernResult> callback) {
        OperationHelper.withConnection(binding, new OperationHelper.AsyncCallableWithConnection() {
            @Override
            public void call(final AsyncConnection connection, final Throwable t) {
                if (t != null) {
                    ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<Object>)callback).onResult(null, t);
                }
                else {
                    final SingleResultCallback<WriteConcernResult> wrappedCallback = OperationHelper.releasingCallback((SingleResultCallback<WriteConcernResult>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), connection);
                    if (BaseWriteOperation.this.writeConcern.isAcknowledged() && OperationHelper.serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                        BaseWriteOperation.this.executeCommandProtocolAsync(connection, new SingleResultCallback<BulkWriteResult>() {
                            @Override
                            public void onResult(final BulkWriteResult result, final Throwable t) {
                                if (t != null) {
                                    wrappedCallback.onResult(null, BaseWriteOperation.this.translateException(t));
                                }
                                else {
                                    wrappedCallback.onResult(BaseWriteOperation.this.translateBulkWriteResult(result), null);
                                }
                            }
                        });
                    }
                    else {
                        BaseWriteOperation.this.executeProtocolAsync(connection, new SingleResultCallback<WriteConcernResult>() {
                            @Override
                            public void onResult(final WriteConcernResult result, final Throwable t) {
                                if (t != null) {
                                    wrappedCallback.onResult(null, BaseWriteOperation.this.translateException(t));
                                }
                                else {
                                    wrappedCallback.onResult(result, null);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    
    protected abstract WriteConcernResult executeProtocol(final Connection p0);
    
    protected abstract void executeProtocolAsync(final AsyncConnection p0, final SingleResultCallback<WriteConcernResult> p1);
    
    protected abstract BulkWriteResult executeCommandProtocol(final Connection p0);
    
    protected abstract void executeCommandProtocolAsync(final AsyncConnection p0, final SingleResultCallback<BulkWriteResult> p1);
    
    private MongoException translateException(final Throwable t) {
        MongoException checkedError = MongoException.fromThrowable(t);
        if (t instanceof MongoBulkWriteException) {
            checkedError = this.convertBulkWriteException((MongoBulkWriteException)t);
        }
        return checkedError;
    }
    
    private MongoException convertBulkWriteException(final MongoBulkWriteException e) {
        final BulkWriteError lastError = this.getLastError(e);
        if (lastError == null) {
            return new WriteConcernException(this.manufactureGetLastErrorResponse(e), e.getServerAddress(), this.translateBulkWriteResult(e.getWriteResult()));
        }
        if (ErrorCategory.fromErrorCode(lastError.getCode()) == ErrorCategory.DUPLICATE_KEY) {
            return new DuplicateKeyException(this.manufactureGetLastErrorResponse(e), e.getServerAddress(), this.translateBulkWriteResult(e.getWriteResult()));
        }
        return new WriteConcernException(this.manufactureGetLastErrorResponse(e), e.getServerAddress(), this.translateBulkWriteResult(e.getWriteResult()));
    }
    
    private BsonDocument manufactureGetLastErrorResponse(final MongoBulkWriteException e) {
        final BsonDocument response = new BsonDocument();
        this.addBulkWriteResultToResponse(e.getWriteResult(), response);
        if (e.getWriteConcernError() != null) {
            response.putAll(e.getWriteConcernError().getDetails());
        }
        if (this.getLastError(e) != null) {
            response.put("err", new BsonString(this.getLastError(e).getMessage()));
            response.put("code", new BsonInt32(this.getLastError(e).getCode()));
            response.putAll(this.getLastError(e).getDetails());
        }
        else if (e.getWriteConcernError() != null) {
            response.put("err", new BsonString(e.getWriteConcernError().getMessage()));
            response.put("code", new BsonInt32(e.getWriteConcernError().getCode()));
        }
        return response;
    }
    
    private void addBulkWriteResultToResponse(final BulkWriteResult bulkWriteResult, final BsonDocument response) {
        response.put("ok", new BsonInt32(1));
        if (this.getType() == WriteRequest.Type.INSERT) {
            response.put("n", new BsonInt32(0));
        }
        else if (this.getType() == WriteRequest.Type.DELETE) {
            response.put("n", new BsonInt32(bulkWriteResult.getDeletedCount()));
        }
        else if (this.getType() == WriteRequest.Type.UPDATE || this.getType() == WriteRequest.Type.REPLACE) {
            response.put("n", new BsonInt32(bulkWriteResult.getMatchedCount() + bulkWriteResult.getUpserts().size()));
            if (bulkWriteResult.getUpserts().isEmpty()) {
                response.put("updatedExisting", BsonBoolean.TRUE);
            }
            else {
                response.put("updatedExisting", BsonBoolean.FALSE);
                response.put("upserted", bulkWriteResult.getUpserts().get(0).getId());
            }
        }
    }
    
    private WriteConcernResult translateBulkWriteResult(final BulkWriteResult bulkWriteResult) {
        return WriteConcernResult.acknowledged(this.getCount(bulkWriteResult), this.getUpdatedExisting(bulkWriteResult), bulkWriteResult.getUpserts().isEmpty() ? null : bulkWriteResult.getUpserts().get(0).getId());
    }
    
    protected abstract WriteRequest.Type getType();
    
    protected abstract int getCount(final BulkWriteResult p0);
    
    protected boolean getUpdatedExisting(final BulkWriteResult bulkWriteResult) {
        return false;
    }
    
    private BulkWriteError getLastError(final MongoBulkWriteException e) {
        return e.getWriteErrors().isEmpty() ? null : e.getWriteErrors().get(e.getWriteErrors().size() - 1);
    }
}
