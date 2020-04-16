// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.io.IOException;
import java.io.OutputStream;
import org.bson.BSONObject;
import org.bson.io.OutputBuffer;

public class LazyDBEncoder implements DBEncoder
{
    @Override
    public int writeObject(final OutputBuffer outputBuffer, final BSONObject document) {
        if (!(document instanceof LazyDBObject)) {
            throw new IllegalArgumentException("LazyDBEncoder can only encode BSONObject instances of type LazyDBObject");
        }
        final LazyDBObject lazyDBObject = (LazyDBObject)document;
        try {
            return lazyDBObject.pipe(outputBuffer);
        }
        catch (IOException e) {
            throw new MongoException("Exception serializing a LazyDBObject", e);
        }
    }
}
