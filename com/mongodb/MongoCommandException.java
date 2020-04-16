// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BsonString;
import org.bson.BsonNumber;
import org.bson.BsonInt32;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.BsonDocumentCodec;
import java.io.Writer;
import org.bson.json.JsonWriter;
import java.io.StringWriter;
import org.bson.BsonDocument;

public class MongoCommandException extends MongoServerException
{
    private static final long serialVersionUID = 8160676451944215078L;
    private final BsonDocument response;
    
    public MongoCommandException(final BsonDocument response, final ServerAddress address) {
        super(extractErrorCode(response), String.format("Command failed with error %s: '%s' on server %s. The full response is %s", extractErrorCode(response), extractErrorMessage(response), address, getResponseAsJson(response)), address);
        this.response = response;
    }
    
    public int getErrorCode() {
        return this.getCode();
    }
    
    public String getErrorMessage() {
        return extractErrorMessage(this.response);
    }
    
    public BsonDocument getResponse() {
        return this.response;
    }
    
    private static String getResponseAsJson(final BsonDocument commandResponse) {
        final StringWriter writer = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(writer);
        new BsonDocumentCodec().encode((BsonWriter)jsonWriter, commandResponse, EncoderContext.builder().build());
        return writer.toString();
    }
    
    private static int extractErrorCode(final BsonDocument response) {
        return response.getNumber("code", new BsonInt32(-1)).intValue();
    }
    
    private static String extractErrorMessage(final BsonDocument response) {
        return response.getString("errmsg", new BsonString("")).getValue();
    }
}
