// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import java.util.Iterator;
import com.mongodb.assertions.Assertions;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonInt32;
import java.util.List;
import java.util.Arrays;
import org.bson.conversions.Bson;

public final class Projections
{
    public static <TExpression> Bson computed(final String fieldName, final TExpression expression) {
        return new SimpleExpression<Object>(fieldName, expression);
    }
    
    public static Bson include(final String... fieldNames) {
        return include(Arrays.asList(fieldNames));
    }
    
    public static Bson include(final List<String> fieldNames) {
        return combine(fieldNames, new BsonInt32(1));
    }
    
    public static Bson exclude(final String... fieldNames) {
        return exclude(Arrays.asList(fieldNames));
    }
    
    public static Bson exclude(final List<String> fieldNames) {
        return combine(fieldNames, new BsonInt32(0));
    }
    
    public static Bson excludeId() {
        return new BsonDocument("_id", new BsonInt32(0));
    }
    
    public static Bson elemMatch(final String fieldName) {
        return new BsonDocument(fieldName + ".$", new BsonInt32(1));
    }
    
    public static Bson elemMatch(final String fieldName, final Bson filter) {
        return new Bson() {
            @Override
            public <TDocument> BsonDocument toBsonDocument(final Class<TDocument> documentClass, final CodecRegistry codecRegistry) {
                return new BsonDocument(fieldName, new BsonDocument("$elemMatch", filter.toBsonDocument(documentClass, codecRegistry)));
            }
        };
    }
    
    public static Bson metaTextScore(final String fieldName) {
        return new BsonDocument(fieldName, new BsonDocument("$meta", new BsonString("textScore")));
    }
    
    public static Bson slice(final String fieldName, final int limit) {
        return new BsonDocument(fieldName, new BsonDocument("$slice", new BsonInt32(limit)));
    }
    
    public static Bson slice(final String fieldName, final int skip, final int limit) {
        return new BsonDocument(fieldName, new BsonDocument("$slice", new BsonArray(Arrays.asList(new BsonInt32(skip), new BsonInt32(limit)))));
    }
    
    public static Bson fields(final Bson... projections) {
        return fields(Arrays.asList(projections));
    }
    
    public static Bson fields(final List<Bson> projections) {
        Assertions.notNull("sorts", projections);
        return new Bson() {
            @Override
            public <TDocument> BsonDocument toBsonDocument(final Class<TDocument> documentClass, final CodecRegistry codecRegistry) {
                final BsonDocument combinedDocument = new BsonDocument();
                for (final Bson sort : projections) {
                    final BsonDocument sortDocument = sort.toBsonDocument(documentClass, codecRegistry);
                    for (final String key : sortDocument.keySet()) {
                        combinedDocument.remove(key);
                        combinedDocument.append(key, sortDocument.get(key));
                    }
                }
                return combinedDocument;
            }
        };
    }
    
    private static Bson combine(final List<String> fieldNames, final BsonValue value) {
        final BsonDocument document = new BsonDocument();
        for (final String fieldName : fieldNames) {
            document.remove(fieldName);
            document.append(fieldName, value);
        }
        return document;
    }
}
