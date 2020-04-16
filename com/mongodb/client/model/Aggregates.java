// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import org.bson.BsonString;
import java.util.Iterator;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.configuration.CodecRegistry;
import java.util.List;
import java.util.Arrays;
import org.bson.BsonValue;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.conversions.Bson;

public final class Aggregates
{
    public static Bson match(final Bson filter) {
        return new SimplePipelineStage("$match", filter);
    }
    
    public static Bson project(final Bson projection) {
        return new SimplePipelineStage("$project", projection);
    }
    
    public static Bson sort(final Bson sort) {
        return new SimplePipelineStage("$sort", sort);
    }
    
    public static Bson skip(final int skip) {
        return new BsonDocument("$skip", new BsonInt32(skip));
    }
    
    public static Bson limit(final int limit) {
        return new BsonDocument("$limit", new BsonInt32(limit));
    }
    
    public static <TExpression> Bson group(final TExpression id, final BsonField... fieldAccumulators) {
        return group(id, Arrays.asList(fieldAccumulators));
    }
    
    public static <TExpression> Bson group(final TExpression id, final List<BsonField> fieldAccumulators) {
        return new Bson() {
            @Override
            public <TDocument> BsonDocument toBsonDocument(final Class<TDocument> tDocumentClass, final CodecRegistry codecRegistry) {
                final BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());
                writer.writeStartDocument();
                writer.writeStartDocument("$group");
                writer.writeName("_id");
                BuildersHelper.encodeValue(writer, id, codecRegistry);
                for (final BsonField fieldAccumulator : fieldAccumulators) {
                    writer.writeName(fieldAccumulator.getName());
                    BuildersHelper.encodeValue(writer, fieldAccumulator.getValue(), codecRegistry);
                }
                writer.writeEndDocument();
                writer.writeEndDocument();
                return writer.getDocument();
            }
        };
    }
    
    public static Bson unwind(final String fieldName) {
        return new BsonDocument("$unwind", new BsonString(fieldName));
    }
    
    public static Bson out(final String collectionName) {
        return new BsonDocument("$out", new BsonString(collectionName));
    }
    
    private static class SimplePipelineStage implements Bson
    {
        private final String name;
        private final Bson value;
        
        public SimplePipelineStage(final String name, final Bson value) {
            this.name = name;
            this.value = value;
        }
        
        @Override
        public <TDocument> BsonDocument toBsonDocument(final Class<TDocument> documentClass, final CodecRegistry codecRegistry) {
            return new BsonDocument(this.name, this.value.toBsonDocument(documentClass, codecRegistry));
        }
    }
}
