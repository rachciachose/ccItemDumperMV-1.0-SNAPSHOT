// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.MaxKey;

public class MaxKeyCodec implements Codec<MaxKey>
{
    @Override
    public void encode(final BsonWriter writer, final MaxKey value, final EncoderContext encoderContext) {
        writer.writeMaxKey();
    }
    
    @Override
    public MaxKey decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readMaxKey();
        return new MaxKey();
    }
    
    @Override
    public Class<MaxKey> getEncoderClass() {
        return MaxKey.class;
    }
}
