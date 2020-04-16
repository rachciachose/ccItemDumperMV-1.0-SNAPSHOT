// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.bson.BsonValue;
import java.util.Iterator;
import org.bson.BsonString;
import org.bson.BsonNumber;
import org.bson.BsonDocument;

final class IndexHelper
{
    static String generateIndexName(final BsonDocument index) {
        final StringBuilder indexName = new StringBuilder();
        for (final String keyNames : index.keySet()) {
            if (indexName.length() != 0) {
                indexName.append('_');
            }
            indexName.append(keyNames).append('_');
            final BsonValue ascOrDescValue = index.get(keyNames);
            if (ascOrDescValue instanceof BsonNumber) {
                indexName.append(((BsonNumber)ascOrDescValue).intValue());
            }
            else {
                if (!(ascOrDescValue instanceof BsonString)) {
                    continue;
                }
                indexName.append(((BsonString)ascOrDescValue).getValue().replace(' ', '_'));
            }
        }
        return indexName.toString();
    }
}
