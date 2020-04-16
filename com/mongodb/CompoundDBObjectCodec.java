// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import org.bson.codecs.Decoder;
import org.bson.codecs.Encoder;
import org.bson.codecs.Codec;

class CompoundDBObjectCodec implements Codec<DBObject>
{
    private final Encoder<DBObject> encoder;
    private final Decoder<DBObject> decoder;
    
    public CompoundDBObjectCodec(final Encoder<DBObject> encoder, final Decoder<DBObject> decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }
    
    public CompoundDBObjectCodec(final Codec<DBObject> codec) {
        this(codec, codec);
    }
    
    @Override
    public DBObject decode(final BsonReader reader, final DecoderContext decoderContext) {
        return this.decoder.decode(reader, decoderContext);
    }
    
    @Override
    public void encode(final BsonWriter writer, final DBObject value, final EncoderContext encoderContext) {
        this.encoder.encode(writer, value, encoderContext);
    }
    
    @Override
    public Class<DBObject> getEncoderClass() {
        return DBObject.class;
    }
    
    public Encoder<DBObject> getEncoder() {
        return this.encoder;
    }
    
    public Decoder<DBObject> getDecoder() {
        return this.decoder;
    }
}
