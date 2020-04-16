// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonJavaScript;

public class BsonJavaScriptCodec implements Codec<BsonJavaScript>
{
    @Override
    public BsonJavaScript decode(final BsonReader reader, final DecoderContext decoderContext) {
        return new BsonJavaScript(reader.readJavaScript());
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonJavaScript value, final EncoderContext encoderContext) {
        writer.writeJavaScript(value.getCode());
    }
    
    @Override
    public Class<BsonJavaScript> getEncoderClass() {
        return BsonJavaScript.class;
    }
}
