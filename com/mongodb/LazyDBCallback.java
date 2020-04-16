// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.types.ObjectId;
import java.util.List;
import java.util.Iterator;
import org.bson.LazyBSONCallback;

public class LazyDBCallback extends LazyBSONCallback implements DBCallback
{
    public LazyDBCallback(final DBCollection collection) {
    }
    
    @Override
    public Object createObject(final byte[] bytes, final int offset) {
        final LazyDBObject document = new LazyDBObject(bytes, offset, this);
        final Iterator<String> iterator = document.keySet().iterator();
        if (iterator.hasNext() && iterator.next().equals("$ref") && iterator.hasNext() && iterator.next().equals("$id")) {
            return new DBRef((String)document.get("$ref"), document.get("$id"));
        }
        return document;
    }
    
    @Override
    public List createArray(final byte[] bytes, final int offset) {
        return new LazyDBList(bytes, offset, this);
    }
    
    @Override
    public Object createDBRef(final String ns, final ObjectId id) {
        return new DBRef(ns, id);
    }
}
