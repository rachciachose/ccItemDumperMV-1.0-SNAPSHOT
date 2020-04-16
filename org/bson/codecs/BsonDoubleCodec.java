// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonDouble;

public class BsonDoubleCodec implements Codec<BsonDouble>
{
    @Override
    public BsonDouble decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new BsonDouble(reader.readDouble());
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonDouble value, final EncoderContext encoderContext) {
        writer.writeDouble(value.getValue());
    }
    
    @Override
    public Class<BsonDouble> getEncoderClass() {
        return BsonDouble.class;
    }
}
