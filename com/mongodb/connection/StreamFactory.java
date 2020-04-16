// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.ServerAddress;

public interface StreamFactory
{
    Stream create(final ServerAddress p0);
}
