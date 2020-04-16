// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model.geojson.codecs;

import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import com.mongodb.client.model.geojson.Geometry;
import java.util.Iterator;
import com.mongodb.client.model.geojson.PolygonCoordinates;
import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.model.geojson.MultiPolygon;
import org.bson.codecs.Codec;

public class MultiPolygonCodec implements Codec<MultiPolygon>
{
    private final CodecRegistry registry;
    
    public MultiPolygonCodec(final CodecRegistry registry) {
        this.registry = Assertions.notNull("registry", registry);
    }
    
    @Override
    public void encode(final BsonWriter writer, final MultiPolygon value, final EncoderContext encoderContext) {
        GeometryCodecHelper.encodeGeometry(writer, value, encoderContext, this.registry, new Runnable() {
            @Override
            public void run() {
                writer.writeStartArray();
                for (final PolygonCoordinates polygonCoordinates : value.getCoordinates()) {
                    GeometryCodecHelper.encodePolygonCoordinates(writer, polygonCoordinates);
                }
                writer.writeEndArray();
            }
        });
    }
    
    @Override
    public Class<MultiPolygon> getEncoderClass() {
        return MultiPolygon.class;
    }
    
    @Override
    public MultiPolygon decode(final BsonReader reader, final DecoderContext decoderContext) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
