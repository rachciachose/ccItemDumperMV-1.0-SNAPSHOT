// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.io.IOException;
import java.io.InputStream;
import org.bson.BSONDecoder;

public interface DBDecoder extends BSONDecoder
{
    DBCallback getDBCallback(final DBCollection p0);
    
    DBObject decode(final InputStream p0, final DBCollection p1) throws IOException;
    
    DBObject decode(final byte[] p0, final DBCollection p1);
}
