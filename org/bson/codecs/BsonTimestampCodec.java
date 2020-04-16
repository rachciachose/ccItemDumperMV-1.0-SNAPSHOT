// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.BsonTimestamp;

public class BsonTimestampCodec implements Codec<BsonTimestamp>
{
    @Override
    public void encode(final BsonWriter writer, final BsonTimestamp value, final EncoderContext encoderContext) {
        writer.writeTimestamp(value);
    }
    
    @Override
    public BsonTimestamp decode(final BsonReader reader, final DecoderContext decoderContext) {
        return reader.readTimestamp();
    }
    
    @Override
    public Class<BsonTimestamp> getEncoderClass() {
        return BsonTimestamp.class;
    }
}
