// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client;

import com.mongodb.client.model.CreateCollectionOptions;
import org.bson.conversions.Bson;
import org.bson.Document;
import com.mongodb.WriteConcern;
import com.mongodb.ReadPreference;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.annotations.ThreadSafe;

@ThreadSafe
public interface MongoDatabase
{
    String getName();
    
    CodecRegistry getCodecRegistry();
    
    ReadPreference getReadPreference();
    
    WriteConcern getWriteConcern();
    
    MongoDatabase withCodecRegistry(final CodecRegistry p0);
    
    MongoDatabase withReadPreference(final ReadPreference p0);
    
    MongoDatabase withWriteConcern(final WriteConcern p0);
    
    MongoCollection<Document> getCollection(final String p0);
    
     <TDocument> MongoCollection<TDocument> getCollection(final String p0, final Class<TDocument> p1);
    
    Document runCommand(final Bson p0);
    
    Document runCommand(final Bson p0, final ReadPreference p1);
    
     <TResult> TResult runCommand(final Bson p0, final Class<TResult> p1);
    
     <TResult> TResult runCommand(final Bson p0, final ReadPreference p1, final Class<TResult> p2);
    
    void drop();
    
    MongoIterable<String> listCollectionNames();
    
    ListCollectionsIterable<Document> listCollections();
    
     <TResult> ListCollectionsIterable<TResult> listCollections(final Class<TResult> p0);
    
    void createCollection(final String p0);
    
    void createCollection(final String p0, final CreateCollectionOptions p1);
}
