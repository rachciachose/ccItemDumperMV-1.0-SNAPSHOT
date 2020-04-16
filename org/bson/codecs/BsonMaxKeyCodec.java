// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.BsonMaxKey;

public class BsonMaxKeyCodec implements Codec<BsonMaxKey>
{
    @Override
    public void encode(final BsonWriter writer, final BsonMaxKey value, final EncoderContext encoderContext) {
        writer.writeMaxKey();
    }
    
    @Override
    public BsonMaxKey decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readMaxKey();
        return new BsonMaxKey();
    }
    
    @Override
    public Class<BsonMaxKey> getEncoderClass() {
        return BsonMaxKey.class;
    }
}
