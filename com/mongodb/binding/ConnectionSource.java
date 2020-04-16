// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.binding;

import com.mongodb.connection.Connection;
import com.mongodb.connection.ServerDescription;

public interface ConnectionSource extends ReferenceCounted
{
    ServerDescription getServerDescription();
    
    Connection getConnection();
    
    ConnectionSource retain();
}
