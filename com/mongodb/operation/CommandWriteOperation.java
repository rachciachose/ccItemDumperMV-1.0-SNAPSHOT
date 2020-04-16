// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.binding.WriteBinding;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.Decoder;
import org.bson.BsonDocument;

public class CommandWriteOperation<T> implements AsyncWriteOperation<T>, WriteOperation<T>
{
    private final String databaseName;
    private final BsonDocument command;
    private final Decoder<T> decoder;
    
    public CommandWriteOperation(final String databaseName, final BsonDocument command, final Decoder<T> decoder) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.command = Assertions.notNull("command", command);
        this.decoder = Assertions.notNull("decoder", decoder);
    }
    
    @Override
    public T execute(final WriteBinding binding) {
        return CommandOperationHelper.executeWrappedCommandProtocol(binding, this.databaseName, this.command, this.decoder);
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<T> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, this.databaseName, this.command, this.decoder, callback);
    }
}
