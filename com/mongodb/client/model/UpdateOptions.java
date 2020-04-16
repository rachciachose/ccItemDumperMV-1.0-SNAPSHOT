// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

public class UpdateOptions
{
    private boolean upsert;
    
    public boolean isUpsert() {
        return this.upsert;
    }
    
    public UpdateOptions upsert(final boolean upsert) {
        this.upsert = upsert;
        return this;
    }
}
