// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonInt64;

public class BsonInt64Codec implements Codec<BsonInt64>
{
    @Override
    public BsonInt64 decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new BsonInt64(reader.readInt64());
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonInt64 value, final EncoderContext encoderContext) {
        writer.writeInt64(value.getValue());
    }
    
    @Override
    public Class<BsonInt64> getEncoderClass() {
        return BsonInt64.class;
    }
}
