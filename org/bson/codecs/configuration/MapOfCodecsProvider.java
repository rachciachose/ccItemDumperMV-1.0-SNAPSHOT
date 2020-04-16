// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs.configuration;

import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import org.bson.codecs.Codec;
import java.util.Map;

final class MapOfCodecsProvider implements CodecProvider
{
    private final Map<Class<?>, Codec<?>> codecsMap;
    
    public MapOfCodecsProvider(final List<? extends Codec<?>> codecsList) {
        this.codecsMap = new HashMap<Class<?>, Codec<?>>();
        for (final Codec<?> codec : codecsList) {
            this.codecsMap.put(codec.getEncoderClass(), codec);
        }
    }
    
    @Override
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        return (Codec<T>)this.codecsMap.get(clazz);
    }
}
