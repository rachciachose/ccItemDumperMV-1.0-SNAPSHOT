// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonValue;
import org.bson.BsonInt32;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.Function;
import com.mongodb.binding.WriteBinding;
import com.mongodb.assertions.Assertions;
import org.bson.BsonDocument;

public class DropDatabaseOperation implements AsyncWriteOperation<Void>, WriteOperation<Void>
{
    private static final BsonDocument DROP_DATABASE;
    private final String databaseName;
    
    public DropDatabaseOperation(final String databaseName) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
    }
    
    @Override
    public Void execute(final WriteBinding binding) {
        return CommandOperationHelper.executeWrappedCommandProtocol(binding, this.databaseName, DropDatabaseOperation.DROP_DATABASE, (Function<BsonDocument, Void>)new OperationHelper.VoidTransformer<BsonDocument>());
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<Void> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, this.databaseName, DropDatabaseOperation.DROP_DATABASE, new OperationHelper.VoidTransformer<BsonDocument>(), callback);
    }
    
    static {
        DROP_DATABASE = new BsonDocument("dropDatabase", new BsonInt32(1));
    }
}
