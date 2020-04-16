// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.gridfs;

import org.bson.conversions.Bson;
import com.mongodb.client.gridfs.model.GridFSDownloadByNameOptions;
import org.bson.BsonValue;
import java.io.OutputStream;
import org.bson.types.ObjectId;
import java.io.InputStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.annotations.ThreadSafe;

@ThreadSafe
public interface GridFSBucket
{
    String getBucketName();
    
    int getChunkSizeBytes();
    
    WriteConcern getWriteConcern();
    
    ReadPreference getReadPreference();
    
    GridFSBucket withChunkSizeBytes(final int p0);
    
    GridFSBucket withReadPreference(final ReadPreference p0);
    
    GridFSBucket withWriteConcern(final WriteConcern p0);
    
    GridFSUploadStream openUploadStream(final String p0);
    
    GridFSUploadStream openUploadStream(final String p0, final GridFSUploadOptions p1);
    
    ObjectId uploadFromStream(final String p0, final InputStream p1);
    
    ObjectId uploadFromStream(final String p0, final InputStream p1, final GridFSUploadOptions p2);
    
    GridFSDownloadStream openDownloadStream(final ObjectId p0);
    
    void downloadToStream(final ObjectId p0, final OutputStream p1);
    
    @Deprecated
    GridFSDownloadStream openDownloadStream(final BsonValue p0);
    
    @Deprecated
    void downloadToStream(final BsonValue p0, final OutputStream p1);
    
    GridFSDownloadStream openDownloadStreamByName(final String p0);
    
    GridFSDownloadStream openDownloadStreamByName(final String p0, final GridFSDownloadByNameOptions p1);
    
    void downloadToStreamByName(final String p0, final OutputStream p1);
    
    void downloadToStreamByName(final String p0, final OutputStream p1, final GridFSDownloadByNameOptions p2);
    
    GridFSFindIterable find();
    
    GridFSFindIterable find(final Bson p0);
    
    void delete(final ObjectId p0);
    
    void rename(final ObjectId p0, final String p1);
    
    void drop();
}
