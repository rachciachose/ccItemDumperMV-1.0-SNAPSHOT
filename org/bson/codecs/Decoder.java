// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonReader;

public interface Decoder<T>
{
    T decode(final BsonReader p0, final DecoderContext p1);
}
