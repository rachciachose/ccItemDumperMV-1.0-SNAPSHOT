// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;
import org.bson.conversions.Bson;

public final class ReplaceOneModel<T> extends WriteModel<T>
{
    private final Bson filter;
    private final T replacement;
    private final UpdateOptions options;
    
    public ReplaceOneModel(final Bson filter, final T replacement) {
        this(filter, replacement, new UpdateOptions());
    }
    
    public ReplaceOneModel(final Bson filter, final T replacement, final UpdateOptions options) {
        this.filter = Assertions.notNull("filter", filter);
        this.replacement = Assertions.notNull("replacement", replacement);
        this.options = Assertions.notNull("options", options);
    }
    
    public Bson getFilter() {
        return this.filter;
    }
    
    public T getReplacement() {
        return this.replacement;
    }
    
    public UpdateOptions getOptions() {
        return this.options;
    }
}
