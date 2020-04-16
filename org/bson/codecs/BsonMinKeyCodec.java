// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.BsonMinKey;

public class BsonMinKeyCodec implements Codec<BsonMinKey>
{
    @Override
    public void encode(final BsonWriter writer, final BsonMinKey value, final EncoderContext encoderContext) {
        writer.writeMinKey();
    }
    
    @Override
    public BsonMinKey decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readMinKey();
        return new BsonMinKey();
    }
    
    @Override
    public Class<BsonMinKey> getEncoderClass() {
        return BsonMinKey.class;
    }
}
