// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.LazyBSONCallback;
import com.mongodb.annotations.Immutable;
import org.bson.LazyBSONObject;

@Immutable
public class LazyDBObject extends LazyBSONObject implements DBObject
{
    private boolean isPartial;
    
    public LazyDBObject(final byte[] bytes, final LazyBSONCallback callback) {
        super(bytes, callback);
        this.isPartial = false;
    }
    
    public LazyDBObject(final byte[] bytes, final int offset, final LazyBSONCallback callback) {
        super(bytes, offset, callback);
        this.isPartial = false;
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
