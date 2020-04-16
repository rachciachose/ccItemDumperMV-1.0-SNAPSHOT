// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonValue;
import org.bson.BsonString;
import org.bson.BsonDocument;
import com.mongodb.Function;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.MongoCommandException;
import com.mongodb.binding.WriteBinding;
import com.mongodb.assertions.Assertions;
import com.mongodb.MongoNamespace;

public class DropCollectionOperation implements AsyncWriteOperation<Void>, WriteOperation<Void>
{
    private final MongoNamespace namespace;
    
    public DropCollectionOperation(final MongoNamespace namespace) {
        this.namespace = Assertions.notNull("namespace", namespace);
    }
    
    @Override
    public Void execute(final WriteBinding binding) {
        try {
            CommandOperationHelper.executeWrappedCommandProtocol(binding, this.namespace.getDatabaseName(), this.getCommand());
        }
        catch (MongoCommandException e) {
            CommandOperationHelper.rethrowIfNotNamespaceError(e);
        }
        return null;
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<Void> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, this.namespace.getDatabaseName(), this.getCommand(), (Function<BsonDocument, Object>)new OperationHelper.VoidTransformer(), (SingleResultCallback<Object>)new SingleResultCallback<Void>() {
            @Override
            public void onResult(final Void result, final Throwable t) {
                if (t != null && !CommandOperationHelper.isNamespaceError(t)) {
                    callback.onResult(null, t);
                }
                else {
                    callback.onResult(result, null);
                }
            }
        });
    }
    
    private BsonDocument getCommand() {
        return new BsonDocument("drop", new BsonString(this.namespace.getCollectionName()));
    }
}
