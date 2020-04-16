// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.io.Closeable;
import java.util.Iterator;

public interface Cursor extends Iterator<DBObject>, Closeable
{
    long getCursorId();
    
    ServerAddress getServerAddress();
    
    void close();
}
