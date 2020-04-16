// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.codecs.Encoder;
import org.bson.types.ObjectId;
import com.mongodb.assertions.Assertions;
import java.util.ArrayList;
import java.util.List;

public class BulkWriteOperation
{
    private static final String ID_FIELD_NAME = "_id";
    private final boolean ordered;
    private final DBCollection collection;
    private final List<WriteRequest> requests;
    private boolean closed;
    
    BulkWriteOperation(final boolean ordered, final DBCollection collection) {
        this.requests = new ArrayList<WriteRequest>();
        this.ordered = ordered;
        this.collection = collection;
    }
    
    public boolean isOrdered() {
        return this.ordered;
    }
    
    public void insert(final DBObject document) {
        Assertions.isTrue("already executed", !this.closed);
        if (document.get("_id") == null) {
            document.put("_id", new ObjectId());
        }
        this.addRequest(new InsertRequest(document, this.collection.getObjectCodec()));
    }
    
    public BulkWriteRequestBuilder find(final DBObject query) {
        Assertions.isTrue("already executed", !this.closed);
        return new BulkWriteRequestBuilder(this, query, this.collection.getDefaultDBObjectCodec(), this.collection.getObjectCodec());
    }
    
    public BulkWriteResult execute() {
        Assertions.isTrue("already executed", !this.closed);
        this.closed = true;
        return this.collection.executeBulkWriteOperation(this.ordered, this.requests);
    }
    
    public BulkWriteResult execute(final WriteConcern writeConcern) {
        Assertions.isTrue("already executed", !this.closed);
        this.closed = true;
        return this.collection.executeBulkWriteOperation(this.ordered, this.requests, writeConcern);
    }
    
    void addRequest(final WriteRequest request) {
        Assertions.isTrue("already executed", !this.closed);
        this.requests.add(request);
    }
}
