// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import java.util.Date;

public class DateCodec implements Codec<Date>
{
    @Override
    public void encode(final BsonWriter writer, final Date value, final EncoderContext encoderContext) {
        writer.writeDateTime(value.getTime());
    }
    
    @Override
    public Date decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new Date(reader.readDateTime());
    }
    
    @Override
    public Class<Date> getEncoderClass() {
        return Date.class;
    }
}
