// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.selector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ServerDescription;
import java.util.List;
import com.mongodb.connection.ClusterDescription;
import java.util.concurrent.TimeUnit;

public class LatencyMinimizingServerSelector implements ServerSelector
{
    private final long acceptableLatencyDifferenceNanos;
    
    public LatencyMinimizingServerSelector(final long acceptableLatencyDifference, final TimeUnit timeUnit) {
        this.acceptableLatencyDifferenceNanos = TimeUnit.NANOSECONDS.convert(acceptableLatencyDifference, timeUnit);
    }
    
    public long getAcceptableLatencyDifference(final TimeUnit timeUnit) {
        return timeUnit.convert(this.acceptableLatencyDifferenceNanos, TimeUnit.NANOSECONDS);
    }
    
    @Override
    public List<ServerDescription> select(final ClusterDescription clusterDescription) {
        if (clusterDescription.getConnectionMode() != ClusterConnectionMode.MULTIPLE) {
            return clusterDescription.getAny();
        }
        return this.getServersWithAcceptableLatencyDifference(clusterDescription.getAny(), this.getFastestRoundTripTimeNanos(clusterDescription.getAll()));
    }
    
    @Override
    public String toString() {
        return "LatencyMinimizingServerSelector{acceptableLatencyDifference=" + TimeUnit.MILLISECONDS.convert(this.acceptableLatencyDifferenceNanos, TimeUnit.NANOSECONDS) + " ms" + '}';
    }
    
    private long getFastestRoundTripTimeNanos(final Set<ServerDescription> members) {
        long fastestRoundTripTime = Long.MAX_VALUE;
        for (final ServerDescription cur : members) {
            if (!cur.isOk()) {
                continue;
            }
            if (cur.getRoundTripTimeNanos() >= fastestRoundTripTime) {
                continue;
            }
            fastestRoundTripTime = cur.getRoundTripTimeNanos();
        }
        return fastestRoundTripTime;
    }
    
    private List<ServerDescription> getServersWithAcceptableLatencyDifference(final List<ServerDescription> servers, final long bestPingTime) {
        final List<ServerDescription> goodSecondaries = new ArrayList<ServerDescription>(servers.size());
        for (final ServerDescription cur : servers) {
            if (!cur.isOk()) {
                continue;
            }
            if (cur.getRoundTripTimeNanos() - this.acceptableLatencyDifferenceNanos > bestPingTime) {
                continue;
            }
            goodSecondaries.add(cur);
        }
        return goodSecondaries;
    }
}
