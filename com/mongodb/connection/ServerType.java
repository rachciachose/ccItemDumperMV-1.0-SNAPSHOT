// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

public enum ServerType
{
    STANDALONE {
        @Override
        public ClusterType getClusterType() {
            return ClusterType.STANDALONE;
        }
    }, 
    REPLICA_SET_PRIMARY {
        @Override
        public ClusterType getClusterType() {
            return ClusterType.REPLICA_SET;
        }
    }, 
    REPLICA_SET_SECONDARY {
        @Override
        public ClusterType getClusterType() {
            return ClusterType.REPLICA_SET;
        }
    }, 
    REPLICA_SET_ARBITER {
        @Override
        public ClusterType getClusterType() {
            return ClusterType.REPLICA_SET;
        }
    }, 
    REPLICA_SET_OTHER {
        @Override
        public ClusterType getClusterType() {
            return ClusterType.REPLICA_SET;
        }
    }, 
    REPLICA_SET_GHOST {
        @Override
        public ClusterType getClusterType() {
            return ClusterType.REPLICA_SET;
        }
    }, 
    SHARD_ROUTER {
        @Override
        public ClusterType getClusterType() {
            return ClusterType.SHARDED;
        }
    }, 
    UNKNOWN {
        @Override
        public ClusterType getClusterType() {
            return ClusterType.UNKNOWN;
        }
    };
    
    public abstract ClusterType getClusterType();
}
