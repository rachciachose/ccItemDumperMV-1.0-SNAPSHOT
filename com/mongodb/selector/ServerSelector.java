// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.selector;

import com.mongodb.connection.ServerDescription;
import java.util.List;
import com.mongodb.connection.ClusterDescription;
import com.mongodb.annotations.ThreadSafe;

@ThreadSafe
public interface ServerSelector
{
    List<ServerDescription> select(final ClusterDescription p0);
}
