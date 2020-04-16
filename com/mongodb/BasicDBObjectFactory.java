// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.List;

class BasicDBObjectFactory implements DBObjectFactory
{
    @Override
    public DBObject getInstance() {
        return new BasicDBObject();
    }
    
    @Override
    public DBObject getInstance(final List<String> path) {
        return new BasicDBObject();
    }
}
