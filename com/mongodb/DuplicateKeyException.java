// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BsonDocument;

public class DuplicateKeyException extends WriteConcernException
{
    private static final long serialVersionUID = -4415279469780082174L;
    
    public DuplicateKeyException(final BsonDocument response, final ServerAddress address, final WriteConcernResult writeConcernResult) {
        super(response, address, writeConcernResult);
    }
}
