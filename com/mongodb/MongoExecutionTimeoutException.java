// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

public class MongoExecutionTimeoutException extends MongoException
{
    private static final long serialVersionUID = 5955669123800274594L;
    
    public MongoExecutionTimeoutException(final int code, final String message) {
        super(code, message);
    }
}
