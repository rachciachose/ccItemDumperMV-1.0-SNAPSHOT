// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.Codec;

public class DBRefCodec implements Codec<DBRef>
{
    private final CodecRegistry registry;
    
    public DBRefCodec(final CodecRegistry registry) {
        this.registry = Assertions.notNull("registry", registry);
    }
    
    @Override
    public void encode(final BsonWriter writer, final DBRef value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("$ref", value.getCollectionName());
        writer.writeName("$id");
        final Codec codec = this.registry.get(value.getId().getClass());
        codec.encode(writer, value.getId(), encoderContext);
        writer.writeEndDocument();
    }
    
    @Override
    public Class<DBRef> getEncoderClass() {
        return DBRef.class;
    }
    
    @Override
    public DBRef decode(final BsonReader reader, final DecoderContext decoderContext) {
        throw new UnsupportedOperationException("DBRefCodec does not support decoding");
    }
}
