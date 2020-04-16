// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.List;

interface DBObjectFactory
{
    DBObject getInstance();
    
    DBObject getInstance(final List<String> p0);
}
