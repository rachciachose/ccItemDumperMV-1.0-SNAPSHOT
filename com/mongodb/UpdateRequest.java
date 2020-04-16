// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.Encoder;

class UpdateRequest extends WriteRequest
{
    private final DBObject query;
    private final DBObject update;
    private final boolean multi;
    private final boolean upsert;
    private final Encoder<DBObject> codec;
    
    public UpdateRequest(final DBObject query, final DBObject update, final boolean multi, final boolean upsert, final Encoder<DBObject> codec) {
        this.query = query;
        this.update = update;
        this.multi = multi;
        this.upsert = upsert;
        this.codec = codec;
    }
    
    public DBObject getQuery() {
        return this.query;
    }
    
    public DBObject getUpdate() {
        return this.update;
    }
    
    public boolean isUpsert() {
        return this.upsert;
    }
    
    public boolean isMulti() {
        return this.multi;
    }
    
    @Override
    com.mongodb.bulk.WriteRequest toNew() {
        return new com.mongodb.bulk.UpdateRequest(new BsonDocumentWrapper<Object>(this.query, this.codec), new BsonDocumentWrapper<Object>(this.update, this.codec), com.mongodb.bulk.WriteRequest.Type.UPDATE).upsert(this.isUpsert()).multi(this.isMulti());
    }
}
