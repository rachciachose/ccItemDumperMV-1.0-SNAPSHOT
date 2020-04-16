// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;
import java.util.concurrent.TimeUnit;
import org.bson.conversions.Bson;

public class CountOptions
{
    private Bson hint;
    private String hintString;
    private int limit;
    private int skip;
    private long maxTimeMS;
    
    public Bson getHint() {
        return this.hint;
    }
    
    public String getHintString() {
        return this.hintString;
    }
    
    public CountOptions hint(final Bson hint) {
        this.hint = hint;
        return this;
    }
    
    public CountOptions hintString(final String hint) {
        this.hintString = hint;
        return this;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public CountOptions limit(final int limit) {
        this.limit = limit;
        return this;
    }
    
    public int getSkip() {
        return this.skip;
    }
    
    public CountOptions skip(final int skip) {
        this.skip = skip;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public CountOptions maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
}
