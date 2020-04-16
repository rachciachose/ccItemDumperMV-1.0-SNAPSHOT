// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonValue;
import org.bson.BsonInt32;
import org.bson.BsonDocument;

final class CursorHelper
{
    static int getNumberToReturn(final int limit, final int batchSize, final int numReturnedSoFar) {
        int numberToReturn;
        if (Math.abs(limit) != 0) {
            numberToReturn = Math.abs(limit) - numReturnedSoFar;
            if (batchSize != 0 && numberToReturn > Math.abs(batchSize)) {
                numberToReturn = batchSize;
            }
        }
        else {
            numberToReturn = batchSize;
        }
        return numberToReturn;
    }
    
    static BsonDocument getCursorDocumentFromBatchSize(final Integer batchSize) {
        return (batchSize == null) ? new BsonDocument() : new BsonDocument("batchSize", new BsonInt32(batchSize));
    }
}
