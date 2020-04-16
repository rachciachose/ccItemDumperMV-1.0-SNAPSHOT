// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model.geojson.codecs;

import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import com.mongodb.client.model.geojson.Geometry;
import java.util.Iterator;
import com.mongodb.client.model.geojson.Position;
import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.model.geojson.LineString;
import org.bson.codecs.Codec;

public class LineStringCodec implements Codec<LineString>
{
    private final CodecRegistry registry;
    
    public LineStringCodec(final CodecRegistry registry) {
        this.registry = Assertions.notNull("registry", registry);
    }
    
    @Override
    public void encode(final BsonWriter writer, final LineString value, final EncoderContext encoderContext) {
        GeometryCodecHelper.encodeGeometry(writer, value, encoderContext, this.registry, new Runnable() {
            @Override
            public void run() {
                writer.writeStartArray();
                for (final Position position : value.getCoordinates()) {
                    GeometryCodecHelper.encodePosition(writer, position);
                }
                writer.writeEndArray();
            }
        });
    }
    
    @Override
    public Class<LineString> getEncoderClass() {
        return LineString.class;
    }
    
    @Override
    public LineString decode(final BsonReader reader, final DecoderContext decoderContext) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
