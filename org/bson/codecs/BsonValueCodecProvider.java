// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import java.util.Collections;
import org.bson.BsonUndefined;
import org.bson.BsonTimestamp;
import org.bson.BsonSymbol;
import org.bson.BsonString;
import org.bson.BsonRegularExpression;
import org.bson.BsonObjectId;
import org.bson.BsonJavaScript;
import org.bson.BsonMinKey;
import org.bson.BsonMaxKey;
import org.bson.BsonInt64;
import org.bson.BsonInt32;
import org.bson.BsonDouble;
import org.bson.BsonDbPointer;
import org.bson.BsonDateTime;
import org.bson.BsonBoolean;
import org.bson.BsonBinary;
import org.bson.BsonNull;
import org.bson.RawBsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonDocument;
import org.bson.BsonJavaScriptWithScope;
import org.bson.BsonArray;
import org.bson.codecs.configuration.CodecRegistry;
import java.util.HashMap;
import org.bson.BsonValue;
import org.bson.BsonType;
import java.util.Map;
import org.bson.codecs.configuration.CodecProvider;

public class BsonValueCodecProvider implements CodecProvider
{
    private static final Map<BsonType, Class<? extends BsonValue>> DEFAULT_BSON_TYPE_CLASS_MAP;
    private final Map<Class<?>, Codec<?>> codecs;
    
    public BsonValueCodecProvider() {
        this.codecs = new HashMap<Class<?>, Codec<?>>();
        this.addCodecs();
    }
    
    public static Class<? extends BsonValue> getClassForBsonType(final BsonType bsonType) {
        return BsonValueCodecProvider.DEFAULT_BSON_TYPE_CLASS_MAP.get(bsonType);
    }
    
    @Override
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        if (this.codecs.containsKey(clazz)) {
            return (Codec<T>)this.codecs.get(clazz);
        }
        if (clazz == BsonArray.class) {
            return (Codec<T>)new BsonArrayCodec(registry);
        }
        if (clazz == BsonJavaScriptWithScope.class) {
            return (Codec<T>)new BsonJavaScriptWithScopeCodec(registry.get(BsonDocument.class));
        }
        if (clazz == BsonValue.class) {
            return (Codec<T>)new BsonValueCodec(registry);
        }
        if (clazz == BsonDocumentWrapper.class) {
            return (Codec<T>)new BsonDocumentWrapperCodec(registry.get(BsonDocument.class));
        }
        if (clazz == RawBsonDocument.class) {
            return (Codec<T>)new RawBsonDocumentCodec();
        }
        if (BsonDocument.class.isAssignableFrom(clazz)) {
            return (Codec<T>)new BsonDocumentCodec(registry);
        }
        return null;
    }
    
    private void addCodecs() {
        this.addCodec((Codec<BsonValue>)new BsonNullCodec());
        this.addCodec((Codec<BsonValue>)new BsonBinaryCodec());
        this.addCodec((Codec<BsonValue>)new BsonBooleanCodec());
        this.addCodec((Codec<BsonValue>)new BsonDateTimeCodec());
        this.addCodec((Codec<BsonValue>)new BsonDBPointerCodec());
        this.addCodec((Codec<BsonValue>)new BsonDoubleCodec());
        this.addCodec((Codec<BsonValue>)new BsonInt32Codec());
        this.addCodec((Codec<BsonValue>)new BsonInt64Codec());
        this.addCodec((Codec<BsonValue>)new BsonMinKeyCodec());
        this.addCodec((Codec<BsonValue>)new BsonMaxKeyCodec());
        this.addCodec((Codec<BsonValue>)new BsonJavaScriptCodec());
        this.addCodec((Codec<BsonValue>)new BsonObjectIdCodec());
        this.addCodec((Codec<BsonValue>)new BsonRegularExpressionCodec());
        this.addCodec((Codec<BsonValue>)new BsonStringCodec());
        this.addCodec((Codec<BsonValue>)new BsonSymbolCodec());
        this.addCodec((Codec<BsonValue>)new BsonTimestampCodec());
        this.addCodec((Codec<BsonValue>)new BsonUndefinedCodec());
    }
    
    private <T extends BsonValue> void addCodec(final Codec<T> codec) {
        this.codecs.put(codec.getEncoderClass(), codec);
    }
    
    static {
        final Map<BsonType, Class<? extends BsonValue>> map = new HashMap<BsonType, Class<? extends BsonValue>>();
        map.put(BsonType.NULL, BsonNull.class);
        map.put(BsonType.ARRAY, BsonArray.class);
        map.put(BsonType.BINARY, BsonBinary.class);
        map.put(BsonType.BOOLEAN, BsonBoolean.class);
        map.put(BsonType.DATE_TIME, BsonDateTime.class);
        map.put(BsonType.DB_POINTER, BsonDbPointer.class);
        map.put(BsonType.DOCUMENT, BsonDocument.class);
        map.put(BsonType.DOUBLE, BsonDouble.class);
        map.put(BsonType.INT32, BsonInt32.class);
        map.put(BsonType.INT64, BsonInt64.class);
        map.put(BsonType.MAX_KEY, BsonMaxKey.class);
        map.put(BsonType.MIN_KEY, BsonMinKey.class);
        map.put(BsonType.JAVASCRIPT, BsonJavaScript.class);
        map.put(BsonType.JAVASCRIPT_WITH_SCOPE, BsonJavaScriptWithScope.class);
        map.put(BsonType.OBJECT_ID, BsonObjectId.class);
        map.put(BsonType.REGULAR_EXPRESSION, BsonRegularExpression.class);
        map.put(BsonType.STRING, BsonString.class);
        map.put(BsonType.SYMBOL, BsonSymbol.class);
        map.put(BsonType.TIMESTAMP, BsonTimestamp.class);
        map.put(BsonType.UNDEFINED, BsonUndefined.class);
        DEFAULT_BSON_TYPE_CLASS_MAP = Collections.unmodifiableMap((Map<? extends BsonType, ? extends Class<? extends BsonValue>>)map);
    }
}
