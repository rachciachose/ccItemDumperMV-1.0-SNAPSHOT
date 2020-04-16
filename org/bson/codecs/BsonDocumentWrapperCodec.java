// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.BsonReader;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;

public class BsonDocumentWrapperCodec implements Codec<BsonDocumentWrapper>
{
    private final Codec<BsonDocument> bsonDocumentCodec;
    
    public BsonDocumentWrapperCodec(final Codec<BsonDocument> bsonDocumentCodec) {
        this.bsonDocumentCodec = bsonDocumentCodec;
    }
    
    @Override
    public BsonDocumentWrapper decode(final BsonReader reader, final DecoderContext decoderContext) {
        throw new UnsupportedOperationException("Decoding into a BsonDocumentWrapper is not allowed");
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonDocumentWrapper value, final EncoderContext encoderContext) {
        if (value.isUnwrapped()) {
            this.bsonDocumentCodec.encode(writer, value, encoderContext);
        }
        else {
            final Encoder encoder = value.getEncoder();
            encoder.encode(writer, value.getWrappedDocument(), encoderContext);
        }
    }
    
    @Override
    public Class<BsonDocumentWrapper> getEncoderClass() {
        return BsonDocumentWrapper.class;
    }
}
