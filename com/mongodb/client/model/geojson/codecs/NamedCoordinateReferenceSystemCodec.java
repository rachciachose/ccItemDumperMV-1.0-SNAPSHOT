// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model.geojson.codecs;

import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import com.mongodb.client.model.geojson.NamedCoordinateReferenceSystem;
import org.bson.codecs.Codec;

public class NamedCoordinateReferenceSystemCodec implements Codec<NamedCoordinateReferenceSystem>
{
    @Override
    public void encode(final BsonWriter writer, final NamedCoordinateReferenceSystem value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("type", value.getType().getTypeName());
        writer.writeStartDocument("properties");
        writer.writeString("name", value.getName());
        writer.writeEndDocument();
        writer.writeEndDocument();
    }
    
    @Override
    public Class<NamedCoordinateReferenceSystem> getEncoderClass() {
        return NamedCoordinateReferenceSystem.class;
    }
    
    @Override
    public NamedCoordinateReferenceSystem decode(final BsonReader reader, final DecoderContext decoderContext) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
