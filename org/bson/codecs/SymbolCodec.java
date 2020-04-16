// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.types.Symbol;

public class SymbolCodec implements Codec<Symbol>
{
    @Override
    public Symbol decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new Symbol(reader.readSymbol());
    }
    
    @Override
    public void encode(final BsonWriter writer, final Symbol value, final EncoderContext encoderContext) {
        writer.writeSymbol(value.getSymbol());
    }
    
    @Override
    public Class<Symbol> getEncoderClass() {
        return Symbol.class;
    }
}
