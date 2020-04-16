// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.ByteBuf;

public interface BufferProvider
{
    ByteBuf getBuffer(final int p0);
}
