// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.FieldNameValidator;
import com.mongodb.internal.validator.NoOpFieldNameValidator;
import org.bson.io.BsonOutput;
import com.mongodb.bulk.DeleteRequest;
import java.util.List;

class DeleteMessage extends RequestMessage
{
    private final List<DeleteRequest> deleteRequests;
    
    public DeleteMessage(final String collectionName, final List<DeleteRequest> deletes, final MessageSettings settings) {
        super(collectionName, OpCode.OP_DELETE, settings);
        this.deleteRequests = deletes;
    }
    
    @Override
    protected RequestMessage encodeMessageBody(final BsonOutput bsonOutput, final int messageStartPosition) {
        return this.encodeMessageBodyWithMetadata(bsonOutput, messageStartPosition).getNextMessage();
    }
    
    @Override
    protected EncodingMetadata encodeMessageBodyWithMetadata(final BsonOutput bsonOutput, final int messageStartPosition) {
        final DeleteRequest deleteRequest = this.deleteRequests.get(0);
        bsonOutput.writeInt32(0);
        bsonOutput.writeCString(this.getCollectionName());
        if (deleteRequest.isMulti()) {
            bsonOutput.writeInt32(0);
        }
        else {
            bsonOutput.writeInt32(1);
        }
        final int firstDocumentStartPosition = bsonOutput.getPosition();
        this.addDocument(deleteRequest.getFilter(), bsonOutput, new NoOpFieldNameValidator());
        if (this.deleteRequests.size() == 1) {
            return new EncodingMetadata(null, firstDocumentStartPosition);
        }
        return new EncodingMetadata(new DeleteMessage(this.getCollectionName(), this.deleteRequests.subList(1, this.deleteRequests.size()), this.getSettings()), firstDocumentStartPosition);
    }
}
