// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.codecs.BsonJavaScriptWithScopeCodec;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.BsonUndefinedCodec;
import org.bson.codecs.BsonTimestampCodec;
import org.bson.codecs.BsonSymbolCodec;
import org.bson.codecs.BsonStringCodec;
import org.bson.codecs.BsonRegularExpressionCodec;
import org.bson.codecs.BsonObjectIdCodec;
import org.bson.codecs.BsonJavaScriptCodec;
import org.bson.codecs.BsonMaxKeyCodec;
import org.bson.codecs.BsonMinKeyCodec;
import org.bson.codecs.BsonInt64Codec;
import org.bson.codecs.BsonInt32Codec;
import org.bson.codecs.BsonDoubleCodec;
import org.bson.codecs.BsonDBPointerCodec;
import org.bson.codecs.BsonDateTimeCodec;
import org.bson.codecs.BsonBooleanCodec;
import org.bson.codecs.BsonBinaryCodec;
import org.bson.BsonValue;
import org.bson.codecs.BsonNullCodec;
import org.bson.BsonDocument;
import org.bson.codecs.BsonArrayCodec;
import org.bson.BsonArray;
import org.bson.codecs.configuration.CodecRegistry;
import java.util.HashMap;
import org.bson.codecs.Decoder;
import org.bson.codecs.Codec;
import java.util.Map;
import org.bson.codecs.configuration.CodecProvider;

class CommandResultCodecProvider<P> implements CodecProvider
{
    private final Map<Class<?>, Codec<?>> codecs;
    private final Decoder<P> payloadDecoder;
    private final String fieldContainingPayload;
    
    public CommandResultCodecProvider(final Decoder<P> payloadDecoder, final String fieldContainingPayload) {
        this.codecs = new HashMap<Class<?>, Codec<?>>();
        this.payloadDecoder = payloadDecoder;
        this.fieldContainingPayload = fieldContainingPayload;
        this.addCodecs();
    }
    
    @Override
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        if (this.codecs.containsKey(clazz)) {
            return (Codec<T>)this.codecs.get(clazz);
        }
        if (clazz == BsonArray.class) {
            return (Codec<T>)new BsonArrayCodec(registry);
        }
        if (clazz == BsonDocument.class) {
            return (Codec<T>)new CommandResultDocumentCodec(registry, (Decoder<Object>)this.payloadDecoder, this.fieldContainingPayload);
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
        this.addCodec((Codec<BsonValue>)new BsonJavaScriptWithScopeCodec(new BsonDocumentCodec()));
    }
    
    private <T extends BsonValue> void addCodec(final Codec<T> codec) {
        this.codecs.put(codec.getEncoderClass(), codec);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final CommandResultCodecProvider<?> that = (CommandResultCodecProvider<?>)o;
        return this.fieldContainingPayload.equals(that.fieldContainingPayload) && this.payloadDecoder.getClass().equals(that.payloadDecoder.getClass());
    }
    
    @Override
    public int hashCode() {
        int result = this.payloadDecoder.getClass().hashCode();
        result = 31 * result + this.fieldContainingPayload.hashCode();
        return result;
    }
}
