// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.ByteBuf;
import java.util.List;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.annotations.ThreadSafe;

@ThreadSafe
interface InternalConnection extends BufferProvider
{
    ConnectionDescription getDescription();
    
    void open();
    
    void openAsync(final SingleResultCallback<Void> p0);
    
    void close();
    
    boolean opened();
    
    boolean isClosed();
    
    void sendMessage(final List<ByteBuf> p0, final int p1);
    
    ResponseBuffers receiveMessage(final int p0);
    
    void sendMessageAsync(final List<ByteBuf> p0, final int p1, final SingleResultCallback<Void> p2);
    
    void receiveMessageAsync(final int p0, final SingleResultCallback<ResponseBuffers> p1);
}
