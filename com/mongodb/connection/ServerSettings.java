// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.annotations.NotThreadSafe;
import java.util.concurrent.TimeUnit;
import com.mongodb.annotations.Immutable;

@Immutable
public class ServerSettings
{
    private final long heartbeatFrequencyMS;
    private final long minHeartbeatFrequencyMS;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public long getHeartbeatFrequency(final TimeUnit timeUnit) {
        return timeUnit.convert(this.heartbeatFrequencyMS, TimeUnit.MILLISECONDS);
    }
    
    public long getMinHeartbeatFrequency(final TimeUnit timeUnit) {
        return timeUnit.convert(this.minHeartbeatFrequencyMS, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ServerSettings that = (ServerSettings)o;
        return this.heartbeatFrequencyMS == that.heartbeatFrequencyMS && this.minHeartbeatFrequencyMS == that.minHeartbeatFrequencyMS;
    }
    
    @Override
    public int hashCode() {
        int result = (int)(this.heartbeatFrequencyMS ^ this.heartbeatFrequencyMS >>> 32);
        result = 31 * result + (int)(this.minHeartbeatFrequencyMS ^ this.minHeartbeatFrequencyMS >>> 32);
        return result;
    }
    
    @Override
    public String toString() {
        return "ServerSettings{heartbeatFrequencyMS=" + this.heartbeatFrequencyMS + ", minHeartbeatFrequencyMS=" + this.minHeartbeatFrequencyMS + '}';
    }
    
    ServerSettings(final Builder builder) {
        this.heartbeatFrequencyMS = builder.heartbeatFrequencyMS;
        this.minHeartbeatFrequencyMS = builder.minHeartbeatFrequencyMS;
    }
    
    @NotThreadSafe
    public static class Builder
    {
        private long heartbeatFrequencyMS;
        private long minHeartbeatFrequencyMS;
        
        public Builder() {
            this.heartbeatFrequencyMS = 10000L;
            this.minHeartbeatFrequencyMS = 500L;
        }
        
        public Builder heartbeatFrequency(final long heartbeatFrequency, final TimeUnit timeUnit) {
            this.heartbeatFrequencyMS = TimeUnit.MILLISECONDS.convert(heartbeatFrequency, timeUnit);
            return this;
        }
        
        public Builder minHeartbeatFrequency(final long minHeartbeatFrequency, final TimeUnit timeUnit) {
            this.minHeartbeatFrequencyMS = TimeUnit.MILLISECONDS.convert(minHeartbeatFrequency, timeUnit);
            return this;
        }
        
        public ServerSettings build() {
            return new ServerSettings(this);
        }
    }
}
