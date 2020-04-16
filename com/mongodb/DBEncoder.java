// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BSONObject;
import org.bson.io.OutputBuffer;

public interface DBEncoder
{
    int writeObject(final OutputBuffer p0, final BSONObject p1);
}
