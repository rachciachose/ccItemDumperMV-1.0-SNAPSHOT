// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;
import org.bson.conversions.Bson;

public class DeleteOneModel<T> extends WriteModel<T>
{
    private final Bson filter;
    
    public DeleteOneModel(final Bson filter) {
        this.filter = Assertions.notNull("filter", filter);
    }
    
    public Bson getFilter() {
        return this.filter;
    }
}
