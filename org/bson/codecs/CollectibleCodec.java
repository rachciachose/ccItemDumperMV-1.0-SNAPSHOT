// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.BsonValue;

public interface CollectibleCodec<T> extends Codec<T>
{
    T generateIdIfAbsentFromDocument(final T p0);
    
    boolean documentHasId(final T p0);
    
    BsonValue getDocumentId(final T p0);
}
