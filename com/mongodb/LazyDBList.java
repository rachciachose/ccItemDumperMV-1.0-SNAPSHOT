// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.LazyBSONCallback;
import org.bson.LazyBSONList;

public class LazyDBList extends LazyBSONList implements DBObject
{
    private boolean isPartial;
    
    public LazyDBList(final byte[] bytes, final LazyBSONCallback callback) {
        super(bytes, callback);
    }
    
    public LazyDBList(final byte[] bytes, final int offset, final LazyBSONCallback callback) {
        super(bytes, offset, callback);
    }
    
    @Override
    public void markAsPartialObject() {
        this.isPartial = true;
    }
    
    @Override
    public boolean isPartialObject() {
        return this.isPartial;
    }
}
