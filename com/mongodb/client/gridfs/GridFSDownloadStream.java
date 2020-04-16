// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.gridfs;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.annotations.NotThreadSafe;
import java.io.InputStream;

@NotThreadSafe
public abstract class GridFSDownloadStream extends InputStream
{
    public abstract GridFSFile getGridFSFile();
    
    public abstract GridFSDownloadStream batchSize(final int p0);
    
    @Override
    public abstract int read();
    
    @Override
    public abstract int read(final byte[] p0);
    
    @Override
    public abstract int read(final byte[] p0, final int p1, final int p2);
    
    @Override
    public abstract long skip(final long p0);
    
    @Override
    public abstract int available();
    
    public abstract void mark();
    
    @Override
    public abstract void reset();
    
    @Override
    public abstract void close();
}
