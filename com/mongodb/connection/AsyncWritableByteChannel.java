// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.nio.ByteBuffer;

interface AsyncWritableByteChannel
{
    void write(final ByteBuffer p0, final AsyncCompletionHandler<Void> p1);
}
