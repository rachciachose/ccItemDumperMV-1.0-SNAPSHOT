// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonWriter;

public interface Encoder<T>
{
    void encode(final BsonWriter p0, final T p1, final EncoderContext p2);
    
    Class<T> getEncoderClass();
}
