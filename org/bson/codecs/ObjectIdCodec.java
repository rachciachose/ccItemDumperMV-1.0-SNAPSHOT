// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.ObjectId;

public class ObjectIdCodec implements Codec<ObjectId>
{
    @Override
    public void encode(final BsonWriter writer, final ObjectId value, final EncoderContext encoderContext) {
        writer.writeObjectId(value);
    }
    
    @Override
    public ObjectId decode(final BsonReader reader, final DecoderContext decoderContext) {
        return reader.readObjectId();
    }
    
    @Override
    public Class<ObjectId> getEncoderClass() {
        return ObjectId.class;
    }
}
