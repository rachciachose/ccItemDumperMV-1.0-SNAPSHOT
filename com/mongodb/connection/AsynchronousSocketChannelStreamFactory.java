// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.ServerAddress;
import com.mongodb.assertions.Assertions;
import com.mongodb.internal.connection.PowerOfTwoBufferPool;

public class AsynchronousSocketChannelStreamFactory implements StreamFactory
{
    private final SocketSettings settings;
    private final BufferProvider bufferProvider;
    
    public AsynchronousSocketChannelStreamFactory(final SocketSettings settings, final SslSettings sslSettings) {
        this.bufferProvider = new PowerOfTwoBufferPool();
        if (sslSettings.isEnabled()) {
            throw new UnsupportedOperationException("No SSL support in java.nio.channels.AsynchronousSocketChannel. For SSL support use com.mongodb.connection.netty.NettyStreamFactoryFactory");
        }
        this.settings = Assertions.notNull("settings", settings);
    }
    
    @Override
    public Stream create(final ServerAddress serverAddress) {
        return new AsynchronousSocketChannelStream(serverAddress, this.settings, this.bufferProvider);
    }
}
