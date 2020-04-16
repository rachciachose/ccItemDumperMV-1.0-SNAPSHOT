// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

import java.io.Serializable;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecProvider;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.io.BsonInput;
import org.bson.io.ByteBufferBsonInput;
import org.bson.codecs.BsonValueCodecProvider;
import java.io.Writer;
import org.bson.json.JsonWriter;
import org.bson.codecs.RawBsonDocumentCodec;
import java.io.StringWriter;
import org.bson.json.JsonWriterSettings;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import org.bson.codecs.DecoderContext;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import org.bson.codecs.EncoderContext;
import org.bson.io.BsonOutput;
import org.bson.io.BasicOutputBuffer;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

public final class RawBsonDocument extends BsonDocument
{
    private static final long serialVersionUID = 1L;
    private static final CodecRegistry REGISTRY;
    private final byte[] bytes;
    
    public RawBsonDocument(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes can not be null");
        }
        this.bytes = bytes;
    }
    
    public RawBsonDocument(final T document, final Codec<T> codec) {
        final BasicOutputBuffer buffer = new BasicOutputBuffer();
        final BsonBinaryWriter writer = new BsonBinaryWriter(buffer);
        try {
            codec.encode(writer, document, EncoderContext.builder().build());
            this.bytes = buffer.toByteArray();
        }
        finally {
            writer.close();
        }
    }
    
    public ByteBuf getByteBuffer() {
        final ByteBuffer buffer = ByteBuffer.wrap(this.bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return new ByteBufNIO(buffer);
    }
    
    public <T> T decode(final Codec<T> codec) {
        final BsonBinaryReader reader = this.createReader();
        try {
            return codec.decode(reader, DecoderContext.builder().build());
        }
        finally {
            reader.close();
        }
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }
    
    @Override
    public BsonValue put(final String key, final BsonValue value) {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }
    
    @Override
    public BsonDocument append(final String key, final BsonValue value) {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends BsonValue> m) {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }
    
    @Override
    public BsonValue remove(final Object key) {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }
    
    @Override
    public boolean isEmpty() {
        final BsonBinaryReader bsonReader = this.createReader();
        try {
            bsonReader.readStartDocument();
            if (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                return false;
            }
            bsonReader.readEndDocument();
        }
        finally {
            bsonReader.close();
        }
        return true;
    }
    
    @Override
    public int size() {
        int size = 0;
        final BsonBinaryReader bsonReader = this.createReader();
        try {
            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                ++size;
                bsonReader.readName();
                bsonReader.skipValue();
            }
            bsonReader.readEndDocument();
        }
        finally {
            bsonReader.close();
        }
        return size;
    }
    
    @Override
    public Set<Map.Entry<String, BsonValue>> entrySet() {
        return this.toBsonDocument().entrySet();
    }
    
    @Override
    public Collection<BsonValue> values() {
        return this.toBsonDocument().values();
    }
    
    @Override
    public Set<String> keySet() {
        return this.toBsonDocument().keySet();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        final BsonBinaryReader bsonReader = this.createReader();
        try {
            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (bsonReader.readName().equals(key)) {
                    return true;
                }
                bsonReader.skipValue();
            }
            bsonReader.readEndDocument();
        }
        finally {
            bsonReader.close();
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        final BsonBinaryReader bsonReader = this.createReader();
        try {
            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                bsonReader.skipName();
                if (this.deserializeBsonValue(bsonReader).equals(value)) {
                    return true;
                }
            }
            bsonReader.readEndDocument();
        }
        finally {
            bsonReader.close();
        }
        return false;
    }
    
    @Override
    public BsonValue get(final Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        final BsonBinaryReader bsonReader = this.createReader();
        try {
            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (bsonReader.readName().equals(key)) {
                    return this.deserializeBsonValue(bsonReader);
                }
                bsonReader.skipValue();
            }
            bsonReader.readEndDocument();
        }
        finally {
            bsonReader.close();
        }
        return null;
    }
    
    @Override
    public String toJson() {
        return this.toJson(new JsonWriterSettings());
    }
    
    @Override
    public String toJson(final JsonWriterSettings settings) {
        final StringWriter writer = new StringWriter();
        new RawBsonDocumentCodec().encode((BsonWriter)new JsonWriter(writer, settings), this, EncoderContext.builder().build());
        return writer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.toBsonDocument().equals(o);
    }
    
    @Override
    public int hashCode() {
        return this.toBsonDocument().hashCode();
    }
    
    @Override
    public BsonDocument clone() {
        return new RawBsonDocument(this.bytes.clone());
    }
    
    private BsonValue deserializeBsonValue(final BsonBinaryReader bsonReader) {
        return RawBsonDocument.REGISTRY.get(BsonValueCodecProvider.getClassForBsonType(bsonReader.getCurrentBsonType())).decode(bsonReader, DecoderContext.builder().build());
    }
    
    private BsonBinaryReader createReader() {
        return new BsonBinaryReader(new ByteBufferBsonInput(this.getByteBuffer()));
    }
    
    private BsonDocument toBsonDocument() {
        final BsonBinaryReader bsonReader = this.createReader();
        try {
            return new BsonDocumentCodec().decode((BsonReader)bsonReader, DecoderContext.builder().build());
        }
        finally {
            bsonReader.close();
        }
    }
    
    private Object writeReplace() {
        return new SerializationProxy(this.bytes);
    }
    
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }
    
    static {
        REGISTRY = CodecRegistries.fromProviders(new BsonValueCodecProvider());
    }
    
    private static class SerializationProxy implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private final byte[] bytes;
        
        public SerializationProxy(final byte[] bytes) {
            this.bytes = bytes;
        }
        
        private Object readResolve() {
            return new RawBsonDocument(this.bytes);
        }
    }
}
