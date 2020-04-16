// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

public final class InsertManyOptions
{
    private boolean ordered;
    
    public InsertManyOptions() {
        this.ordered = true;
    }
    
    public boolean isOrdered() {
        return this.ordered;
    }
    
    public InsertManyOptions ordered(final boolean ordered) {
        this.ordered = ordered;
        return this;
    }
}
