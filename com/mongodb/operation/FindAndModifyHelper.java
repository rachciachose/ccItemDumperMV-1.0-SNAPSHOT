// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonDocument;
import com.mongodb.Function;

final class FindAndModifyHelper
{
    static <T> Function<BsonDocument, T> transformer() {
        return new Function<BsonDocument, T>() {
            @Override
            public T apply(final BsonDocument result) {
                if (!result.isDocument("value")) {
                    return null;
                }
                return BsonDocumentWrapperHelper.toDocument(result.getDocument("value", null));
            }
        };
    }
}
