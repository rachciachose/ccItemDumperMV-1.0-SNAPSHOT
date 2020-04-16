// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model.geojson.codecs;

import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import org.bson.codecs.Encoder;
import java.util.Iterator;
import com.mongodb.client.model.geojson.Geometry;
import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.client.model.geojson.GeometryCollection;
import org.bson.codecs.Codec;

public class GeometryCollectionCodec implements Codec<GeometryCollection>
{
    private final CodecRegistry registry;
    
    public GeometryCollectionCodec(final CodecRegistry registry) {
        this.registry = Assertions.notNull("registry", registry);
    }
    
    @Override
    public void encode(final BsonWriter writer, final GeometryCollection value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        GeometryCodecHelper.encodeType(writer, value);
        writer.writeName("geometries");
        writer.writeStartArray();
        for (final Geometry geometry : value.getGeometries()) {
            this.encodeGeometry(writer, geometry, encoderContext);
        }
        writer.writeEndArray();
        GeometryCodecHelper.encodeCoordinateReferenceSystem(writer, value, encoderContext, this.registry);
        writer.writeEndDocument();
    }
    
    private void encodeGeometry(final BsonWriter writer, final Geometry geometry, final EncoderContext encoderContext) {
        final Codec codec = this.registry.get(geometry.getClass());
        encoderContext.encodeWithChildContext(codec, writer, geometry);
    }
    
    @Override
    public Class<GeometryCollection> getEncoderClass() {
        return GeometryCollection.class;
    }
    
    @Override
    public GeometryCollection decode(final BsonReader reader, final DecoderContext decoderContext) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
