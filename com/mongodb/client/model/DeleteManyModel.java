// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;
import org.bson.conversions.Bson;

public final class DeleteManyModel<T> extends WriteModel<T>
{
    private final Bson filter;
    
    public DeleteManyModel(final Bson filter) {
        this.filter = Assertions.notNull("filter", filter);
    }
    
    public Bson getFilter() {
        return this.filter;
    }
}
