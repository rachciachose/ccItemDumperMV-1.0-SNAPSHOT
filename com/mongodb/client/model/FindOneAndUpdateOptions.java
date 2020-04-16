// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;
import java.util.concurrent.TimeUnit;
import org.bson.conversions.Bson;

public class FindOneAndUpdateOptions
{
    private Bson projection;
    private Bson sort;
    private boolean upsert;
    private ReturnDocument returnDocument;
    private long maxTimeMS;
    
    public FindOneAndUpdateOptions() {
        this.returnDocument = ReturnDocument.BEFORE;
    }
    
    public Bson getProjection() {
        return this.projection;
    }
    
    public FindOneAndUpdateOptions projection(final Bson projection) {
        this.projection = projection;
        return this;
    }
    
    public Bson getSort() {
        return this.sort;
    }
    
    public FindOneAndUpdateOptions sort(final Bson sort) {
        this.sort = sort;
        return this;
    }
    
    public boolean isUpsert() {
        return this.upsert;
    }
    
    public FindOneAndUpdateOptions upsert(final boolean upsert) {
        this.upsert = upsert;
        return this;
    }
    
    public ReturnDocument getReturnDocument() {
        return this.returnDocument;
    }
    
    public FindOneAndUpdateOptions returnDocument(final ReturnDocument returnDocument) {
        this.returnDocument = returnDocument;
        return this;
    }
    
    public FindOneAndUpdateOptions maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
}
