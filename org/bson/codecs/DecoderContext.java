// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

public final class DecoderContext
{
    public static Builder builder() {
        return new Builder();
    }
    
    private DecoderContext(final Builder builder) {
    }
    
    public static final class Builder
    {
        public DecoderContext build() {
            return new DecoderContext(this, null);
        }
    }
}
