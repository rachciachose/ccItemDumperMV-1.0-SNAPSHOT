// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonBinary;
import org.bson.BsonWriter;
import org.bson.types.Binary;

public class BinaryCodec implements Codec<Binary>
{
    @Override
    public void encode(final BsonWriter writer, final Binary value, final EncoderContext encoderContext) {
        writer.writeBinaryData(new BsonBinary(value.getType(), value.getData()));
    }
    
    @Override
    public Binary decode(final BsonReader reader, final DecoderContext decoderContext) {
        final BsonBinary bsonBinary = reader.readBinaryData();
        return new Binary(bsonBinary.getType(), bsonBinary.getData());
    }
    
    @Override
    public Class<Binary> getEncoderClass() {
        return Binary.class;
    }
}
