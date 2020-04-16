// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.util;

public interface ObjectSerializer
{
    void serialize(final Object p0, final StringBuilder p1);
    
    String serialize(final Object p0);
}
