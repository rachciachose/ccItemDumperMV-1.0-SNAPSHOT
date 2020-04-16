// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.gridfs;

import com.mongodb.client.MongoDatabase;

public final class GridFSBuckets
{
    public static GridFSBucket create(final MongoDatabase database) {
        return new GridFSBucketImpl(database);
    }
    
    public static GridFSBucket create(final MongoDatabase database, final String bucketName) {
        return new GridFSBucketImpl(database, bucketName);
    }
}
