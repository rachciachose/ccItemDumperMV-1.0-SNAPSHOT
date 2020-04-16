// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.ServerAddress;
import org.bson.ByteBuf;
import java.util.List;
import java.io.IOException;

public interface Stream extends BufferProvider
{
    void open() throws IOException;
    
    void openAsync(final AsyncCompletionHandler<Void> p0);
    
    void write(final List<ByteBuf> p0) throws IOException;
    
    ByteBuf read(final int p0) throws IOException;
    
    void writeAsync(final List<ByteBuf> p0, final AsyncCompletionHandler<Void> p1);
    
    void readAsync(final int p0, final AsyncCompletionHandler<ByteBuf> p1);
    
    ServerAddress getAddress();
    
    void close();
    
    boolean isClosed();
}
