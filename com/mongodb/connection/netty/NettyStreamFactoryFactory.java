// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection.netty;

import com.mongodb.connection.StreamFactory;
import com.mongodb.connection.SslSettings;
import com.mongodb.connection.SocketSettings;
import io.netty.channel.nio.NioEventLoopGroup;
import com.mongodb.assertions.Assertions;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import com.mongodb.connection.StreamFactoryFactory;

public class NettyStreamFactoryFactory implements StreamFactoryFactory
{
    private final EventLoopGroup eventLoopGroup;
    private final ByteBufAllocator allocator;
    
    public NettyStreamFactoryFactory(final EventLoopGroup eventLoopGroup, final ByteBufAllocator allocator) {
        this.eventLoopGroup = Assertions.notNull("eventLoopGroup", eventLoopGroup);
        this.allocator = Assertions.notNull("allocator", allocator);
    }
    
    public NettyStreamFactoryFactory() {
        this((EventLoopGroup)new NioEventLoopGroup(), ByteBufAllocator.DEFAULT);
    }
    
    @Override
    public StreamFactory create(final SocketSettings socketSettings, final SslSettings sslSettings) {
        return new NettyStreamFactory(socketSettings, sslSettings, this.eventLoopGroup, this.allocator);
    }
}
