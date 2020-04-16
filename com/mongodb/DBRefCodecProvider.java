// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.configuration.CodecProvider;

public class DBRefCodecProvider implements CodecProvider
{
    @Override
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        if (clazz == DBRef.class) {
            return (Codec<T>)new DBRefCodec(registry);
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o != null && this.getClass() == o.getClass());
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
}
