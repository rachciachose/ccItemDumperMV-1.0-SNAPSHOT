// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bson.BSONException;
import java.io.OutputStream;
import org.bson.io.BsonOutput;
import org.bson.BsonBinaryWriter;
import org.bson.io.BasicOutputBuffer;
import org.bson.BsonReader;
import org.bson.io.BsonInput;
import org.bson.BsonBinaryReader;
import org.bson.io.ByteBufferBsonInput;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;

public class RawBsonDocumentCodec implements Codec<RawBsonDocument>
{
    @Override
    public void encode(final BsonWriter writer, final RawBsonDocument value, final EncoderContext encoderContext) {
        final BsonBinaryReader reader = new BsonBinaryReader(new ByteBufferBsonInput(value.getByteBuffer()));
        try {
            writer.pipe(reader);
        }
        finally {
            reader.close();
        }
    }
    
    @Override
    public RawBsonDocument decode(final BsonReader reader, final DecoderContext decoderContext) {
        final BasicOutputBuffer buffer = new BasicOutputBuffer();
        final BsonBinaryWriter writer = new BsonBinaryWriter(buffer);
        try {
            writer.pipe(reader);
            final BufferExposingByteArrayOutputStream byteArrayOutputStream = new BufferExposingByteArrayOutputStream(writer.getBsonOutput().getSize());
            buffer.pipe(byteArrayOutputStream);
            return new RawBsonDocument(byteArrayOutputStream.getInternalBytes());
        }
        catch (IOException e) {
            throw new BSONException("impossible", e);
        }
        finally {
            writer.close();
        }
    }
    
    @Override
    public Class<RawBsonDocument> getEncoderClass() {
        return RawBsonDocument.class;
    }
    
    private static class BufferExposingByteArrayOutputStream extends ByteArrayOutputStream
    {
        BufferExposingByteArrayOutputStream(final int size) {
            super(size);
        }
        
        byte[] getInternalBytes() {
            return this.buf;
        }
    }
}
