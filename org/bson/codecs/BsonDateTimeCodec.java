// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonDateTime;

public class BsonDateTimeCodec implements Codec<BsonDateTime>
{
    @Override
    public BsonDateTime decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new BsonDateTime(reader.readDateTime());
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonDateTime value, final EncoderContext encoderContext) {
        writer.writeDateTime(value.getValue());
    }
    
    @Override
    public Class<BsonDateTime> getEncoderClass() {
        return BsonDateTime.class;
    }
}
