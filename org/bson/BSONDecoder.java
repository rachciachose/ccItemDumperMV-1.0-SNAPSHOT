// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

import java.io.IOException;
import java.io.InputStream;

public interface BSONDecoder
{
    BSONObject readObject(final byte[] p0);
    
    BSONObject readObject(final InputStream p0) throws IOException;
    
    int decode(final byte[] p0, final BSONCallback p1);
    
    int decode(final InputStream p0, final BSONCallback p1) throws IOException;
}
