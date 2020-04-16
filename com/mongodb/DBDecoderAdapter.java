// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bson.io.BsonOutput;
import org.bson.BsonBinaryWriter;
import com.mongodb.connection.ByteBufferBsonOutput;
import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import com.mongodb.connection.BufferProvider;
import org.bson.codecs.Decoder;

class DBDecoderAdapter implements Decoder<DBObject>
{
    private final DBDecoder decoder;
    private final DBCollection collection;
    private final BufferProvider bufferProvider;
    
    public DBDecoderAdapter(final DBDecoder decoder, final DBCollection collection, final BufferProvider bufferProvider) {
        this.decoder = decoder;
        this.collection = collection;
        this.bufferProvider = bufferProvider;
    }
    
    @Override
    public DBObject decode(final BsonReader reader, final DecoderContext decoderContext) {
        final ByteBufferBsonOutput bsonOutput = new ByteBufferBsonOutput(this.bufferProvider);
        final BsonBinaryWriter binaryWriter = new BsonBinaryWriter(bsonOutput);
        try {
            binaryWriter.pipe(reader);
            final BufferExposingByteArrayOutputStream byteArrayOutputStream = new BufferExposingByteArrayOutputStream(binaryWriter.getBsonOutput().getSize());
            bsonOutput.pipe(byteArrayOutputStream);
            return this.decoder.decode(byteArrayOutputStream.getInternalBytes(), this.collection);
        }
        catch (IOException e) {
            throw new MongoInternalException("An unlikely IOException thrown.", e);
        }
        finally {
            binaryWriter.close();
            bsonOutput.close();
        }
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
