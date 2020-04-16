// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;
import org.bson.conversions.Bson;

public final class UpdateOneModel<T> extends WriteModel<T>
{
    private final Bson filter;
    private final Bson update;
    private final UpdateOptions options;
    
    public UpdateOneModel(final Bson filter, final Bson update) {
        this(filter, update, new UpdateOptions());
    }
    
    public UpdateOneModel(final Bson filter, final Bson update, final UpdateOptions options) {
        this.filter = Assertions.notNull("filter", filter);
        this.update = Assertions.notNull("update", update);
        this.options = Assertions.notNull("options", options);
    }
    
    public Bson getFilter() {
        return this.filter;
    }
    
    public Bson getUpdate() {
        return this.update;
    }
    
    public UpdateOptions getOptions() {
        return this.options;
    }
}
