// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.BsonValue;

public class BsonValueCodec implements Codec<BsonValue>
{
    private final CodecRegistry codecRegistry;
    
    public BsonValueCodec() {
        this(CodecRegistries.fromProviders(new BsonValueCodecProvider()));
    }
    
    public BsonValueCodec(final CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }
    
    @Override
    public BsonValue decode(final BsonReader reader, final DecoderContext decoderContext) {
        return this.codecRegistry.get(BsonValueCodecProvider.getClassForBsonType(reader.getCurrentBsonType())).decode(reader, decoderContext);
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonValue value, final EncoderContext encoderContext) {
        final Codec codec = this.codecRegistry.get(value.getClass());
        encoderContext.encodeWithChildContext(codec, writer, value);
    }
    
    @Override
    public Class<BsonValue> getEncoderClass() {
        return BsonValue.class;
    }
}
