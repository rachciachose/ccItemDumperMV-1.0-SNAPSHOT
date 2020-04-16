// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import org.bson.BsonNumber;
import org.bson.BsonInt32;
import org.bson.BsonValue;
import org.bson.BsonDocument;

public class WriteConcernException extends MongoServerException
{
    private static final long serialVersionUID = -1100801000476719450L;
    private final WriteConcernResult writeConcernResult;
    private final BsonDocument response;
    
    public WriteConcernException(final BsonDocument response, final ServerAddress address, final WriteConcernResult writeConcernResult) {
        super(extractErrorCode(response), String.format("Write failed with error code %d and error message '%s'", extractErrorCode(response), extractErrorMessage(response)), address);
        this.response = response;
        this.writeConcernResult = writeConcernResult;
    }
    
    public static int extractErrorCode(final BsonDocument response) {
        if (response.containsKey("err")) {
            final String errorMessage = extractErrorMessage(response);
            if (errorMessage.contains("E11000 duplicate key error")) {
                return 11000;
            }
        }
        if (!response.containsKey("code") && response.containsKey("errObjects")) {
            for (final BsonValue curErrorDocument : response.getArray("errObjects")) {
                if (extractErrorMessage(response).equals(extractErrorMessage(curErrorDocument.asDocument()))) {
                    return curErrorDocument.asDocument().getNumber("code").intValue();
                }
            }
        }
        return response.getNumber("code", new BsonInt32(-1)).intValue();
    }
    
    public static String extractErrorMessage(final BsonDocument response) {
        if (response.isString("err")) {
            return response.getString("err").getValue();
        }
        return null;
    }
    
    public WriteConcernResult getWriteConcernResult() {
        return this.writeConcernResult;
    }
    
    public int getErrorCode() {
        return extractErrorCode(this.response);
    }
    
    public String getErrorMessage() {
        return extractErrorMessage(this.response);
    }
    
    public BsonDocument getResponse() {
        return this.response;
    }
}
