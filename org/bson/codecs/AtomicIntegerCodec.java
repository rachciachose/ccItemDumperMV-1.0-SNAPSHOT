// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerCodec implements Codec<AtomicInteger>
{
    @Override
    public void encode(final BsonWriter writer, final AtomicInteger value, final EncoderContext encoderContext) {
        writer.writeInt32(value.intValue());
    }
    
    @Override
    public AtomicInteger decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new AtomicInteger(reader.readInt32());
    }
    
    @Override
    public Class<AtomicInteger> getEncoderClass() {
        return AtomicInteger.class;
    }
}
