// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.BsonBinary;

public class BsonBinaryCodec implements Codec<BsonBinary>
{
    @Override
    public void encode(final BsonWriter writer, final BsonBinary value, final EncoderContext encoderContext) {
        writer.writeBinaryData(value);
    }
    
    @Override
    public BsonBinary decode(final BsonReader reader, final DecoderContext decoderContext) {
        return reader.readBinaryData();
    }
    
    @Override
    public Class<BsonBinary> getEncoderClass() {
        return BsonBinary.class;
    }
}
