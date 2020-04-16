// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection.netty;

import org.bson.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.ByteBufAllocator;
import com.mongodb.connection.BufferProvider;

final class NettyBufferProvider implements BufferProvider
{
    private final ByteBufAllocator allocator;
    
    public NettyBufferProvider() {
        this.allocator = (ByteBufAllocator)PooledByteBufAllocator.DEFAULT;
    }
    
    public NettyBufferProvider(final ByteBufAllocator allocator) {
        this.allocator = allocator;
    }
    
    @Override
    public ByteBuf getBuffer(final int size) {
        return new NettyByteBuf(this.allocator.directBuffer(size, size));
    }
}
