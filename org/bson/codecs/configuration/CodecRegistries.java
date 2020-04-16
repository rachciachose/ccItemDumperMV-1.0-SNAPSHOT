// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs.configuration;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.bson.codecs.Codec;

public final class CodecRegistries
{
    public static CodecRegistry fromCodecs(final Codec<?>... codecs) {
        return fromCodecs(Arrays.asList(codecs));
    }
    
    public static CodecRegistry fromCodecs(final List<? extends Codec<?>> codecs) {
        return fromProviders(new MapOfCodecsProvider(codecs));
    }
    
    public static CodecRegistry fromProviders(final CodecProvider... providers) {
        return fromProviders(Arrays.asList(providers));
    }
    
    public static CodecRegistry fromProviders(final List<? extends CodecProvider> providers) {
        return new ProvidersCodecRegistry(providers);
    }
    
    public static CodecRegistry fromRegistries(final CodecRegistry... registries) {
        return fromRegistries(Arrays.asList(registries));
    }
    
    public static CodecRegistry fromRegistries(final List<? extends CodecRegistry> registries) {
        final List<CodecProvider> providers = new ArrayList<CodecProvider>();
        for (final CodecRegistry registry : registries) {
            providers.add(providerFromRegistry(registry));
        }
        return new ProvidersCodecRegistry(providers);
    }
    
    private static CodecProvider providerFromRegistry(final CodecRegistry innerRegistry) {
        if (innerRegistry instanceof CodecProvider) {
            return (CodecProvider)innerRegistry;
        }
        return new CodecProvider() {
            @Override
            public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry outerRregistry) {
                try {
                    return innerRegistry.get(clazz);
                }
                catch (CodecConfigurationException e) {
                    return null;
                }
            }
        };
    }
}
