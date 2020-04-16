// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

public interface ReadWriteBinding extends ReadBinding, WriteBinding, ReferenceCounted
{
    ReadWriteBinding retain();
}
