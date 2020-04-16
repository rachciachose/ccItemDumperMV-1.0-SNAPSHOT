// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BsonReader;
import org.bson.codecs.DecoderContext;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocument;

final class DBObjects
{
    public static DBObject toDBObject(final BsonDocument document) {
        return MongoClient.getDefaultCodecRegistry().get(DBObject.class).decode(new BsonDocumentReader(document), DecoderContext.builder().build());
    }
}
