// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.codecs.configuration.CodecRegistries;
import java.util.Arrays;
import org.bson.codecs.configuration.CodecProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.BsonBinarySubType;
import java.util.Iterator;
import org.bson.BsonType;
import org.bson.BsonReader;
import java.util.Map;
import org.bson.BsonWriter;
import org.bson.BsonDocumentWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.assertions.Assertions;
import org.bson.Transformer;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.Document;

public class DocumentCodec implements CollectibleCodec<Document>
{
    private static final String ID_FIELD_NAME = "_id";
    private static final CodecRegistry DEFAULT_REGISTRY;
    private static final BsonTypeClassMap DEFAULT_BSON_TYPE_CLASS_MAP;
    private final BsonTypeClassMap bsonTypeClassMap;
    private final CodecRegistry registry;
    private final IdGenerator idGenerator;
    private final Transformer valueTransformer;
    
    public DocumentCodec() {
        this(DocumentCodec.DEFAULT_REGISTRY, DocumentCodec.DEFAULT_BSON_TYPE_CLASS_MAP);
    }
    
    public DocumentCodec(final CodecRegistry registry, final BsonTypeClassMap bsonTypeClassMap) {
        this(registry, bsonTypeClassMap, null);
    }
    
    public DocumentCodec(final CodecRegistry registry, final BsonTypeClassMap bsonTypeClassMap, final Transformer valueTransformer) {
        this.registry = Assertions.notNull("registry", registry);
        this.bsonTypeClassMap = Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap);
        this.idGenerator = Assertions.notNull("idGenerator", new ObjectIdGenerator());
        this.valueTransformer = ((valueTransformer != null) ? valueTransformer : new Transformer() {
            @Override
            public Object transform(final Object value) {
                return value;
            }
        });
    }
    
    @Override
    public boolean documentHasId(final Document document) {
        return document.containsKey("_id");
    }
    
    @Override
    public BsonValue getDocumentId(final Document document) {
        if (!this.documentHasId(document)) {
            throw new IllegalStateException("The document does not contain an _id");
        }
        final Object id = document.get("_id");
        if (id instanceof BsonValue) {
            return (BsonValue)id;
        }
        final BsonDocument idHoldingDocument = new BsonDocument();
        final BsonWriter writer = new BsonDocumentWriter(idHoldingDocument);
        writer.writeStartDocument();
        writer.writeName("_id");
        this.writeValue(writer, EncoderContext.builder().build(), id);
        writer.writeEndDocument();
        return idHoldingDocument.get("_id");
    }
    
    @Override
    public Document generateIdIfAbsentFromDocument(final Document document) {
        if (!this.documentHasId(document)) {
            document.put("_id", this.idGenerator.generate());
        }
        return document;
    }
    
    @Override
    public void encode(final BsonWriter writer, final Document document, final EncoderContext encoderContext) {
        this.writeMap(writer, document, encoderContext);
    }
    
    @Override
    public Document decode(final BsonReader reader, final DecoderContext decoderContext) {
        final Document document = new Document();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            final String fieldName = reader.readName();
            document.put(fieldName, this.readValue(reader, decoderContext));
        }
        reader.readEndDocument();
        return document;
    }
    
    @Override
    public Class<Document> getEncoderClass() {
        return Document.class;
    }
    
    private void beforeFields(final BsonWriter bsonWriter, final EncoderContext encoderContext, final Map<String, Object> document) {
        if (encoderContext.isEncodingCollectibleDocument() && document.containsKey("_id")) {
            bsonWriter.writeName("_id");
            this.writeValue(bsonWriter, encoderContext, document.get("_id"));
        }
    }
    
    private boolean skipField(final EncoderContext encoderContext, final String key) {
        return encoderContext.isEncodingCollectibleDocument() && key.equals("_id");
    }
    
    private void writeValue(final BsonWriter writer, final EncoderContext encoderContext, final Object value) {
        if (value == null) {
            writer.writeNull();
        }
        else if (Iterable.class.isAssignableFrom(value.getClass())) {
            this.writeIterable(writer, (Iterable<Object>)value, encoderContext.getChildContext());
        }
        else if (Map.class.isAssignableFrom(value.getClass())) {
            this.writeMap(writer, (Map<String, Object>)value, encoderContext.getChildContext());
        }
        else {
            final Codec codec = this.registry.get(value.getClass());
            encoderContext.encodeWithChildContext(codec, writer, value);
        }
    }
    
    private void writeMap(final BsonWriter writer, final Map<String, Object> map, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        this.beforeFields(writer, encoderContext, map);
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            if (this.skipField(encoderContext, entry.getKey())) {
                continue;
            }
            writer.writeName(entry.getKey());
            this.writeValue(writer, encoderContext, entry.getValue());
        }
        writer.writeEndDocument();
    }
    
    private void writeIterable(final BsonWriter writer, final Iterable<Object> list, final EncoderContext encoderContext) {
        writer.writeStartArray();
        for (final Object value : list) {
            this.writeValue(writer, encoderContext, value);
        }
        writer.writeEndArray();
    }
    
    private Object readValue(final BsonReader reader, final DecoderContext decoderContext) {
        final BsonType bsonType = reader.getCurrentBsonType();
        if (bsonType == BsonType.NULL) {
            reader.readNull();
            return null;
        }
        if (bsonType == BsonType.ARRAY) {
            return this.readList(reader, decoderContext);
        }
        if (bsonType == BsonType.BINARY) {
            final byte bsonSubType = reader.peekBinarySubType();
            if (bsonSubType == BsonBinarySubType.UUID_STANDARD.getValue() || bsonSubType == BsonBinarySubType.UUID_LEGACY.getValue()) {
                return this.registry.get(UUID.class).decode(reader, decoderContext);
            }
        }
        return this.valueTransformer.transform(this.registry.get(this.bsonTypeClassMap.get(bsonType)).decode(reader, decoderContext));
    }
    
    private List<Object> readList(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartArray();
        final List<Object> list = new ArrayList<Object>();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(this.readValue(reader, decoderContext));
        }
        reader.readEndArray();
        return list;
    }
    
    static {
        DEFAULT_REGISTRY = CodecRegistries.fromProviders(Arrays.asList(new ValueCodecProvider(), new BsonValueCodecProvider(), new DocumentCodecProvider()));
        DEFAULT_BSON_TYPE_CLASS_MAP = new BsonTypeClassMap();
    }
}
