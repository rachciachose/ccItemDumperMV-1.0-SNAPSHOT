// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import com.mongodb.assertions.Assertions;

public final class InsertOneModel<T> extends WriteModel<T>
{
    private final T document;
    
    public InsertOneModel(final T document) {
        this.document = Assertions.notNull("document", document);
    }
    
    public T getDocument() {
        return this.document;
    }
}
