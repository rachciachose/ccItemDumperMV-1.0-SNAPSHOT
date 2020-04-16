// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model.geojson.codecs;

import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import com.mongodb.client.model.geojson.Geometry;
import java.util.Iterator;
import com.mongodb.client.model.geojson.Position;
import java.util.List;
import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.model.geojson.MultiLineString;
import org.bson.codecs.Codec;

public class MultiLineStringCodec implements Codec<MultiLineString>
{
    private final CodecRegistry registry;
    
    public MultiLineStringCodec(final CodecRegistry registry) {
        this.registry = Assertions.notNull("registry", registry);
    }
    
    @Override
    public void encode(final BsonWriter writer, final MultiLineString value, final EncoderContext encoderContext) {
        GeometryCodecHelper.encodeGeometry(writer, value, encoderContext, this.registry, new Runnable() {
            @Override
            public void run() {
                writer.writeStartArray();
                for (final List<Position> ring : value.getCoordinates()) {
                    writer.writeStartArray();
                    for (final Position position : ring) {
                        GeometryCodecHelper.encodePosition(writer, position);
                    }
                    writer.writeEndArray();
                }
                writer.writeEndArray();
            }
        });
    }
    
    @Override
    public Class<MultiLineString> getEncoderClass() {
        return MultiLineString.class;
    }
    
    @Override
    public MultiLineString decode(final BsonReader reader, final DecoderContext decoderContext) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
