// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;
import org.bson.conversions.Bson;

public class IndexModel
{
    private final Bson keys;
    private final IndexOptions options;
    
    public IndexModel(final Bson keys) {
        this(keys, new IndexOptions());
    }
    
    public IndexModel(final Bson keys, final IndexOptions options) {
        this.keys = Assertions.notNull("keys", keys);
        this.options = Assertions.notNull("options", options);
    }
    
    public Bson getKeys() {
        return this.keys;
    }
    
    public IndexOptions getOptions() {
        return this.options;
    }
}
