// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import org.bson.conversions.Bson;

public class CreateCollectionOptions
{
    private boolean autoIndex;
    private long maxDocuments;
    private boolean capped;
    private long sizeInBytes;
    private Boolean usePowerOf2Sizes;
    private Bson storageEngineOptions;
    
    public CreateCollectionOptions() {
        this.autoIndex = true;
    }
    
    public boolean isAutoIndex() {
        return this.autoIndex;
    }
    
    public CreateCollectionOptions autoIndex(final boolean autoIndex) {
        this.autoIndex = autoIndex;
        return this;
    }
    
    public long getMaxDocuments() {
        return this.maxDocuments;
    }
    
    public CreateCollectionOptions maxDocuments(final long maxDocuments) {
        this.maxDocuments = maxDocuments;
        return this;
    }
    
    public boolean isCapped() {
        return this.capped;
    }
    
    public CreateCollectionOptions capped(final boolean capped) {
        this.capped = capped;
        return this;
    }
    
    public long getSizeInBytes() {
        return this.sizeInBytes;
    }
    
    public CreateCollectionOptions sizeInBytes(final long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
        return this;
    }
    
    public Boolean isUsePowerOf2Sizes() {
        return this.usePowerOf2Sizes;
    }
    
    public CreateCollectionOptions usePowerOf2Sizes(final Boolean usePowerOf2Sizes) {
        this.usePowerOf2Sizes = usePowerOf2Sizes;
        return this;
    }
    
    public Bson getStorageEngineOptions() {
        return this.storageEngineOptions;
    }
    
    public CreateCollectionOptions storageEngineOptions(final Bson storageEngineOptions) {
        this.storageEngineOptions = storageEngineOptions;
        return this;
    }
}
