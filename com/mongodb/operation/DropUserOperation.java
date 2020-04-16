// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.MongoNamespace;
import com.mongodb.WriteConcernResult;
import com.mongodb.Function;
import org.bson.BsonDocument;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import java.util.Arrays;
import com.mongodb.bulk.DeleteRequest;
import com.mongodb.WriteConcern;
import com.mongodb.connection.Connection;
import com.mongodb.binding.WriteBinding;
import com.mongodb.assertions.Assertions;

public class DropUserOperation implements AsyncWriteOperation<Void>, WriteOperation<Void>
{
    private final String databaseName;
    private final String userName;
    
    public DropUserOperation(final String databaseName, final String userName) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.userName = Assertions.notNull("userName", userName);
    }
    
    @Override
    public Void execute(final WriteBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnection<Void>)new OperationHelper.CallableWithConnection<Void>() {
            @Override
            public Void call(final Connection connection) {
                if (OperationHelper.serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                    CommandOperationHelper.executeWrappedCommandProtocol(binding, DropUserOperation.this.databaseName, DropUserOperation.this.getCommand(), connection);
                }
                else {
                    connection.delete(DropUserOperation.this.getNamespace(), true, WriteConcern.ACKNOWLEDGED, Arrays.asList(DropUserOperation.this.getDeleteRequest()));
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
                        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, DropUserOperation.this.databaseName, DropUserOperation.this.getCommand(), connection, new OperationHelper.VoidTransformer<BsonDocument>(), wrappedCallback);
                    }
                    else {
                        connection.deleteAsync(DropUserOperation.this.getNamespace(), true, WriteConcern.ACKNOWLEDGED, Arrays.asList(DropUserOperation.this.getDeleteRequest()), new SingleResultCallback<WriteConcernResult>() {
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
    
    private MongoNamespace getNamespace() {
        return new MongoNamespace(this.databaseName, "system.users");
    }
    
    private DeleteRequest getDeleteRequest() {
        return new DeleteRequest(new BsonDocument("user", new BsonString(this.userName)));
    }
    
    private BsonDocument getCommand() {
        return new BsonDocument("dropUser", new BsonString(this.userName));
    }
}
