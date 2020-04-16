// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonDocumentWrapper;
import java.util.List;
import org.bson.BsonDocument;

final class BsonDocumentWrapperHelper
{
    static <T> List<T> toList(final BsonDocument result, final String fieldContainingWrappedArray) {
        return ((BsonArrayWrapper)result.getArray(fieldContainingWrappedArray)).getWrappedArray();
    }
    
    static <T> T toDocument(final BsonDocument document) {
        if (document == null) {
            return null;
        }
        return ((BsonDocumentWrapper)document).getWrappedDocument();
    }
}
