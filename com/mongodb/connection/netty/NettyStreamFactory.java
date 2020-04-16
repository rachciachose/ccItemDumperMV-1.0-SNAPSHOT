// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection.netty;

import com.mongodb.connection.Stream;
import com.mongodb.ServerAddress;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import com.mongodb.assertions.Assertions;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import com.mongodb.connection.SslSettings;
import com.mongodb.connection.SocketSettings;
import com.mongodb.connection.StreamFactory;

public class NettyStreamFactory implements StreamFactory
{
    private final SocketSettings settings;
    private final SslSettings sslSettings;
    private final EventLoopGroup eventLoopGroup;
    private final ByteBufAllocator allocator;
    
    public NettyStreamFactory(final SocketSettings settings, final SslSettings sslSettings, final EventLoopGroup eventLoopGroup, final ByteBufAllocator allocator) {
        this.settings = Assertions.notNull("settings", settings);
        this.sslSettings = Assertions.notNull("sslSettings", sslSettings);
        this.eventLoopGroup = Assertions.notNull("eventLoopGroup", eventLoopGroup);
        this.allocator = Assertions.notNull("allocator", allocator);
    }
    
    public NettyStreamFactory(final SocketSettings settings, final SslSettings sslSettings) {
        this(settings, sslSettings, (EventLoopGroup)new NioEventLoopGroup(), (ByteBufAllocator)PooledByteBufAllocator.DEFAULT);
    }
    
    @Override
    public Stream create(final ServerAddress serverAddress) {
        return new NettyStream(serverAddress, this.settings, this.sslSettings, this.eventLoopGroup, this.allocator);
    }
}
