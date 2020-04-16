// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import java.util.Iterator;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonInt32;
import com.mongodb.assertions.Assertions;
import java.util.List;
import java.util.Arrays;
import org.bson.conversions.Bson;

public final class Sorts
{
    public static Bson ascending(final String... fieldNames) {
        return ascending(Arrays.asList(fieldNames));
    }
    
    public static Bson ascending(final List<String> fieldNames) {
        Assertions.notNull("fieldNames", fieldNames);
        return orderBy(fieldNames, new BsonInt32(1));
    }
    
    public static Bson descending(final String... fieldNames) {
        return descending(Arrays.asList(fieldNames));
    }
    
    public static Bson descending(final List<String> fieldNames) {
        Assertions.notNull("fieldNames", fieldNames);
        return orderBy(fieldNames, new BsonInt32(-1));
    }
    
    public static Bson metaTextScore(final String fieldName) {
        return new BsonDocument(fieldName, new BsonDocument("$meta", new BsonString("textScore")));
    }
    
    public static Bson orderBy(final Bson... sorts) {
        return orderBy(Arrays.asList(sorts));
    }
    
    public static Bson orderBy(final List<Bson> sorts) {
        Assertions.notNull("sorts", sorts);
        return new Bson() {
            @Override
            public <TDocument> BsonDocument toBsonDocument(final Class<TDocument> documentClass, final CodecRegistry codecRegistry) {
                final BsonDocument combinedDocument = new BsonDocument();
                for (final Bson sort : sorts) {
                    final BsonDocument sortDocument = sort.toBsonDocument(documentClass, codecRegistry);
                    for (final String key : sortDocument.keySet()) {
                        combinedDocument.append(key, sortDocument.get(key));
                    }
                }
                return combinedDocument;
            }
        };
    }
    
    private static Bson orderBy(final List<String> fieldNames, final BsonValue value) {
        final BsonDocument document = new BsonDocument();
        for (final String fieldName : fieldNames) {
            document.append(fieldName, value);
        }
        return document;
    }
}
