// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonDbPointer;

public class BsonDBPointerCodec implements Codec<BsonDbPointer>
{
    @Override
    public BsonDbPointer decode(final BsonReader reader, final DecoderContext decoderContext) {
        return reader.readDBPointer();
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonDbPointer value, final EncoderContext encoderContext) {
        writer.writeDBPointer(value);
    }
    
    @Override
    public Class<BsonDbPointer> getEncoderClass() {
        return BsonDbPointer.class;
    }
}
