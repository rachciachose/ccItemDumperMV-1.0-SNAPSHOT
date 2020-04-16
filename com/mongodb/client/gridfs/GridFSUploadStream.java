// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.gridfs;

import org.bson.types.ObjectId;
import com.mongodb.annotations.NotThreadSafe;
import java.io.OutputStream;

@NotThreadSafe
public abstract class GridFSUploadStream extends OutputStream
{
    public abstract ObjectId getFileId();
    
    public abstract void abort();
    
    @Override
    public abstract void write(final int p0);
    
    @Override
    public abstract void write(final byte[] p0);
    
    @Override
    public abstract void write(final byte[] p0, final int p1, final int p2);
    
    @Override
    public void flush() {
    }
    
    @Override
    public abstract void close();
}
