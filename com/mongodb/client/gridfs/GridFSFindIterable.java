// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.gridfs;

import java.util.concurrent.TimeUnit;
import org.bson.conversions.Bson;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.MongoIterable;

public interface GridFSFindIterable extends MongoIterable<GridFSFile>
{
    GridFSFindIterable filter(final Bson p0);
    
    GridFSFindIterable limit(final int p0);
    
    GridFSFindIterable skip(final int p0);
    
    GridFSFindIterable sort(final Bson p0);
    
    GridFSFindIterable noCursorTimeout(final boolean p0);
    
    GridFSFindIterable maxTime(final long p0, final TimeUnit p1);
    
    GridFSFindIterable batchSize(final int p0);
}
