// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.bulk.WriteConcernError;

public class MongoWriteConcernException extends MongoServerException
{
    private static final long serialVersionUID = 4577579466973523211L;
    private final WriteConcernError writeConcernError;
    
    public MongoWriteConcernException(final WriteConcernError writeConcernError, final ServerAddress serverAddress) {
        super(writeConcernError.getCode(), writeConcernError.getMessage(), serverAddress);
        this.writeConcernError = writeConcernError;
    }
    
    public WriteConcernError getWriteConcernError() {
        return this.writeConcernError;
    }
}
