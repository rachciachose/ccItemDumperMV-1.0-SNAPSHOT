// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.BsonDocumentWriter;

final class BuildersHelper
{
    static <TItem> void encodeValue(final BsonDocumentWriter writer, final TItem value, final CodecRegistry codecRegistry) {
        if (value == null) {
            writer.writeNull();
        }
        else if (value instanceof Bson) {
            codecRegistry.get(BsonDocument.class).encode(writer, ((Bson)value).toBsonDocument(BsonDocument.class, codecRegistry), EncoderContext.builder().build());
        }
        else {
            codecRegistry.get(value.getClass()).encode(writer, value, EncoderContext.builder().build());
        }
    }
}
