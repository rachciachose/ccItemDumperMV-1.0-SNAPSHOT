// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.codecs.Encoder;

public class BulkWriteRequestBuilder
{
    private final BulkWriteOperation bulkWriteOperation;
    private final DBObject query;
    private final Encoder<DBObject> codec;
    private final Encoder<DBObject> replacementCodec;
    
    BulkWriteRequestBuilder(final BulkWriteOperation bulkWriteOperation, final DBObject query, final Encoder<DBObject> queryCodec, final Encoder<DBObject> replacementCodec) {
        this.bulkWriteOperation = bulkWriteOperation;
        this.query = query;
        this.codec = queryCodec;
        this.replacementCodec = replacementCodec;
    }
    
    public void remove() {
        this.bulkWriteOperation.addRequest(new RemoveRequest(this.query, true, this.codec));
    }
    
    public void removeOne() {
        this.bulkWriteOperation.addRequest(new RemoveRequest(this.query, false, this.codec));
    }
    
    public void replaceOne(final DBObject document) {
        new BulkUpdateRequestBuilder(this.bulkWriteOperation, this.query, false, this.codec, this.replacementCodec).replaceOne(document);
    }
    
    public void update(final DBObject update) {
        new BulkUpdateRequestBuilder(this.bulkWriteOperation, this.query, false, this.codec, this.replacementCodec).update(update);
    }
    
    public void updateOne(final DBObject update) {
        new BulkUpdateRequestBuilder(this.bulkWriteOperation, this.query, false, this.codec, this.replacementCodec).updateOne(update);
    }
    
    public BulkUpdateRequestBuilder upsert() {
        return new BulkUpdateRequestBuilder(this.bulkWriteOperation, this.query, true, this.codec, this.replacementCodec);
    }
}
