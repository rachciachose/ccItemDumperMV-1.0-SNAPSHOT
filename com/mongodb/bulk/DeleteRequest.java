// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.bulk;

import com.mongodb.assertions.Assertions;
import org.bson.BsonDocument;

public final class DeleteRequest extends WriteRequest
{
    private final BsonDocument filter;
    private boolean isMulti;
    
    public DeleteRequest(final BsonDocument filter) {
        this.isMulti = true;
        this.filter = Assertions.notNull("filter", filter);
    }
    
    public BsonDocument getFilter() {
        return this.filter;
    }
    
    public DeleteRequest multi(final boolean isMulti) {
        this.isMulti = isMulti;
        return this;
    }
    
    public boolean isMulti() {
        return this.isMulti;
    }
    
    @Override
    public Type getType() {
        return Type.DELETE;
    }
}
