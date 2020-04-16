// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.codecs.Encoder;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.BsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.Decoder;
import org.bson.codecs.BsonDocumentCodec;

class CommandResultDocumentCodec<T> extends BsonDocumentCodec
{
    private final Decoder<T> payloadDecoder;
    private final String fieldContainingPayload;
    
    CommandResultDocumentCodec(final CodecRegistry registry, final Decoder<T> payloadDecoder, final String fieldContainingPayload) {
        super(registry);
        this.payloadDecoder = payloadDecoder;
        this.fieldContainingPayload = fieldContainingPayload;
    }
    
    static <P> Codec<BsonDocument> create(final Decoder<P> decoder, final String fieldContainingPayload) {
        final CodecRegistry registry = CodecRegistries.fromProviders(new CommandResultCodecProvider<Object>(decoder, fieldContainingPayload));
        return registry.get(BsonDocument.class);
    }
    
    @Override
    protected BsonValue readValue(final BsonReader reader, final DecoderContext decoderContext) {
        if (reader.getCurrentName().equals(this.fieldContainingPayload)) {
            if (reader.getCurrentBsonType() == BsonType.DOCUMENT) {
                return new BsonDocumentWrapper<Object>(this.payloadDecoder.decode(reader, decoderContext), null);
            }
            if (reader.getCurrentBsonType() == BsonType.ARRAY) {
                return new CommandResultArrayCodec(this.getCodecRegistry(), (Decoder<Object>)this.payloadDecoder).decode(reader, decoderContext);
            }
        }
        return super.readValue(reader, decoderContext);
    }
}
