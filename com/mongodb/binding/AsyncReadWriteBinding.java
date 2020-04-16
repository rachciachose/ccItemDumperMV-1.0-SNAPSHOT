// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

public interface AsyncReadWriteBinding extends AsyncReadBinding, AsyncWriteBinding, ReferenceCounted
{
    AsyncReadWriteBinding retain();
}
