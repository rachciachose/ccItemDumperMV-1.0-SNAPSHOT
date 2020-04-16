// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BsonDocument;
import com.mongodb.bulk.UpdateRequest;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.Encoder;

class ReplaceRequest extends WriteRequest
{
    private final DBObject query;
    private final DBObject document;
    private final boolean upsert;
    private final Encoder<DBObject> codec;
    private final Encoder<DBObject> replacementCodec;
    
    public ReplaceRequest(final DBObject query, final DBObject document, final boolean upsert, final Encoder<DBObject> codec, final Encoder<DBObject> replacementCodec) {
        this.query = query;
        this.document = document;
        this.upsert = upsert;
        this.codec = codec;
        this.replacementCodec = replacementCodec;
    }
    
    public DBObject getQuery() {
        return this.query;
    }
    
    public DBObject getDocument() {
        return this.document;
    }
    
    public boolean isUpsert() {
        return this.upsert;
    }
    
    @Override
    com.mongodb.bulk.WriteRequest toNew() {
        return new UpdateRequest(new BsonDocumentWrapper<Object>(this.query, this.codec), new BsonDocumentWrapper<Object>(this.document, this.replacementCodec), com.mongodb.bulk.WriteRequest.Type.REPLACE).upsert(this.isUpsert());
    }
}
