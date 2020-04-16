// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;

public class ByteCodec implements Codec<Byte>
{
    @Override
    public void encode(final BsonWriter writer, final Byte value, final EncoderContext encoderContext) {
        writer.writeInt32(value);
    }
    
    @Override
    public Byte decode(final BsonReader reader, final DecoderContext decoderContext) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Class<Byte> getEncoderClass() {
        return Byte.class;
    }
}
