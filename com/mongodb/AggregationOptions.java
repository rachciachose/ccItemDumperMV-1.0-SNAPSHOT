// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.annotations.NotThreadSafe;
import java.util.concurrent.TimeUnit;

public class AggregationOptions
{
    private final Integer batchSize;
    private final Boolean allowDiskUse;
    private final OutputMode outputMode;
    private final long maxTimeMS;
    
    AggregationOptions(final Builder builder) {
        this.batchSize = builder.batchSize;
        this.allowDiskUse = builder.allowDiskUse;
        this.outputMode = builder.outputMode;
        this.maxTimeMS = builder.maxTimeMS;
    }
    
    public Boolean getAllowDiskUse() {
        return this.allowDiskUse;
    }
    
    public Integer getBatchSize() {
        return this.batchSize;
    }
    
    public OutputMode getOutputMode() {
        return this.outputMode;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AggregationOptions{");
        sb.append("allowDiskUse=").append(this.allowDiskUse);
        sb.append(", batchSize=").append(this.batchSize);
        sb.append(", outputMode=").append(this.outputMode);
        sb.append(", maxTimeMS=").append(this.maxTimeMS);
        sb.append('}');
        return sb.toString();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public enum OutputMode
    {
        INLINE, 
        CURSOR;
    }
    
    @NotThreadSafe
    public static class Builder
    {
        private Integer batchSize;
        private Boolean allowDiskUse;
        private OutputMode outputMode;
        private long maxTimeMS;
        
        private Builder() {
            this.outputMode = OutputMode.INLINE;
        }
        
        public Builder batchSize(final Integer size) {
            this.batchSize = size;
            return this;
        }
        
        public Builder allowDiskUse(final Boolean allowDiskUse) {
            this.allowDiskUse = allowDiskUse;
            return this;
        }
        
        public Builder outputMode(final OutputMode mode) {
            this.outputMode = mode;
            return this;
        }
        
        public Builder maxTime(final long maxTime, final TimeUnit timeUnit) {
            this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
            return this;
        }
        
        public AggregationOptions build() {
            return new AggregationOptions(this);
        }
    }
}
