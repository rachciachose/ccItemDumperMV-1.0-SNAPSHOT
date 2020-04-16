// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

public interface WriteBinding extends ReferenceCounted
{
    ConnectionSource getWriteConnectionSource();
    
    WriteBinding retain();
}
