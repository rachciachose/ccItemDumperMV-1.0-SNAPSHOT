// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;

public class LongCodec implements Codec<Long>
{
    @Override
    public void encode(final BsonWriter writer, final Long value, final EncoderContext encoderContext) {
        writer.writeInt64(value);
    }
    
    @Override
    public Long decode(final BsonReader reader, final DecoderContext decoderContext) {
        return reader.readInt64();
    }
    
    @Override
    public Class<Long> getEncoderClass() {
        return Long.class;
    }
}
