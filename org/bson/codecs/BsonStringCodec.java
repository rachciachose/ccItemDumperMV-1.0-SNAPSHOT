// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonString;

public class BsonStringCodec implements Codec<BsonString>
{
    @Override
    public BsonString decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new BsonString(reader.readString());
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonString value, final EncoderContext encoderContext) {
        writer.writeString(value.getValue());
    }
    
    @Override
    public Class<BsonString> getEncoderClass() {
        return BsonString.class;
    }
}
