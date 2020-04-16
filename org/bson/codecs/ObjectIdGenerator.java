// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.codecs;

import org.bson.types.ObjectId;

public class ObjectIdGenerator implements IdGenerator
{
    @Override
    public Object generate() {
        return new ObjectId();
    }
}
