// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection.netty;

import io.netty.handler.timeout.ReadTimeoutException;
import java.util.concurrent.TimeUnit;
import io.netty.channel.ChannelHandlerContext;
import com.mongodb.assertions.Assertions;
import java.util.concurrent.ScheduledFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;

final class ReadTimeoutHandler extends ChannelInboundHandlerAdapter
{
    private final long readTimeout;
    private volatile ScheduledFuture<?> timeout;
    
    public ReadTimeoutHandler(final long readTimeout) {
        Assertions.isTrueArgument("readTimeout must be greater than zero.", readTimeout > 0L);
        this.readTimeout = readTimeout;
    }
    
    void scheduleTimeout(final ChannelHandlerContext ctx) {
        Assertions.isTrue("Handler called from the eventLoop", ctx.channel().eventLoop().inEventLoop());
        if (this.timeout == null) {
            this.timeout = (ScheduledFuture<?>)ctx.executor().schedule((Runnable)new ReadTimeoutTask(ctx), this.readTimeout, TimeUnit.MILLISECONDS);
        }
    }
    
    void removeTimeout(final ChannelHandlerContext ctx) {
        Assertions.isTrue("Handler called from the eventLoop", ctx.channel().eventLoop().inEventLoop());
        if (this.timeout != null) {
            this.timeout.cancel(false);
            this.timeout = null;
        }
    }
    
    private static final class ReadTimeoutTask implements Runnable
    {
        private final ChannelHandlerContext ctx;
        
        ReadTimeoutTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }
        
        @Override
        public void run() {
            if (this.ctx.channel().isOpen()) {
                try {
                    this.ctx.fireExceptionCaught((Throwable)ReadTimeoutException.INSTANCE);
                    this.ctx.close();
                }
                catch (Throwable t) {
                    this.ctx.fireExceptionCaught(t);
                }
            }
        }
    }
}
