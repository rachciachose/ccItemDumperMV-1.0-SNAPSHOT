// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BsonReader;
import org.bson.io.BsonInput;
import org.bson.BsonBinaryReader;
import org.bson.ByteBuf;
import org.bson.io.ByteBufferBsonInput;
import org.bson.ByteBufNIO;
import java.nio.ByteBuffer;
import org.bson.BSONObject;
import org.bson.io.OutputBuffer;
import org.bson.io.BasicOutputBuffer;
import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.Encoder;

class DBEncoderAdapter implements Encoder<DBObject>
{
    private final DBEncoder encoder;
    
    public DBEncoderAdapter(final DBEncoder encoder) {
        this.encoder = Assertions.notNull("encoder", encoder);
    }
    
    @Override
    public void encode(final BsonWriter writer, final DBObject document, final EncoderContext encoderContext) {
        final BasicOutputBuffer buffer = new BasicOutputBuffer();
        try {
            this.encoder.writeObject(buffer, document);
            final BsonBinaryReader reader = new BsonBinaryReader(new ByteBufferBsonInput(new ByteBufNIO(ByteBuffer.wrap(buffer.toByteArray()))));
            try {
                writer.pipe(reader);
            }
            finally {
                reader.close();
            }
        }
        finally {
            buffer.close();
        }
    }
    
    @Override
    public Class<DBObject> getEncoderClass() {
        return DBObject.class;
    }
}
