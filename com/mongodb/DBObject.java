// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BSONObject;

public interface DBObject extends BSONObject
{
    void markAsPartialObject();
    
    boolean isPartialObject();
}
