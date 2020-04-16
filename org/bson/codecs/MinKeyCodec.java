// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.MinKey;

public class MinKeyCodec implements Codec<MinKey>
{
    @Override
    public void encode(final BsonWriter writer, final MinKey value, final EncoderContext encoderContext) {
        writer.writeMinKey();
    }
    
    @Override
    public MinKey decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readMinKey();
        return new MinKey();
    }
    
    @Override
    public Class<MinKey> getEncoderClass() {
        return MinKey.class;
    }
}
