// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.assertions.Assertions;
import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.annotations.Immutable;

@Immutable
public final class ParallelScanOptions
{
    private final int numCursors;
    private final int batchSize;
    private final ReadPreference readPreference;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public int getNumCursors() {
        return this.numCursors;
    }
    
    public int getBatchSize() {
        return this.batchSize;
    }
    
    public ReadPreference getReadPreference() {
        return this.readPreference;
    }
    
    private ParallelScanOptions(final Builder builder) {
        this.numCursors = builder.numCursors;
        this.batchSize = builder.batchSize;
        this.readPreference = builder.readPreference;
    }
    
    @NotThreadSafe
    public static class Builder
    {
        private int numCursors;
        private int batchSize;
        private ReadPreference readPreference;
        
        public Builder() {
            this.numCursors = 1;
        }
        
        public Builder numCursors(final int numCursors) {
            Assertions.isTrue("numCursors >= 1", numCursors >= 1);
            this.numCursors = numCursors;
            return this;
        }
        
        public Builder batchSize(final int batchSize) {
            Assertions.isTrue("batchSize >= 0", batchSize >= 0);
            this.batchSize = batchSize;
            return this;
        }
        
        public Builder readPreference(final ReadPreference readPreference) {
            this.readPreference = Assertions.notNull("readPreference", readPreference);
            return this;
        }
        
        public ParallelScanOptions build() {
            return new ParallelScanOptions(this, null);
        }
    }
}
