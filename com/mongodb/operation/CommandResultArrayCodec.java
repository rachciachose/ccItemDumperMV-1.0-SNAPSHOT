// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.codecs.Encoder;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonValue;
import java.util.List;
import org.bson.BsonType;
import java.util.ArrayList;
import org.bson.BsonArray;
import org.bson.codecs.DecoderContext;
import org.bson.BsonReader;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.Decoder;
import org.bson.codecs.BsonArrayCodec;

class CommandResultArrayCodec<T> extends BsonArrayCodec
{
    private final Decoder<T> decoder;
    
    CommandResultArrayCodec(final CodecRegistry registry, final Decoder<T> decoder) {
        super(registry);
        this.decoder = decoder;
    }
    
    @Override
    public BsonArray decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartArray();
        final List<T> list = new ArrayList<T>();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(this.decoder.decode(reader, decoderContext));
        }
        reader.readEndArray();
        return new BsonArrayWrapper<Object>(list);
    }
    
    @Override
    protected BsonValue readValue(final BsonReader reader, final DecoderContext decoderContext) {
        if (reader.getCurrentBsonType() == BsonType.DOCUMENT) {
            return new BsonDocumentWrapper<Object>(this.decoder.decode(reader, decoderContext), null);
        }
        return super.readValue(reader, decoderContext);
    }
}
