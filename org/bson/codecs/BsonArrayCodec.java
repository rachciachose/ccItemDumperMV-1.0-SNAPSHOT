// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import java.util.Iterator;
import org.bson.BsonWriter;
import java.util.List;
import org.bson.BsonType;
import org.bson.BsonValue;
import java.util.ArrayList;
import org.bson.BsonReader;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.BsonArray;

public class BsonArrayCodec implements Codec<BsonArray>
{
    private final CodecRegistry codecRegistry;
    
    public BsonArrayCodec(final CodecRegistry codecRegistry) {
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
    }
    
    @Override
    public BsonArray decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartArray();
        final List<BsonValue> list = new ArrayList<BsonValue>();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(this.readValue(reader, decoderContext));
        }
        reader.readEndArray();
        return new BsonArray(list);
    }
    
    @Override
    public void encode(final BsonWriter writer, final BsonArray array, final EncoderContext encoderContext) {
        writer.writeStartArray();
        for (final BsonValue value : array) {
            final Codec codec = this.codecRegistry.get(value.getClass());
            encoderContext.encodeWithChildContext(codec, writer, value);
        }
        writer.writeEndArray();
    }
    
    @Override
    public Class<BsonArray> getEncoderClass() {
        return BsonArray.class;
    }
    
    protected BsonValue readValue(final BsonReader reader, final DecoderContext decoderContext) {
        return this.codecRegistry.get(BsonValueCodecProvider.getClassForBsonType(reader.getCurrentBsonType())).decode(reader, decoderContext);
    }
}
