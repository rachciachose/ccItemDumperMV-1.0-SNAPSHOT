// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs.configuration;

import org.bson.codecs.Codec;

public interface CodecRegistry
{
     <T> Codec<T> get(final Class<T> p0);
}
