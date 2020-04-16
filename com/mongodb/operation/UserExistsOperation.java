// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.connection.QueryResult;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncReadBinding;
import org.bson.codecs.Decoder;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.MongoNamespace;
import org.bson.BsonDocument;
import com.mongodb.Function;
import com.mongodb.connection.Connection;
import com.mongodb.binding.ReadBinding;
import com.mongodb.assertions.Assertions;

public class UserExistsOperation implements AsyncReadOperation<Boolean>, ReadOperation<Boolean>
{
    private final String databaseName;
    private final String userName;
    
    public UserExistsOperation(final String databaseName, final String userName) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.userName = Assertions.notNull("userName", userName);
    }
    
    @Override
    public Boolean execute(final ReadBinding binding) {
        return OperationHelper.withConnection(binding, (OperationHelper.CallableWithConnection<Boolean>)new OperationHelper.CallableWithConnection<Boolean>() {
            @Override
            public Boolean call(final Connection connection) {
                if (OperationHelper.serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                    return CommandOperationHelper.executeWrappedCommandProtocol(binding, UserExistsOperation.this.databaseName, UserExistsOperation.this.getCommand(), connection, UserExistsOperation.this.transformer());
                }
                return UserExistsOperation.this.transformQueryResult().apply(connection.query(new MongoNamespace(UserExistsOperation.this.databaseName, "system.users"), new BsonDocument("user", new BsonString(UserExistsOperation.this.userName)), null, 0, 1, 0, binding.getReadPreference().isSlaveOk(), false, false, false, false, false, (Decoder<Object>)new BsonDocumentCodec()));
            }
        });
    }
    
    @Override
    public void executeAsync(final AsyncReadBinding binding, final SingleResultCallback<Boolean> callback) {
        OperationHelper.withConnection(binding, new OperationHelper.AsyncCallableWithConnection() {
            @Override
            public void call(final AsyncConnection connection, final Throwable t) {
                if (t != null) {
                    ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<Object>)callback).onResult(null, t);
                }
                else {
                    final SingleResultCallback<Boolean> wrappedCallback = OperationHelper.releasingCallback((SingleResultCallback<Boolean>)ErrorHandlingResultCallback.errorHandlingCallback((SingleResultCallback<T>)callback), connection);
                    if (OperationHelper.serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, UserExistsOperation.this.databaseName, UserExistsOperation.this.getCommand(), new BsonDocumentCodec(), connection, UserExistsOperation.this.transformer(), wrappedCallback);
                    }
                    else {
                        connection.queryAsync(new MongoNamespace(UserExistsOperation.this.databaseName, "system.users"), new BsonDocument("user", new BsonString(UserExistsOperation.this.userName)), null, 0, 1, 0, binding.getReadPreference().isSlaveOk(), false, false, false, false, false, (Decoder<Object>)new BsonDocumentCodec(), (SingleResultCallback<QueryResult<Object>>)new SingleResultCallback<QueryResult<BsonDocument>>() {
                            @Override
                            public void onResult(final QueryResult<BsonDocument> result, final Throwable t) {
                                if (t != null) {
                                    wrappedCallback.onResult(null, t);
                                }
                                else {
                                    try {
                                        wrappedCallback.onResult(UserExistsOperation.this.transformQueryResult().apply(result), null);
                                    }
                                    catch (Throwable tr) {
                                        wrappedCallback.onResult(null, tr);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    
    private Function<BsonDocument, Boolean> transformer() {
        return new Function<BsonDocument, Boolean>() {
            @Override
            public Boolean apply(final BsonDocument result) {
                return result.get("users").isArray() && !result.getArray("users").isEmpty();
            }
        };
    }
    
    private Function<QueryResult<BsonDocument>, Boolean> transformQueryResult() {
        return new Function<QueryResult<BsonDocument>, Boolean>() {
            @Override
            public Boolean apply(final QueryResult<BsonDocument> queryResult) {
                return !queryResult.getResults().isEmpty();
            }
        };
    }
    
    private BsonDocument getCommand() {
        return new BsonDocument("usersInfo", new BsonString(this.userName));
    }
}
