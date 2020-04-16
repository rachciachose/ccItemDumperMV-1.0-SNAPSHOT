// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanCodec implements Codec<AtomicBoolean>
{
    @Override
    public void encode(final BsonWriter writer, final AtomicBoolean value, final EncoderContext encoderContext) {
        writer.writeBoolean(value.get());
    }
    
    @Override
    public AtomicBoolean decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new AtomicBoolean(reader.readBoolean());
    }
    
    @Override
    public Class<AtomicBoolean> getEncoderClass() {
        return AtomicBoolean.class;
    }
}
