// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BSONObject;
import java.io.IOException;
import org.bson.BSONCallback;
import java.io.InputStream;
import org.bson.LazyBSONDecoder;

public class LazyDBDecoder extends LazyBSONDecoder implements DBDecoder
{
    public static final DBDecoderFactory FACTORY;
    
    @Override
    public DBCallback getDBCallback(final DBCollection collection) {
        return new LazyDBCallback(collection);
    }
    
    @Override
    public DBObject readObject(final InputStream in) throws IOException {
        final DBCallback dbCallback = this.getDBCallback(null);
        this.decode(in, dbCallback);
        return (DBObject)dbCallback.get();
    }
    
    @Override
    public DBObject decode(final InputStream input, final DBCollection collection) throws IOException {
        final DBCallback callback = this.getDBCallback(collection);
        this.decode(input, callback);
        return (DBObject)callback.get();
    }
    
    @Override
    public DBObject decode(final byte[] bytes, final DBCollection collection) {
        final DBCallback callback = this.getDBCallback(collection);
        this.decode(bytes, callback);
        return (DBObject)callback.get();
    }
    
    static {
        FACTORY = new DBDecoderFactory() {
            @Override
            public DBDecoder create() {
                return new LazyDBDecoder();
            }
        };
    }
}
