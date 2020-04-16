// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonBoolean;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.Function;
import org.bson.BsonDocument;
import com.mongodb.binding.WriteBinding;
import com.mongodb.assertions.Assertions;
import com.mongodb.MongoNamespace;

public class RenameCollectionOperation implements AsyncWriteOperation<Void>, WriteOperation<Void>
{
    private final MongoNamespace originalNamespace;
    private final MongoNamespace newNamespace;
    private boolean dropTarget;
    
    public RenameCollectionOperation(final MongoNamespace originalNamespace, final MongoNamespace newNamespace) {
        this.originalNamespace = Assertions.notNull("originalNamespace", originalNamespace);
        this.newNamespace = Assertions.notNull("newNamespace", newNamespace);
    }
    
    public boolean isDropTarget() {
        return this.dropTarget;
    }
    
    public RenameCollectionOperation dropTarget(final boolean dropTarget) {
        this.dropTarget = dropTarget;
        return this;
    }
    
    @Override
    public Void execute(final WriteBinding binding) {
        return CommandOperationHelper.executeWrappedCommandProtocol(binding, "admin", this.getCommand(), (Function<BsonDocument, Void>)new OperationHelper.VoidTransformer<BsonDocument>());
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<Void> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, "admin", this.getCommand(), new OperationHelper.VoidTransformer<BsonDocument>(), callback);
    }
    
    private BsonDocument getCommand() {
        return new BsonDocument("renameCollection", new BsonString(this.originalNamespace.getFullName())).append("to", new BsonString(this.newNamespace.getFullName())).append("dropTarget", BsonBoolean.valueOf(this.dropTarget));
    }
}
