// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Loggers;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.mongodb.assertions.Assertions;
import java.util.concurrent.ConcurrentHashMap;
import com.mongodb.event.ClusterListener;
import com.mongodb.ServerAddress;
import java.util.concurrent.ConcurrentMap;
import org.bson.types.ObjectId;
import com.mongodb.diagnostics.logging.Logger;

final class MultiServerCluster extends BaseCluster
{
    private static final Logger LOGGER;
    private ClusterType clusterType;
    private String replicaSetName;
    private ObjectId maxElectionId;
    private final ConcurrentMap<ServerAddress, ServerTuple> addressToServerTupleMap;
    
    public MultiServerCluster(final ClusterId clusterId, final ClusterSettings settings, final ClusterableServerFactory serverFactory, final ClusterListener clusterListener) {
        super(clusterId, settings, serverFactory, clusterListener);
        this.addressToServerTupleMap = new ConcurrentHashMap<ServerAddress, ServerTuple>();
        Assertions.isTrue("connection mode is multiple", settings.getMode() == ClusterConnectionMode.MULTIPLE);
        this.clusterType = settings.getRequiredClusterType();
        this.replicaSetName = settings.getRequiredReplicaSetName();
        if (MultiServerCluster.LOGGER.isInfoEnabled()) {
            MultiServerCluster.LOGGER.info(String.format("Cluster created with settings %s", settings.getShortDescription()));
        }
        synchronized (this) {
            for (final ServerAddress serverAddress : settings.getHosts()) {
                this.addServer(serverAddress);
            }
            this.updateDescription();
        }
        this.fireChangeEvent();
    }
    
    @Override
    protected void connect() {
        for (final ServerTuple cur : this.addressToServerTupleMap.values()) {
            cur.server.connect();
        }
    }
    
    @Override
    public void close() {
        if (!this.isClosed()) {
            synchronized (this) {
                for (final ServerTuple serverTuple : this.addressToServerTupleMap.values()) {
                    serverTuple.server.close();
                }
            }
            super.close();
        }
    }
    
    @Override
    protected ClusterableServer getServer(final ServerAddress serverAddress) {
        Assertions.isTrue("is open", !this.isClosed());
        final ServerTuple serverTuple = this.addressToServerTupleMap.get(serverAddress);
        if (serverTuple == null) {
            return null;
        }
        return serverTuple.server;
    }
    
    private void onChange(final ChangeEvent<ServerDescription> event) {
        if (this.isClosed()) {
            return;
        }
        boolean shouldUpdateDescription = true;
        synchronized (this) {
            final ServerDescription newDescription = event.getNewValue();
            final ServerTuple serverTuple = this.addressToServerTupleMap.get(newDescription.getAddress());
            if (serverTuple == null) {
                return;
            }
            if (event.getNewValue().isOk()) {
                if (this.clusterType == ClusterType.UNKNOWN && newDescription.getType() != ServerType.REPLICA_SET_GHOST) {
                    this.clusterType = newDescription.getClusterType();
                    if (MultiServerCluster.LOGGER.isInfoEnabled()) {
                        MultiServerCluster.LOGGER.info(String.format("Discovered cluster type of %s", this.clusterType));
                    }
                }
                switch (this.clusterType) {
                    case REPLICA_SET: {
                        shouldUpdateDescription = this.handleReplicaSetMemberChanged(newDescription);
                        break;
                    }
                    case SHARDED: {
                        shouldUpdateDescription = this.handleShardRouterChanged(newDescription);
                        break;
                    }
                    case STANDALONE: {
                        shouldUpdateDescription = this.handleStandAloneChanged(newDescription);
                        break;
                    }
                }
            }
            if (shouldUpdateDescription) {
                serverTuple.description = newDescription;
                this.updateDescription();
            }
        }
        if (shouldUpdateDescription) {
            this.fireChangeEvent();
        }
    }
    
    private boolean handleReplicaSetMemberChanged(final ServerDescription newDescription) {
        if (!newDescription.isReplicaSetMember()) {
            MultiServerCluster.LOGGER.error(String.format("Expecting replica set member, but found a %s.  Removing %s from client view of cluster.", newDescription.getType(), newDescription.getAddress()));
            this.removeServer(newDescription.getAddress());
            return true;
        }
        if (newDescription.getType() == ServerType.REPLICA_SET_GHOST) {
            if (MultiServerCluster.LOGGER.isInfoEnabled()) {
                MultiServerCluster.LOGGER.info(String.format("Server %s does not appear to be a member of an initiated replica set.", newDescription.getAddress()));
            }
            return true;
        }
        if (this.replicaSetName == null) {
            this.replicaSetName = newDescription.getSetName();
        }
        if (!this.replicaSetName.equals(newDescription.getSetName())) {
            MultiServerCluster.LOGGER.error(String.format("Expecting replica set member from set '%s', but found one from set '%s'.  Removing %s from client view of cluster.", this.replicaSetName, newDescription.getSetName(), newDescription.getAddress()));
            this.removeServer(newDescription.getAddress());
            return true;
        }
        this.ensureServers(newDescription);
        if (newDescription.getCanonicalAddress() != null && !newDescription.getAddress().sameHost(newDescription.getCanonicalAddress())) {
            this.removeServer(newDescription.getAddress());
            return true;
        }
        if (newDescription.isPrimary()) {
            if (newDescription.getElectionId() != null) {
                if (this.maxElectionId != null && this.maxElectionId.compareTo(newDescription.getElectionId()) > 0) {
                    this.addressToServerTupleMap.get(newDescription.getAddress()).server.invalidate();
                    return false;
                }
                this.maxElectionId = newDescription.getElectionId();
            }
            if (this.isNotAlreadyPrimary(newDescription.getAddress())) {
                MultiServerCluster.LOGGER.info(String.format("Discovered replica set primary %s", newDescription.getAddress()));
            }
            this.invalidateOldPrimaries(newDescription.getAddress());
        }
        return true;
    }
    
    private boolean isNotAlreadyPrimary(final ServerAddress address) {
        final ServerTuple serverTuple = this.addressToServerTupleMap.get(address);
        return serverTuple == null || !serverTuple.description.isPrimary();
    }
    
    private boolean handleShardRouterChanged(final ServerDescription newDescription) {
        if (!newDescription.isShardRouter()) {
            MultiServerCluster.LOGGER.error(String.format("Expecting a %s, but found a %s.  Removing %s from client view of cluster.", ServerType.SHARD_ROUTER, newDescription.getType(), newDescription.getAddress()));
            this.removeServer(newDescription.getAddress());
        }
        return true;
    }
    
    private boolean handleStandAloneChanged(final ServerDescription newDescription) {
        if (this.getSettings().getHosts().size() > 1) {
            MultiServerCluster.LOGGER.error(String.format("Expecting a single %s, but found more than one.  Removing %s from client view of cluster.", ServerType.STANDALONE, newDescription.getAddress()));
            this.clusterType = ClusterType.UNKNOWN;
            this.removeServer(newDescription.getAddress());
        }
        return true;
    }
    
    private void addServer(final ServerAddress serverAddress) {
        if (!this.addressToServerTupleMap.containsKey(serverAddress)) {
            if (MultiServerCluster.LOGGER.isInfoEnabled()) {
                MultiServerCluster.LOGGER.info(String.format("Adding discovered server %s to client view of cluster", serverAddress));
            }
            final ClusterableServer server = this.createServer(serverAddress, new DefaultServerStateListener());
            this.addressToServerTupleMap.put(serverAddress, new ServerTuple(server, this.getConnectingServerDescription(serverAddress)));
        }
    }
    
    private void removeServer(final ServerAddress serverAddress) {
        final ServerTuple removed = this.addressToServerTupleMap.remove(serverAddress);
        if (removed != null) {
            removed.server.close();
        }
    }
    
    private void invalidateOldPrimaries(final ServerAddress newPrimary) {
        for (final ServerTuple serverTuple : this.addressToServerTupleMap.values()) {
            if (!serverTuple.description.getAddress().equals(newPrimary) && serverTuple.description.isPrimary()) {
                if (MultiServerCluster.LOGGER.isInfoEnabled()) {
                    MultiServerCluster.LOGGER.info(String.format("Rediscovering type of existing primary %s", serverTuple.description.getAddress()));
                }
                serverTuple.server.invalidate();
            }
        }
    }
    
    private ServerDescription getConnectingServerDescription(final ServerAddress serverAddress) {
        return ServerDescription.builder().state(ServerConnectionState.CONNECTING).address(serverAddress).build();
    }
    
    private void updateDescription() {
        final List<ServerDescription> newServerDescriptionList = this.getNewServerDescriptionList();
        this.updateDescription(new ClusterDescription(ClusterConnectionMode.MULTIPLE, this.clusterType, newServerDescriptionList));
    }
    
    private List<ServerDescription> getNewServerDescriptionList() {
        final List<ServerDescription> serverDescriptions = new ArrayList<ServerDescription>();
        for (final ServerTuple cur : this.addressToServerTupleMap.values()) {
            serverDescriptions.add(cur.description);
        }
        return serverDescriptions;
    }
    
    private void ensureServers(final ServerDescription description) {
        if (description.isPrimary() || !this.hasPrimary()) {
            this.addNewHosts(description.getHosts());
            this.addNewHosts(description.getPassives());
            this.addNewHosts(description.getArbiters());
        }
        if (description.isPrimary()) {
            this.removeExtraHosts(description);
        }
    }
    
    private boolean hasPrimary() {
        for (final ServerTuple serverTuple : this.addressToServerTupleMap.values()) {
            if (serverTuple.description.isPrimary()) {
                return true;
            }
        }
        return false;
    }
    
    private void addNewHosts(final Set<String> hosts) {
        for (final String cur : hosts) {
            this.addServer(new ServerAddress(cur));
        }
    }
    
    private void removeExtraHosts(final ServerDescription serverDescription) {
        final Set<ServerAddress> allServerAddresses = this.getAllServerAddresses(serverDescription);
        for (final ServerTuple cur : this.addressToServerTupleMap.values()) {
            if (!allServerAddresses.contains(cur.description.getAddress())) {
                if (MultiServerCluster.LOGGER.isInfoEnabled()) {
                    MultiServerCluster.LOGGER.info(String.format("Server %s is no longer a member of the replica set.  Removing from client view of cluster.", cur.description.getAddress()));
                }
                this.removeServer(cur.description.getAddress());
            }
        }
    }
    
    private Set<ServerAddress> getAllServerAddresses(final ServerDescription serverDescription) {
        final Set<ServerAddress> retVal = new HashSet<ServerAddress>();
        this.addHostsToSet(serverDescription.getHosts(), retVal);
        this.addHostsToSet(serverDescription.getPassives(), retVal);
        this.addHostsToSet(serverDescription.getArbiters(), retVal);
        return retVal;
    }
    
    private void addHostsToSet(final Set<String> hosts, final Set<ServerAddress> retVal) {
        for (final String host : hosts) {
            retVal.add(new ServerAddress(host));
        }
    }
    
    static {
        LOGGER = Loggers.getLogger("cluster");
    }
    
    private static final class ServerTuple
    {
        private final ClusterableServer server;
        private ServerDescription description;
        
        private ServerTuple(final ClusterableServer server, final ServerDescription description) {
            this.server = server;
            this.description = description;
        }
    }
    
    private final class DefaultServerStateListener implements ChangeListener<ServerDescription>
    {
        @Override
        public void stateChanged(final ChangeEvent<ServerDescription> event) {
            MultiServerCluster.this.onChange(event);
        }
    }
}
