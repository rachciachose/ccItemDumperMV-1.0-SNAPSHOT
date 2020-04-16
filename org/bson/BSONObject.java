// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

import java.util.Set;
import java.util.Map;

public interface BSONObject
{
    Object put(final String p0, final Object p1);
    
    void putAll(final BSONObject p0);
    
    void putAll(final Map p0);
    
    Object get(final String p0);
    
    Map toMap();
    
    Object removeField(final String p0);
    
    @Deprecated
    boolean containsKey(final String p0);
    
    boolean containsField(final String p0);
    
    Set<String> keySet();
}
