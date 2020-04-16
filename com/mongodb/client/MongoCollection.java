// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import com.mongodb.client.model.RenameCollectionOptions;
import org.bson.Document;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.WriteModel;
import java.util.List;
import com.mongodb.client.model.CountOptions;
import org.bson.conversions.Bson;
import com.mongodb.WriteConcern;
import com.mongodb.ReadPreference;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoNamespace;
import com.mongodb.annotations.ThreadSafe;

@ThreadSafe
public interface MongoCollection<TDocument>
{
    MongoNamespace getNamespace();
    
    Class<TDocument> getDocumentClass();
    
    CodecRegistry getCodecRegistry();
    
    ReadPreference getReadPreference();
    
    WriteConcern getWriteConcern();
    
     <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(final Class<NewTDocument> p0);
    
    MongoCollection<TDocument> withCodecRegistry(final CodecRegistry p0);
    
    MongoCollection<TDocument> withReadPreference(final ReadPreference p0);
    
    MongoCollection<TDocument> withWriteConcern(final WriteConcern p0);
    
    long count();
    
    long count(final Bson p0);
    
    long count(final Bson p0, final CountOptions p1);
    
     <TResult> DistinctIterable<TResult> distinct(final String p0, final Class<TResult> p1);
    
     <TResult> DistinctIterable<TResult> distinct(final String p0, final Bson p1, final Class<TResult> p2);
    
    FindIterable<TDocument> find();
    
     <TResult> FindIterable<TResult> find(final Class<TResult> p0);
    
    FindIterable<TDocument> find(final Bson p0);
    
     <TResult> FindIterable<TResult> find(final Bson p0, final Class<TResult> p1);
    
    AggregateIterable<TDocument> aggregate(final List<? extends Bson> p0);
    
     <TResult> AggregateIterable<TResult> aggregate(final List<? extends Bson> p0, final Class<TResult> p1);
    
    MapReduceIterable<TDocument> mapReduce(final String p0, final String p1);
    
     <TResult> MapReduceIterable<TResult> mapReduce(final String p0, final String p1, final Class<TResult> p2);
    
    BulkWriteResult bulkWrite(final List<? extends WriteModel<? extends TDocument>> p0);
    
    BulkWriteResult bulkWrite(final List<? extends WriteModel<? extends TDocument>> p0, final BulkWriteOptions p1);
    
    void insertOne(final TDocument p0);
    
    void insertMany(final List<? extends TDocument> p0);
    
    void insertMany(final List<? extends TDocument> p0, final InsertManyOptions p1);
    
    DeleteResult deleteOne(final Bson p0);
    
    DeleteResult deleteMany(final Bson p0);
    
    UpdateResult replaceOne(final Bson p0, final TDocument p1);
    
    UpdateResult replaceOne(final Bson p0, final TDocument p1, final UpdateOptions p2);
    
    UpdateResult updateOne(final Bson p0, final Bson p1);
    
    UpdateResult updateOne(final Bson p0, final Bson p1, final UpdateOptions p2);
    
    UpdateResult updateMany(final Bson p0, final Bson p1);
    
    UpdateResult updateMany(final Bson p0, final Bson p1, final UpdateOptions p2);
    
    TDocument findOneAndDelete(final Bson p0);
    
    TDocument findOneAndDelete(final Bson p0, final FindOneAndDeleteOptions p1);
    
    TDocument findOneAndReplace(final Bson p0, final TDocument p1);
    
    TDocument findOneAndReplace(final Bson p0, final TDocument p1, final FindOneAndReplaceOptions p2);
    
    TDocument findOneAndUpdate(final Bson p0, final Bson p1);
    
    TDocument findOneAndUpdate(final Bson p0, final Bson p1, final FindOneAndUpdateOptions p2);
    
    void drop();
    
    String createIndex(final Bson p0);
    
    String createIndex(final Bson p0, final IndexOptions p1);
    
    List<String> createIndexes(final List<IndexModel> p0);
    
    ListIndexesIterable<Document> listIndexes();
    
     <TResult> ListIndexesIterable<TResult> listIndexes(final Class<TResult> p0);
    
    void dropIndex(final String p0);
    
    void dropIndex(final Bson p0);
    
    void dropIndexes();
    
    void renameCollection(final MongoNamespace p0);
    
    void renameCollection(final MongoNamespace p0, final RenameCollectionOptions p1);
}
