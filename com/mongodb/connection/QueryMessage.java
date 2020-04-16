// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.FieldNameValidator;
import com.mongodb.internal.validator.NoOpFieldNameValidator;
import org.bson.io.BsonOutput;
import org.bson.BsonDocument;

class QueryMessage extends BaseQueryMessage
{
    private final BsonDocument queryDocument;
    private final BsonDocument fields;
    
    public QueryMessage(final String collectionName, final int skip, final int numberToReturn, final BsonDocument queryDocument, final BsonDocument fields, final MessageSettings settings) {
        super(collectionName, skip, numberToReturn, settings);
        this.queryDocument = queryDocument;
        this.fields = fields;
    }
    
    @Override
    protected RequestMessage encodeMessageBody(final BsonOutput bsonOutput, final int messageStartPosition) {
        return this.encodeMessageBodyWithMetadata(bsonOutput, messageStartPosition).getNextMessage();
    }
    
    @Override
    protected EncodingMetadata encodeMessageBodyWithMetadata(final BsonOutput bsonOutput, final int messageStartPosition) {
        this.writeQueryPrologue(bsonOutput);
        final int firstDocumentStartPosition = bsonOutput.getPosition();
        this.addDocument(this.queryDocument, bsonOutput, new NoOpFieldNameValidator());
        if (this.fields != null) {
            this.addDocument(this.fields, bsonOutput, new NoOpFieldNameValidator());
        }
        return new EncodingMetadata(null, firstDocumentStartPosition);
    }
}
