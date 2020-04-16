// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.codecs.Encoder;

public class BulkUpdateRequestBuilder
{
    private final BulkWriteOperation bulkWriteOperation;
    private final DBObject query;
    private final boolean upsert;
    private final Encoder<DBObject> queryCodec;
    private final Encoder<DBObject> replacementCodec;
    
    BulkUpdateRequestBuilder(final BulkWriteOperation bulkWriteOperation, final DBObject query, final boolean upsert, final Encoder<DBObject> queryCodec, final Encoder<DBObject> replacementCodec) {
        this.bulkWriteOperation = bulkWriteOperation;
        this.query = query;
        this.upsert = upsert;
        this.queryCodec = queryCodec;
        this.replacementCodec = replacementCodec;
    }
    
    public void replaceOne(final DBObject document) {
        this.bulkWriteOperation.addRequest(new ReplaceRequest(this.query, document, this.upsert, this.queryCodec, this.replacementCodec));
    }
    
    public void update(final DBObject update) {
        this.bulkWriteOperation.addRequest(new UpdateRequest(this.query, update, true, this.upsert, this.queryCodec));
    }
    
    public void updateOne(final DBObject update) {
        this.bulkWriteOperation.addRequest(new UpdateRequest(this.query, update, false, this.upsert, this.queryCodec));
    }
}
