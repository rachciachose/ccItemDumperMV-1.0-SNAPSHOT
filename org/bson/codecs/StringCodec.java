// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonType;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class StringCodec implements Codec<String>
{
    @Override
    public void encode(final BsonWriter writer, final String value, final EncoderContext encoderContext) {
        writer.writeString(value);
    }
    
    @Override
    public String decode(final BsonReader reader, final DecoderContext decoderContext) {
        if (reader.getCurrentBsonType() == BsonType.SYMBOL) {
            return reader.readSymbol();
        }
        return reader.readString();
    }
    
    @Override
    public Class<String> getEncoderClass() {
        return String.class;
    }
}
