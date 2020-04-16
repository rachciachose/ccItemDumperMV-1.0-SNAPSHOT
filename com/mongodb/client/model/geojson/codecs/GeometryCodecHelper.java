// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model.geojson.codecs;

import java.util.Iterator;
import com.mongodb.client.model.geojson.Position;
import java.util.List;
import com.mongodb.client.model.geojson.PolygonCoordinates;
import org.bson.codecs.Codec;
import com.mongodb.client.model.geojson.CoordinateReferenceSystem;
import org.bson.codecs.Encoder;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.EncoderContext;
import com.mongodb.client.model.geojson.Geometry;
import org.bson.BsonWriter;

final class GeometryCodecHelper
{
    static void encodeGeometry(final BsonWriter writer, final Geometry geometry, final EncoderContext encoderContext, final CodecRegistry registry, final Runnable coordinatesEncoder) {
        writer.writeStartDocument();
        encodeType(writer, geometry);
        writer.writeName("coordinates");
        coordinatesEncoder.run();
        encodeCoordinateReferenceSystem(writer, geometry, encoderContext, registry);
        writer.writeEndDocument();
    }
    
    static void encodeType(final BsonWriter writer, final Geometry geometry) {
        writer.writeString("type", geometry.getType().getTypeName());
    }
    
    static void encodeCoordinateReferenceSystem(final BsonWriter writer, final Geometry geometry, final EncoderContext encoderContext, final CodecRegistry registry) {
        if (geometry.getCoordinateReferenceSystem() != null) {
            writer.writeName("crs");
            final Codec codec = registry.get(geometry.getCoordinateReferenceSystem().getClass());
            encoderContext.encodeWithChildContext(codec, writer, geometry.getCoordinateReferenceSystem());
        }
    }
    
    static void encodePolygonCoordinates(final BsonWriter writer, final PolygonCoordinates polygonCoordinates) {
        writer.writeStartArray();
        encodeLinearRing(polygonCoordinates.getExterior(), writer);
        for (final List<Position> ring : polygonCoordinates.getHoles()) {
            encodeLinearRing(ring, writer);
        }
        writer.writeEndArray();
    }
    
    private static void encodeLinearRing(final List<Position> ring, final BsonWriter writer) {
        writer.writeStartArray();
        for (final Position position : ring) {
            encodePosition(writer, position);
        }
        writer.writeEndArray();
    }
    
    static void encodePosition(final BsonWriter writer, final Position value) {
        writer.writeStartArray();
        for (final double number : value.getValues()) {
            writer.writeDouble(number);
        }
        writer.writeEndArray();
    }
}
