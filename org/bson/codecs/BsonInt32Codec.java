// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonInt32;

public class BsonInt32Codec implements Codec<BsonInt32>
{
    @Override
    public BsonInt32 decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new BsonInt32(reader.readInt32());
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonInt32 value, final EncoderContext encoderContext) {
        writer.writeInt32(value.getValue());
    }
    
    @Override
    public Class<BsonInt32> getEncoderClass() {
        return BsonInt32.class;
    }
}
