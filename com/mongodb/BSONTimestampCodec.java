// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import org.bson.BsonTimestamp;
import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import org.bson.types.BSONTimestamp;
import org.bson.codecs.Codec;

public class BSONTimestampCodec implements Codec<BSONTimestamp>
{
    @Override
    public void encode(final BsonWriter writer, final BSONTimestamp value, final EncoderContext encoderContext) {
        writer.writeTimestamp(new BsonTimestamp(value.getTime(), value.getInc()));
    }
    
    @Override
    public BSONTimestamp decode(final BsonReader reader, final DecoderContext decoderContext) {
        final BsonTimestamp timestamp = reader.readTimestamp();
        return new BSONTimestamp(timestamp.getTime(), timestamp.getInc());
    }
    
    @Override
    public Class<BSONTimestamp> getEncoderClass() {
        return BSONTimestamp.class;
    }
}
