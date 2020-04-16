// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonSymbol;

public class BsonSymbolCodec implements Codec<BsonSymbol>
{
    @Override
    public BsonSymbol decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new BsonSymbol(reader.readSymbol());
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonSymbol value, final EncoderContext encoderContext) {
        writer.writeSymbol(value.getSymbol());
    }
    
    @Override
    public Class<BsonSymbol> getEncoderClass() {
        return BsonSymbol.class;
    }
}
