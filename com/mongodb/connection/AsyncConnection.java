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
import com.mongodb.async.SingleResultCallback;
import com.mongodb.bulk.InsertRequest;
import java.util.List;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.annotations.ThreadSafe;
import com.mongodb.binding.ReferenceCounted;

@ThreadSafe
public interface AsyncConnection extends ReferenceCounted
{
    AsyncConnection retain();
    
    ConnectionDescription getDescription();
    
    void insertAsync(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<InsertRequest> p3, final SingleResultCallback<WriteConcernResult> p4);
    
    void updateAsync(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<UpdateRequest> p3, final SingleResultCallback<WriteConcernResult> p4);
    
    void deleteAsync(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<DeleteRequest> p3, final SingleResultCallback<WriteConcernResult> p4);
    
    void insertCommandAsync(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<InsertRequest> p3, final SingleResultCallback<BulkWriteResult> p4);
    
    void updateCommandAsync(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<UpdateRequest> p3, final SingleResultCallback<BulkWriteResult> p4);
    
    void deleteCommandAsync(final MongoNamespace p0, final boolean p1, final WriteConcern p2, final List<DeleteRequest> p3, final SingleResultCallback<BulkWriteResult> p4);
    
     <T> void commandAsync(final String p0, final BsonDocument p1, final boolean p2, final FieldNameValidator p3, final Decoder<T> p4, final SingleResultCallback<T> p5);
    
    @Deprecated
     <T> void queryAsync(final MongoNamespace p0, final BsonDocument p1, final BsonDocument p2, final int p3, final int p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9, final boolean p10, final Decoder<T> p11, final SingleResultCallback<QueryResult<T>> p12);
    
     <T> void queryAsync(final MongoNamespace p0, final BsonDocument p1, final BsonDocument p2, final int p3, final int p4, final int p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9, final boolean p10, final boolean p11, final Decoder<T> p12, final SingleResultCallback<QueryResult<T>> p13);
    
     <T> void getMoreAsync(final MongoNamespace p0, final long p1, final int p2, final Decoder<T> p3, final SingleResultCallback<QueryResult<T>> p4);
    
    @Deprecated
    void killCursorAsync(final List<Long> p0, final SingleResultCallback<Void> p1);
    
    void killCursorAsync(final MongoNamespace p0, final List<Long> p1, final SingleResultCallback<Void> p2);
}
