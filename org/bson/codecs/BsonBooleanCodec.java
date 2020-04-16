// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonBoolean;

public class BsonBooleanCodec implements Codec<BsonBoolean>
{
    @Override
    public BsonBoolean decode(final BsonReader reader, final DecoderContext decoderContext) {
        final boolean value = reader.readBoolean();
        return BsonBoolean.valueOf(value);
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonBoolean value, final EncoderContext encoderContext) {
        writer.writeBoolean(value.getValue());
    }
    
    @Override
    public Class<BsonBoolean> getEncoderClass() {
        return BsonBoolean.class;
    }
}
