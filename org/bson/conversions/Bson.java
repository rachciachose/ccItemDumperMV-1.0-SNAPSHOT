// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.conversions;

import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;

public interface Bson
{
     <TDocument> BsonDocument toBsonDocument(final Class<TDocument> p0, final CodecRegistry p1);
}
