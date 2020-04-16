// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

import com.mongodb.ReadPreference;

public interface ReadBinding extends ReferenceCounted
{
    ReadPreference getReadPreference();
    
    ConnectionSource getReadConnectionSource();
    
    ReadBinding retain();
}
