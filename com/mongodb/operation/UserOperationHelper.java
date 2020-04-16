// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import java.util.List;
import org.bson.BsonArray;
import java.util.Arrays;
import org.bson.BsonBoolean;
import com.mongodb.internal.authentication.NativeAuthenticationHelper;
import org.bson.BsonValue;
import org.bson.BsonString;
import org.bson.BsonDocument;
import com.mongodb.MongoCredential;

final class UserOperationHelper
{
    static BsonDocument asCommandDocument(final MongoCredential credential, final boolean readOnly, final String commandName) {
        final BsonDocument document = new BsonDocument();
        document.put(commandName, new BsonString(credential.getUserName()));
        document.put("pwd", new BsonString(NativeAuthenticationHelper.createAuthenticationHash(credential.getUserName(), credential.getPassword())));
        document.put("digestPassword", BsonBoolean.FALSE);
        document.put("roles", new BsonArray(Arrays.asList(new BsonString(getRoleName(credential, readOnly)))));
        return document;
    }
    
    private static String getRoleName(final MongoCredential credential, final boolean readOnly) {
        return credential.getSource().equals("admin") ? (readOnly ? "readAnyDatabase" : "root") : (readOnly ? "read" : "dbOwner");
    }
    
    static BsonDocument asCollectionQueryDocument(final MongoCredential credential) {
        return new BsonDocument("user", new BsonString(credential.getUserName()));
    }
    
    static BsonDocument asCollectionUpdateDocument(final MongoCredential credential, final boolean readOnly) {
        return asCollectionQueryDocument(credential).append("pwd", new BsonString(NativeAuthenticationHelper.createAuthenticationHash(credential.getUserName(), credential.getPassword()))).append("readOnly", BsonBoolean.valueOf(readOnly));
    }
    
    static BsonDocument asCollectionInsertDocument(final MongoCredential credential, final boolean readOnly) {
        return asCollectionUpdateDocument(credential, readOnly).append("_id", new BsonObjectId(new ObjectId()));
    }
}
