// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.types.BSONTimestamp;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.configuration.CodecProvider;

public class DBObjectCodecProvider implements CodecProvider
{
    private final BsonTypeClassMap bsonTypeClassMap;
    
    public DBObjectCodecProvider() {
        this(DBObjectCodec.getDefaultBsonTypeClassMap());
    }
    
    public DBObjectCodecProvider(final BsonTypeClassMap bsonTypeClassMap) {
        this.bsonTypeClassMap = bsonTypeClassMap;
    }
    
    @Override
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        if (clazz == BSONTimestamp.class) {
            return (Codec<T>)new BSONTimestampCodec();
        }
        if (DBObject.class.isAssignableFrom(clazz)) {
            return (Codec<T>)new DBObjectCodec(registry, this.bsonTypeClassMap);
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o != null && this.getClass() == o.getClass());
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
