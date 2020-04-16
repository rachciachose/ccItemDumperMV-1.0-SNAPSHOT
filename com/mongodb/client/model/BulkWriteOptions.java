// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

public final class BulkWriteOptions
{
    private boolean ordered;
    
    public BulkWriteOptions() {
        this.ordered = true;
    }
    
    public boolean isOrdered() {
        return this.ordered;
    }
    
    public BulkWriteOptions ordered(final boolean ordered) {
        this.ordered = ordered;
        return this;
    }
}
