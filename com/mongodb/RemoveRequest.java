// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BsonDocument;
import com.mongodb.bulk.DeleteRequest;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.Encoder;

class RemoveRequest extends WriteRequest
{
    private final DBObject query;
    private final boolean multi;
    private final Encoder<DBObject> codec;
    
    public RemoveRequest(final DBObject query, final boolean multi, final Encoder<DBObject> codec) {
        this.query = query;
        this.multi = multi;
        this.codec = codec;
    }
    
    public DBObject getQuery() {
        return this.query;
    }
    
    public boolean isMulti() {
        return this.multi;
    }
    
    @Override
    com.mongodb.bulk.WriteRequest toNew() {
        return new DeleteRequest(new BsonDocumentWrapper<Object>(this.query, this.codec)).multi(this.isMulti());
    }
}
