// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model.geojson.codecs;

import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import com.mongodb.client.model.geojson.Geometry;
import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.model.geojson.Point;
import org.bson.codecs.Codec;

public class PointCodec implements Codec<Point>
{
    private final CodecRegistry registry;
    
    public PointCodec(final CodecRegistry registry) {
        this.registry = Assertions.notNull("registry", registry);
    }
    
    @Override
    public void encode(final BsonWriter writer, final Point value, final EncoderContext encoderContext) {
        GeometryCodecHelper.encodeGeometry(writer, value, encoderContext, this.registry, new Runnable() {
            @Override
            public void run() {
                GeometryCodecHelper.encodePosition(writer, value.getPosition());
            }
        });
    }
    
    @Override
    public Class<Point> getEncoderClass() {
        return Point.class;
    }
    
    @Override
    public Point decode(final BsonReader reader, final DecoderContext decoderContext) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
