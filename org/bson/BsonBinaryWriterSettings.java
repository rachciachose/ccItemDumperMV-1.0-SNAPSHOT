// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

public class BsonBinaryWriterSettings
{
    private final int maxDocumentSize;
    
    public BsonBinaryWriterSettings(final int maxDocumentSize) {
        this.maxDocumentSize = maxDocumentSize;
    }
    
    public BsonBinaryWriterSettings() {
        this(Integer.MAX_VALUE);
    }
    
    public int getMaxDocumentSize() {
        return this.maxDocumentSize;
    }
}
