// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;

public class IntegerCodec implements Codec<Integer>
{
    @Override
    public void encode(final BsonWriter writer, final Integer value, final EncoderContext encoderContext) {
        writer.writeInt32(value);
    }
    
    @Override
    public Integer decode(final BsonReader reader, final DecoderContext decoderContext) {
        return reader.readInt32();
    }
    
    @Override
    public Class<Integer> getEncoderClass() {
        return Integer.class;
    }
}
