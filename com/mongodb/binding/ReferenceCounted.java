// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

public interface ReferenceCounted
{
    int getCount();
    
    ReferenceCounted retain();
    
    void release();
}
