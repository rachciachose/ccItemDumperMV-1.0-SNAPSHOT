// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model.geojson;

import org.bson.codecs.configuration.CodecRegistries;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.Codec;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import java.io.Writer;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import java.io.StringWriter;
import org.bson.codecs.configuration.CodecRegistry;

public abstract class Geometry
{
    private static final CodecRegistry REGISTRY;
    private final CoordinateReferenceSystem coordinateReferenceSystem;
    
    protected Geometry() {
        this(null);
    }
    
    protected Geometry(final CoordinateReferenceSystem coordinateReferenceSystem) {
        this.coordinateReferenceSystem = coordinateReferenceSystem;
    }
    
    public abstract GeoJsonObjectType getType();
    
    public String toJson() {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter writer = new JsonWriter(stringWriter, new JsonWriterSettings());
        final Codec codec = getRegistry().get(this.getClass());
        codec.encode(writer, this, EncoderContext.builder().build());
        return stringWriter.toString();
    }
    
    static CodecRegistry getRegistry() {
        return Geometry.REGISTRY;
    }
    
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return this.coordinateReferenceSystem;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Geometry geometry = (Geometry)o;
        if (this.coordinateReferenceSystem != null) {
            if (this.coordinateReferenceSystem.equals(geometry.coordinateReferenceSystem)) {
                return true;
            }
        }
        else if (geometry.coordinateReferenceSystem == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (this.coordinateReferenceSystem != null) ? this.coordinateReferenceSystem.hashCode() : 0;
    }
    
    static {
        REGISTRY = CodecRegistries.fromProviders(new GeoJsonCodecProvider());
    }
}
