// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecProvider;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.RawBsonDocument;
import org.bson.BsonReader;
import org.bson.io.BsonInput;
import org.bson.io.ByteBufferBsonInput;
import java.io.Writer;
import org.bson.json.JsonWriter;
import java.io.StringWriter;
import org.bson.json.JsonWriterSettings;
import java.util.Collection;
import java.util.Set;
import org.bson.BsonBinaryReader;
import org.bson.BsonType;
import java.util.Map;
import org.bson.BsonValue;
import java.util.ArrayList;
import java.nio.ByteOrder;
import java.util.List;
import org.bson.ByteBuf;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.BsonDocument;

class ByteBufBsonDocument extends BsonDocument implements Cloneable
{
    private static final long serialVersionUID = 1L;
    private static final CodecRegistry REGISTRY;
    private final transient ByteBuf byteBuf;
    
    static List<ByteBufBsonDocument> create(final ResponseBuffers responseBuffers) {
        final int numDocuments = responseBuffers.getReplyHeader().getNumberReturned();
        final ByteBuf documentsBuffer = responseBuffers.getBodyByteBuffer();
        documentsBuffer.order(ByteOrder.LITTLE_ENDIAN);
        final List<ByteBufBsonDocument> documents = new ArrayList<ByteBufBsonDocument>(numDocuments);
        while (documents.size() < numDocuments) {
            final int documentSizeInBytes = documentsBuffer.getInt();
            documentsBuffer.position(documentsBuffer.position() - 4);
            final ByteBuf documentBuffer = documentsBuffer.duplicate();
            documentBuffer.limit(documentBuffer.position() + documentSizeInBytes);
            documents.add(new ByteBufBsonDocument(documentBuffer));
            documentsBuffer.position(documentsBuffer.position() + documentSizeInBytes);
        }
        return documents;
    }
    
    static ByteBufBsonDocument createOne(final ByteBufferBsonOutput bsonOutput, final int startPosition) {
        return create(bsonOutput, startPosition).get(0);
    }
    
    static List<ByteBufBsonDocument> create(final ByteBufferBsonOutput bsonOutput, final int startPosition) {
        final CompositeByteBuf outputByteBuf = new CompositeByteBuf(bsonOutput.getByteBuffers());
        outputByteBuf.position(startPosition);
        final List<ByteBufBsonDocument> documents = new ArrayList<ByteBufBsonDocument>();
        int curDocumentStartPosition = startPosition;
        while (outputByteBuf.hasRemaining()) {
            final int documentSizeInBytes = outputByteBuf.getInt();
            final ByteBuf slice = outputByteBuf.duplicate();
            slice.position(curDocumentStartPosition);
            slice.limit(curDocumentStartPosition + documentSizeInBytes);
            documents.add(new ByteBufBsonDocument(slice));
            curDocumentStartPosition += documentSizeInBytes;
            outputByteBuf.position(outputByteBuf.position() + documentSizeInBytes - 4);
        }
        return documents;
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
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(stringWriter, settings);
        final BsonBinaryReader reader = new BsonBinaryReader(new ByteBufferBsonInput(this.byteBuf));
        jsonWriter.pipe(reader);
        return stringWriter.toString();
    }
    
    public String getFirstKey() {
        final BsonBinaryReader bsonReader = this.createReader();
        try {
            bsonReader.readStartDocument();
            if (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                return bsonReader.readName();
            }
            bsonReader.readEndDocument();
        }
        finally {
            bsonReader.close();
        }
        return null;
    }
    
    @Override
    public BsonDocument clone() {
        final byte[] clonedBytes = new byte[this.byteBuf.remaining()];
        this.byteBuf.get(this.byteBuf.position(), clonedBytes);
        return new RawBsonDocument(clonedBytes);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || this.toBsonDocument().equals(o);
    }
    
    @Override
    public int hashCode() {
        return this.toBsonDocument().hashCode();
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
    
    private BsonBinaryReader createReader() {
        return new BsonBinaryReader(new ByteBufferBsonInput(this.byteBuf.duplicate()));
    }
    
    private BsonValue deserializeBsonValue(final BsonBinaryReader bsonReader) {
        return ByteBufBsonDocument.REGISTRY.get(BsonValueCodecProvider.getClassForBsonType(bsonReader.getCurrentBsonType())).decode(bsonReader, DecoderContext.builder().build());
    }
    
    ByteBufBsonDocument(final ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }
    
    private Object writeReplace() {
        return this.toBsonDocument();
    }
    
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }
    
    static {
        REGISTRY = CodecRegistries.fromProviders(new BsonValueCodecProvider());
    }
}
