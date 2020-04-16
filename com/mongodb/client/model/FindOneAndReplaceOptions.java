// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;
import java.util.concurrent.TimeUnit;
import org.bson.conversions.Bson;

public class FindOneAndReplaceOptions
{
    private Bson projection;
    private Bson sort;
    private boolean upsert;
    private ReturnDocument returnDocument;
    private long maxTimeMS;
    
    public FindOneAndReplaceOptions() {
        this.returnDocument = ReturnDocument.BEFORE;
    }
    
    public Bson getProjection() {
        return this.projection;
    }
    
    public FindOneAndReplaceOptions projection(final Bson projection) {
        this.projection = projection;
        return this;
    }
    
    public Bson getSort() {
        return this.sort;
    }
    
    public FindOneAndReplaceOptions sort(final Bson sort) {
        this.sort = sort;
        return this;
    }
    
    public boolean isUpsert() {
        return this.upsert;
    }
    
    public FindOneAndReplaceOptions upsert(final boolean upsert) {
        this.upsert = upsert;
        return this;
    }
    
    public ReturnDocument getReturnDocument() {
        return this.returnDocument;
    }
    
    public FindOneAndReplaceOptions returnDocument(final ReturnDocument returnDocument) {
        this.returnDocument = returnDocument;
        return this;
    }
    
    public FindOneAndReplaceOptions maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
}
