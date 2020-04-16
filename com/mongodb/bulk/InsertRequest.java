// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.bulk;

import com.mongodb.assertions.Assertions;
import org.bson.BsonDocument;

public final class InsertRequest extends WriteRequest
{
    private final BsonDocument document;
    
    public InsertRequest(final BsonDocument document) {
        this.document = Assertions.notNull("document", document);
    }
    
    public BsonDocument getDocument() {
        return this.document;
    }
    
    @Override
    public Type getType() {
        return Type.INSERT;
    }
}
