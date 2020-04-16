// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Logger;
import org.bson.BsonDocument;
import com.mongodb.WriteConcernResult;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;

class GenericWriteProtocol extends WriteProtocol
{
    private final RequestMessage requestMessage;
    
    public GenericWriteProtocol(final MongoNamespace namespace, final RequestMessage requestMessage, final boolean ordered, final WriteConcern writeConcern) {
        super(namespace, ordered, writeConcern);
        this.requestMessage = requestMessage;
    }
    
    @Override
    protected void appendToWriteCommandResponseDocument(final RequestMessage curMessage, final RequestMessage nextMessage, final WriteConcernResult writeConcernResult, final BsonDocument response) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
    
    @Override
    protected BsonDocument getAsWriteCommand(final ByteBufferBsonOutput bsonOutput, final int firstDocumentPosition) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
    
    @Override
    protected String getCommandName() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
    
    @Override
    protected RequestMessage createRequestMessage(final MessageSettings settings) {
        return this.requestMessage;
    }
    
    @Override
    protected Logger getLogger() {
        throw new UnsupportedOperationException();
    }
}
