// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;
import org.bson.conversions.Bson;

public final class BsonField
{
    private final String name;
    private final Bson value;
    
    public BsonField(final String name, final Bson value) {
        this.name = Assertions.notNull("name", name);
        this.value = Assertions.notNull("value", value);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Bson getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "BsonField{name='" + this.name + '\'' + ", value=" + this.value + '}';
    }
}
