// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonUndefined;

public class BsonUndefinedCodec implements Codec<BsonUndefined>
{
    @Override
    public BsonUndefined decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readUndefined();
        return new BsonUndefined();
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonUndefined value, final EncoderContext encoderContext) {
        writer.writeUndefined();
    }
    
    @Override
    public Class<BsonUndefined> getEncoderClass() {
        return BsonUndefined.class;
    }
}
