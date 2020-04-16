// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.bulk;

public abstract class WriteRequest
{
    public abstract Type getType();
    
    public enum Type
    {
        INSERT, 
        UPDATE, 
        REPLACE, 
        DELETE;
    }
}
