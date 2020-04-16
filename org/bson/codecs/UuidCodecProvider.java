// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import java.util.UUID;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecProvider;

public class UuidCodecProvider implements CodecProvider
{
    private UuidRepresentation uuidRepresentation;
    
    public UuidCodecProvider(final UuidRepresentation uuidRepresentation) {
        this.uuidRepresentation = uuidRepresentation;
    }
    
    @Override
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        if (clazz == UUID.class) {
            return (Codec<T>)new UuidCodec(this.uuidRepresentation);
        }
        return null;
    }
}
