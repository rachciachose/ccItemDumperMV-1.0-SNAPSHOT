// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import java.util.Iterator;
import java.util.Map;
import org.bson.BsonWriter;
import org.bson.BsonValue;
import java.util.List;
import org.bson.BsonType;
import org.bson.BsonElement;
import java.util.ArrayList;
import org.bson.BsonReader;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.BsonDocument;

public class BsonDocumentCodec implements CollectibleCodec<BsonDocument>
{
    private static final String ID_FIELD_NAME = "_id";
    private static final CodecRegistry DEFAULT_REGISTRY;
    private final CodecRegistry codecRegistry;
    
    public BsonDocumentCodec() {
        this.codecRegistry = BsonDocumentCodec.DEFAULT_REGISTRY;
    }
    
    public BsonDocumentCodec(final CodecRegistry codecRegistry) {
        if (codecRegistry == null) {
            throw new IllegalArgumentException("Codec registry can not be null");
        }
        this.codecRegistry = codecRegistry;
    }
    
    public CodecRegistry getCodecRegistry() {
        return this.codecRegistry;
    }
    
    @Override
    public BsonDocument decode(final BsonReader reader, final DecoderContext decoderContext) {
        final List<BsonElement> keyValuePairs = new ArrayList<BsonElement>();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            final String fieldName = reader.readName();
            keyValuePairs.add(new BsonElement(fieldName, this.readValue(reader, decoderContext)));
        }
        reader.readEndDocument();
        return new BsonDocument(keyValuePairs);
    }
    
    protected BsonValue readValue(final BsonReader reader, final DecoderContext decoderContext) {
        return this.codecRegistry.get(BsonValueCodecProvider.getClassForBsonType(reader.getCurrentBsonType())).decode(reader, decoderContext);
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonDocument value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        this.beforeFields(writer, encoderContext, value);
        for (final Map.Entry<String, BsonValue> entry : value.entrySet()) {
            if (this.skipField(encoderContext, entry.getKey())) {
                continue;
            }
            writer.writeName(entry.getKey());
            this.writeValue(writer, encoderContext, entry.getValue());
        }
        writer.writeEndDocument();
    }
    
    private void beforeFields(final BsonWriter bsonWriter, final EncoderContext encoderContext, final BsonDocument value) {
        if (encoderContext.isEncodingCollectibleDocument() && value.containsKey("_id")) {
            bsonWriter.writeName("_id");
            this.writeValue(bsonWriter, encoderContext, value.get("_id"));
        }
    }
    
    private boolean skipField(final EncoderContext encoderContext, final String key) {
        return encoderContext.isEncodingCollectibleDocument() && key.equals("_id");
    }
    
    private void writeValue(final BsonWriter writer, final EncoderContext encoderContext, final BsonValue value) {
        final Codec codec = this.codecRegistry.get(value.getClass());
        encoderContext.encodeWithChildContext(codec, writer, value);
    }
    
    @Override
    public Class<BsonDocument> getEncoderClass() {
        return BsonDocument.class;
    }
    
    @Override
    public BsonDocument generateIdIfAbsentFromDocument(final BsonDocument document) {
        if (!this.documentHasId(document)) {
            document.put("_id", new BsonObjectId(new ObjectId()));
        }
        return document;
    }
    
    @Override
    public boolean documentHasId(final BsonDocument document) {
        return document.containsKey("_id");
    }
    
    @Override
    public BsonValue getDocumentId(final BsonDocument document) {
        return document.get("_id");
    }
    
    static {
        DEFAULT_REGISTRY = CodecRegistries.fromProviders(new BsonValueCodecProvider());
    }
}
