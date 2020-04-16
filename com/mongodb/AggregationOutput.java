// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.List;

public class AggregationOutput
{
    private final List<DBObject> results;
    
    AggregationOutput(final List<DBObject> results) {
        this.results = results;
    }
    
    public Iterable<DBObject> results() {
        return this.results;
    }
}
