// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

public class MongoSocketClosedException extends MongoSocketException
{
    private static final long serialVersionUID = -6855036625330867705L;
    
    public MongoSocketClosedException(final String message, final ServerAddress address) {
        super(message, address);
    }
}
