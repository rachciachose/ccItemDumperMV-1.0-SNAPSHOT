// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import org.bson.BsonDocumentWriter;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

class SimpleExpression<TExpression> implements Bson
{
    private final String name;
    private final TExpression expression;
    
    public SimpleExpression(final String name, final TExpression expression) {
        this.name = name;
        this.expression = expression;
    }
    
    @Override
    public <TDocument> BsonDocument toBsonDocument(final Class<TDocument> documentClass, final CodecRegistry codecRegistry) {
        final BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());
        writer.writeStartDocument();
        writer.writeName(this.name);
        BuildersHelper.encodeValue(writer, this.expression, codecRegistry);
        writer.writeEndDocument();
        return writer.getDocument();
    }
}
