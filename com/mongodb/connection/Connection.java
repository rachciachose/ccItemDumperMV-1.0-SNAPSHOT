// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.codecs.Decoder;
import org.bson.FieldNameValidator;
import org.bson.BsonDocument;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.bulk.DeleteRequest;
import com.mongodb.bulk.UpdateRequest;
import com.mongodb.WriteConcernResult;
import com.mongodb.bulk.InsertRequest;
import java.util.List;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.annotations.ThreadSafe;
import com.mongodb.binding.ReferenceCounted;

@ThreadSafe
public interface Connection extends ReferenceCounted
{
    Connection retain();
    
    ConnectionDescription getDescription();
    
    WriteConcernResult insert(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<InsertRequest> p3);
    
    WriteConcernResult update(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<UpdateRequest> p3);
    
    WriteConcernResult delete(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<DeleteRequest> p3);
    
    BulkWriteResult insertCommand(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<InsertRequest> p3);
    
    BulkWriteResult updateCommand(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<UpdateRequest> p3);
    
    BulkWriteResult deleteCommand(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<DeleteRequest> p3);
    
     <T> T command(final String p0, final BsonDocument p1, final boolean p2, final FieldNameValidator p3, final Decoder<T> p4);
    
    @Deprecated
     <T> QueryResult<T> query(final MongoNamespace p0, final BsonDocument p1, final BsonDocument p2, final int p3, final int p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9, final boolean p10, final Decoder<T> p11);
    
     <T> QueryResult<T> query(final MongoNamespace p0, final BsonDocument p1, final BsonDocument p2, final int p3, final int p4, final int p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9, final boolean p10, final boolean p11, final Decoder<T> p12);
    
     <T> QueryResult<T> getMore(final MongoNamespace p0, final long p1, final int p2, final Decoder<T> p3);
    
    @Deprecated
    void killCursor(final List<Long> p0);
    
    void killCursor(final MongoNamespace p0, final List<Long> p1);
}
