// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

public class BsonWriterSettings
{
    private final int maxSerializationDepth;
    
    public BsonWriterSettings(final int maxSerializationDepth) {
        this.maxSerializationDepth = maxSerializationDepth;
    }
    
    public BsonWriterSettings() {
        this(1024);
    }
    
    public int getMaxSerializationDepth() {
        return this.maxSerializationDepth;
    }
}
