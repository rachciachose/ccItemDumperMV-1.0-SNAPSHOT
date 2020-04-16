// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection.netty;

import com.mongodb.MongoInterruptedException;
import com.mongodb.MongoInternalException;
import com.mongodb.MongoException;
import java.util.concurrent.CountDownLatch;
import com.mongodb.MongoSocketReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutor;
import io.netty.channel.ChannelHandlerContext;
import java.util.Iterator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import java.util.List;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import com.mongodb.MongoSocketOpenException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import javax.net.ssl.SSLEngine;
import io.netty.channel.ChannelHandler;
import io.netty.handler.ssl.SslHandler;
import com.mongodb.internal.connection.SslHelper;
import javax.net.ssl.SSLContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import java.util.concurrent.TimeUnit;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.bootstrap.Bootstrap;
import java.io.IOException;
import com.mongodb.connection.AsyncCompletionHandler;
import io.netty.buffer.ByteBuf;
import java.util.LinkedList;
import io.netty.channel.Channel;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import com.mongodb.connection.SslSettings;
import com.mongodb.connection.SocketSettings;
import com.mongodb.ServerAddress;
import com.mongodb.connection.Stream;

final class NettyStream implements Stream
{
    private static final String READ_HANDLER_NAME = "ReadTimeoutHandler";
    private final ServerAddress address;
    private final SocketSettings settings;
    private final SslSettings sslSettings;
    private final EventLoopGroup workerGroup;
    private final ByteBufAllocator allocator;
    private volatile boolean isClosed;
    private volatile Channel channel;
    private final LinkedList<ByteBuf> pendingInboundBuffers;
    private volatile PendingReader pendingReader;
    private volatile Throwable pendingException;
    
    public NettyStream(final ServerAddress address, final SocketSettings settings, final SslSettings sslSettings, final EventLoopGroup workerGroup, final ByteBufAllocator allocator) {
        this.pendingInboundBuffers = new LinkedList<ByteBuf>();
        this.address = address;
        this.settings = settings;
        this.sslSettings = sslSettings;
        this.workerGroup = workerGroup;
        this.allocator = allocator;
    }
    
    @Override
    public org.bson.ByteBuf getBuffer(final int size) {
        return new NettyByteBuf(this.allocator.buffer(size, size));
    }
    
    @Override
    public void open() throws IOException {
        final FutureAsyncCompletionHandler<Void> handler = new FutureAsyncCompletionHandler<Void>();
        this.openAsync(handler);
        handler.get();
    }
    
    @Override
    public void openAsync(final AsyncCompletionHandler<Void> handler) {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(this.workerGroup);
        bootstrap.channel((Class)NioSocketChannel.class);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (Object)this.settings.getConnectTimeout(TimeUnit.MILLISECONDS));
        bootstrap.option(ChannelOption.TCP_NODELAY, (Object)true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, (Object)this.settings.isKeepAlive());
        if (this.settings.getReceiveBufferSize() > 0) {
            bootstrap.option(ChannelOption.SO_RCVBUF, (Object)this.settings.getReceiveBufferSize());
        }
        if (this.settings.getSendBufferSize() > 0) {
            bootstrap.option(ChannelOption.SO_SNDBUF, (Object)this.settings.getSendBufferSize());
        }
        bootstrap.option(ChannelOption.ALLOCATOR, (Object)this.allocator);
        bootstrap.handler((ChannelHandler)new ChannelInitializer<SocketChannel>() {
            public void initChannel(final SocketChannel ch) throws Exception {
                if (NettyStream.this.sslSettings.isEnabled()) {
                    final SSLEngine engine = SSLContext.getDefault().createSSLEngine(NettyStream.this.address.getHost(), NettyStream.this.address.getPort());
                    engine.setUseClientMode(true);
                    if (!NettyStream.this.sslSettings.isInvalidHostNameAllowed()) {
                        engine.setSSLParameters(SslHelper.enableHostNameVerification(engine.getSSLParameters()));
                    }
                    ch.pipeline().addFirst("ssl", (ChannelHandler)new SslHandler(engine, false));
                }
                final int readTimeout = NettyStream.this.settings.getReadTimeout(TimeUnit.MILLISECONDS);
                if (readTimeout > 0) {
                    ch.pipeline().addLast("ReadTimeoutHandler", (ChannelHandler)new ReadTimeoutHandler(readTimeout));
                }
                ch.pipeline().addLast(new ChannelHandler[] { new InboundBufferHandler() });
            }
        });
        final ChannelFuture channelFuture = bootstrap.connect(this.address.getHost(), this.address.getPort());
        channelFuture.addListener((GenericFutureListener)new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    NettyStream.this.channel = channelFuture.channel();
                    handler.completed(null);
                }
                else {
                    handler.failed(new MongoSocketOpenException("Exception opening socket", NettyStream.this.getAddress(), future.cause()));
                }
            }
        });
    }
    
    @Override
    public void write(final List<org.bson.ByteBuf> buffers) throws IOException {
        final FutureAsyncCompletionHandler<Void> future = new FutureAsyncCompletionHandler<Void>();
        this.writeAsync(buffers, future);
        future.get();
    }
    
    @Override
    public org.bson.ByteBuf read(final int numBytes) throws IOException {
        final FutureAsyncCompletionHandler<org.bson.ByteBuf> future = new FutureAsyncCompletionHandler<org.bson.ByteBuf>();
        this.readAsync(numBytes, future);
        return future.get();
    }
    
    @Override
    public void writeAsync(final List<org.bson.ByteBuf> buffers, final AsyncCompletionHandler<Void> handler) {
        final CompositeByteBuf composite = PooledByteBufAllocator.DEFAULT.compositeBuffer();
        for (final org.bson.ByteBuf cur : buffers) {
            final ByteBuf byteBuf = ((NettyByteBuf)cur).asByteBuf();
            composite.addComponent(byteBuf.retain());
            composite.writerIndex(composite.writerIndex() + byteBuf.writerIndex());
        }
        this.channel.writeAndFlush((Object)composite).addListener((GenericFutureListener)new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    handler.failed(future.cause());
                }
                else {
                    handler.completed(null);
                }
            }
        });
    }
    
    @Override
    public void readAsync(final int numBytes, final AsyncCompletionHandler<org.bson.ByteBuf> handler) {
        this.scheduleReadTimeout();
        org.bson.ByteBuf buffer = null;
        Throwable exceptionResult = null;
        synchronized (this) {
            exceptionResult = this.pendingException;
            if (exceptionResult == null) {
                if (!this.hasBytesAvailable(numBytes)) {
                    this.pendingReader = new PendingReader(numBytes, (AsyncCompletionHandler)handler);
                }
                else {
                    final CompositeByteBuf composite = this.allocator.compositeBuffer(this.pendingInboundBuffers.size());
                    int bytesNeeded = numBytes;
                    final Iterator<ByteBuf> iter = this.pendingInboundBuffers.iterator();
                    while (iter.hasNext()) {
                        final ByteBuf next = iter.next();
                        final int bytesNeededFromCurrentBuffer = Math.min(next.readableBytes(), bytesNeeded);
                        if (bytesNeededFromCurrentBuffer == next.readableBytes()) {
                            composite.addComponent(next);
                            iter.remove();
                        }
                        else {
                            next.retain();
                            composite.addComponent(next.readSlice(bytesNeededFromCurrentBuffer));
                        }
                        composite.writerIndex(composite.writerIndex() + bytesNeededFromCurrentBuffer);
                        bytesNeeded -= bytesNeededFromCurrentBuffer;
                        if (bytesNeeded == 0) {
                            break;
                        }
                    }
                    buffer = new NettyByteBuf((ByteBuf)composite).flip();
                }
            }
        }
        if (exceptionResult != null) {
            this.disableReadTimeout();
            handler.failed(exceptionResult);
        }
        if (buffer != null) {
            this.disableReadTimeout();
            handler.completed(buffer);
        }
    }
    
    private boolean hasBytesAvailable(final int numBytes) {
        int bytesAvailable = 0;
        for (final ByteBuf cur : this.pendingInboundBuffers) {
            bytesAvailable += cur.readableBytes();
            if (bytesAvailable >= numBytes) {
                return true;
            }
        }
        return false;
    }
    
    private void handleReadResponse(final ByteBuf buffer, final Throwable t) {
        PendingReader localPendingReader = null;
        synchronized (this) {
            if (buffer != null) {
                this.pendingInboundBuffers.add(buffer.retain());
            }
            else {
                this.pendingException = t;
            }
            if (this.pendingReader != null) {
                localPendingReader = this.pendingReader;
                this.pendingReader = null;
            }
        }
        if (localPendingReader != null) {
            this.readAsync(localPendingReader.numBytes, localPendingReader.handler);
        }
    }
    
    @Override
    public ServerAddress getAddress() {
        return this.address;
    }
    
    @Override
    public void close() {
        this.isClosed = true;
        if (this.channel != null) {
            this.channel.close();
            this.channel = null;
        }
        final Iterator<ByteBuf> iterator = this.pendingInboundBuffers.iterator();
        while (iterator.hasNext()) {
            final ByteBuf nextByteBuf = iterator.next();
            iterator.remove();
            nextByteBuf.release();
        }
    }
    
    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
    
    private void scheduleReadTimeout() {
        this.adjustTimeout(false);
    }
    
    private void disableReadTimeout() {
        this.adjustTimeout(true);
    }
    
    private void adjustTimeout(final boolean disable) {
        final ChannelHandler timeoutHandler = this.channel.pipeline().get("ReadTimeoutHandler");
        if (timeoutHandler != null) {
            final ReadTimeoutHandler readTimeoutHandler = (ReadTimeoutHandler)timeoutHandler;
            final ChannelHandlerContext handlerContext = this.channel.pipeline().context(timeoutHandler);
            final EventExecutor executor = handlerContext.executor();
            if (disable) {
                if (executor.inEventLoop()) {
                    readTimeoutHandler.removeTimeout(handlerContext);
                }
                else {
                    executor.submit((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            readTimeoutHandler.removeTimeout(handlerContext);
                        }
                    });
                }
            }
            else if (executor.inEventLoop()) {
                readTimeoutHandler.scheduleTimeout(handlerContext);
            }
            else {
                executor.submit((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        readTimeoutHandler.scheduleTimeout(handlerContext);
                    }
                });
            }
        }
    }
    
    private class InboundBufferHandler extends SimpleChannelInboundHandler<ByteBuf>
    {
        protected void channelRead0(final ChannelHandlerContext ctx, final ByteBuf buffer) throws Exception {
            NettyStream.this.handleReadResponse(buffer, null);
        }
        
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable t) {
            if (t instanceof ReadTimeoutException) {
                NettyStream.this.handleReadResponse(null, new MongoSocketReadTimeoutException("Timeout while receiving message", NettyStream.this.address, t));
            }
            else {
                NettyStream.this.handleReadResponse(null, t);
            }
            ctx.close();
        }
    }
    
    private static final class PendingReader
    {
        private final int numBytes;
        private final AsyncCompletionHandler<org.bson.ByteBuf> handler;
        
        private PendingReader(final int numBytes, final AsyncCompletionHandler<org.bson.ByteBuf> handler) {
            this.numBytes = numBytes;
            this.handler = handler;
        }
    }
    
    private static final class FutureAsyncCompletionHandler<T> implements AsyncCompletionHandler<T>
    {
        private final CountDownLatch latch;
        private volatile T t;
        private volatile Throwable throwable;
        
        public FutureAsyncCompletionHandler() {
            this.latch = new CountDownLatch(1);
        }
        
        @Override
        public void completed(final T t) {
            this.t = t;
            this.latch.countDown();
        }
        
        @Override
        public void failed(final Throwable t) {
            this.throwable = t;
            this.latch.countDown();
        }
        
        public T get() throws IOException {
            try {
                this.latch.await();
                if (this.throwable == null) {
                    return this.t;
                }
                if (this.throwable instanceof IOException) {
                    throw (IOException)this.throwable;
                }
                if (this.throwable instanceof MongoException) {
                    throw (MongoException)this.throwable;
                }
                throw new MongoInternalException("Exception thrown from Netty Stream", this.throwable);
            }
            catch (InterruptedException e) {
                throw new MongoInterruptedException("Interrupted", e);
            }
        }
    }
}
