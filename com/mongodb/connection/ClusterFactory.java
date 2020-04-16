// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.event.ConnectionListener;
import com.mongodb.event.ConnectionPoolListener;
import com.mongodb.event.ClusterListener;
import com.mongodb.MongoCredential;
import java.util.List;

public interface ClusterFactory
{
    Cluster create(final ClusterSettings p0, final ServerSettings p1, final ConnectionPoolSettings p2, final StreamFactory p3, final StreamFactory p4, final List<MongoCredential> p5, final ClusterListener p6, final ConnectionPoolListener p7, final ConnectionListener p8);
}
