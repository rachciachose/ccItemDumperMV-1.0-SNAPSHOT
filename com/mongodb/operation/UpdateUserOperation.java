// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.MongoNamespace;
import com.mongodb.bulk.WriteRequest;
import com.mongodb.WriteConcernResult;
import com.mongodb.Function;
import org.bson.BsonDocument;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import java.util.Arrays;
import com.mongodb.bulk.UpdateRequest;
import com.mongodb.WriteConcern;
import com.mongodb.connection.Connection;
import com.mongodb.binding.WriteBinding;
import com.mongodb.assertions.Assertions;
import com.mongodb.MongoCredential;

public class UpdateUserOperation implements AsyncWriteOperation<Void>, WriteOperation<Void>
{
    private final MongoCredential credential;
    private final boolean readOnly;
    
    public UpdateUserOperation(final MongoCredential credential, final boolean readOnly) {
        this.credential = Assertions.notNull("credential", credential);
        this.readOnly = readOnly;
    }
    
    public MongoCredential getCredential() {
        return this.credential;
    }
    
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    @Override
    public Void execute(final WriteBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnection<Void>)new OperationHelper.CallableWithConnection<Void>() {
            @Override
            public Void call(final Connection connection) {
                if (OperationHelper.serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                    CommandOperationHelper.executeWrappedCommandProtocol(binding, UpdateUserOperation.this.credential.getSource(), UpdateUserOperation.this.getCommand(), connection);
                }
                else {
                    connection.update(UpdateUserOperation.this.getNamespace(), true, WriteConcern.ACKNOWLEDGED, Arrays.asList(UpdateUserOperation.this.getUpdateRequest()));
                }
                return null;
            }
        });
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<Void> callback) {
        OperationHelper.withConnection(binding, new OperationHelper.AsyncCallableWithConnection() {
            @Override
            public void call(final AsyncConnection connection, final Throwable t) {
                if (t != null) {
                    ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<Object>)callback).onResult(null, t);
                }
                else {
                    final SingleResultCallback<Void> wrappedCallback = OperationHelper.releasingCallback((SingleResultCallback<Void>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), connection);
                    if (OperationHelper.serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, UpdateUserOperation.this.credential.getSource(), UpdateUserOperation.this.getCommand(), connection, new OperationHelper.VoidTransformer<BsonDocument>(), wrappedCallback);
                    }
                    else {
                        connection.updateAsync(UpdateUserOperation.this.getNamespace(), true, WriteConcern.ACKNOWLEDGED, Arrays.asList(UpdateUserOperation.this.getUpdateRequest()), new SingleResultCallback<WriteConcernResult>() {
                            @Override
                            public void onResult(final WriteConcernResult result, final Throwable t) {
                                wrappedCallback.onResult(null, t);
                            }
                        });
                    }
                }
            }
        });
    }
    
    private UpdateRequest getUpdateRequest() {
        return new UpdateRequest(UserOperationHelper.asCollectionQueryDocument(this.credential), UserOperationHelper.asCollectionUpdateDocument(this.credential, this.readOnly), WriteRequest.Type.REPLACE);
    }
    
    private MongoNamespace getNamespace() {
        return new MongoNamespace(this.credential.getSource(), "system.users");
    }
    
    private BsonDocument getCommand() {
        return UserOperationHelper.asCommandDocument(this.credential, this.readOnly, "updateUser");
    }
}
