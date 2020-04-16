// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.concurrent.atomic.AtomicInteger;
import com.mongodb.connection.Connection;
import com.mongodb.binding.ConnectionSource;
import com.mongodb.binding.SingleServerBinding;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import com.mongodb.operation.WriteOperation;
import com.mongodb.operation.OperationExecutor;
import com.mongodb.binding.ClusterBinding;
import com.mongodb.binding.ReadWriteBinding;
import com.mongodb.binding.ReadBinding;
import com.mongodb.binding.WriteBinding;
import com.mongodb.selector.LatencyMinimizingServerSelector;
import com.mongodb.selector.ServerSelector;
import com.mongodb.event.CommandListenerMulticaster;
import com.mongodb.event.CommandListener;
import com.mongodb.event.ConnectionListener;
import com.mongodb.event.ConnectionPoolListener;
import com.mongodb.event.ClusterListener;
import com.mongodb.connection.StreamFactory;
import com.mongodb.management.JMXConnectionPoolListener;
import com.mongodb.connection.SocketStreamFactory;
import com.mongodb.connection.DefaultClusterFactory;
import java.util.concurrent.TimeUnit;
import com.mongodb.connection.ClusterSettings;
import java.util.Collection;
import com.mongodb.operation.ReadOperation;
import org.bson.codecs.Decoder;
import com.mongodb.operation.ListDatabasesOperation;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterType;
import com.mongodb.connection.ClusterDescription;
import java.util.Iterator;
import com.mongodb.connection.ServerDescription;
import java.util.ArrayList;
import com.mongodb.internal.connection.PowerOfTwoBufferPool;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.mongodb.connection.BufferProvider;
import com.mongodb.connection.Cluster;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import com.mongodb.annotations.ThreadSafe;

@ThreadSafe
public class Mongo
{
    static final String ADMIN_DATABASE_NAME = "admin";
    private final ConcurrentMap<String, DB> dbCache;
    private volatile WriteConcern writeConcern;
    private volatile ReadPreference readPreference;
    private final MongoClientOptions options;
    private final List<MongoCredential> credentialsList;
    private final Bytes.OptionHolder optionHolder;
    private final Cluster cluster;
    private final BufferProvider bufferProvider;
    private final ConcurrentLinkedQueue<ServerCursorAndNamespace> orphanedCursors;
    private final ExecutorService cursorCleaningService;
    
    public Mongo() {
        this(new ServerAddress(), createLegacyOptions());
    }
    
    public Mongo(final String host) {
        this(new ServerAddress(host), createLegacyOptions());
    }
    
    public Mongo(final String host, final MongoOptions options) {
        this(new ServerAddress(host), options.toClientOptions());
    }
    
    public Mongo(final String host, final int port) {
        this(new ServerAddress(host, port), createLegacyOptions());
    }
    
    public Mongo(final ServerAddress address) {
        this(address, createLegacyOptions());
    }
    
    public Mongo(final ServerAddress address, final MongoOptions options) {
        this(address, options.toClientOptions());
    }
    
    public Mongo(final ServerAddress left, final ServerAddress right) {
        this(Arrays.asList(left, right), createLegacyOptions());
    }
    
    public Mongo(final ServerAddress left, final ServerAddress right, final MongoOptions options) {
        this(Arrays.asList(left, right), options.toClientOptions());
    }
    
    public Mongo(final List<ServerAddress> seeds) {
        this(seeds, createLegacyOptions());
    }
    
    public Mongo(final List<ServerAddress> seeds, final MongoOptions options) {
        this(seeds, options.toClientOptions());
    }
    
    public Mongo(final MongoURI uri) {
        this(uri.toClientURI());
    }
    
    Mongo(final List<ServerAddress> seedList, final MongoClientOptions options) {
        this(seedList, Collections.emptyList(), options);
    }
    
    Mongo(final ServerAddress serverAddress, final MongoClientOptions options) {
        this(serverAddress, Collections.emptyList(), options);
    }
    
    Mongo(final ServerAddress serverAddress, final List<MongoCredential> credentialsList, final MongoClientOptions options) {
        this(createCluster(serverAddress, credentialsList, options), options, credentialsList);
    }
    
    Mongo(final List<ServerAddress> seedList, final List<MongoCredential> credentialsList, final MongoClientOptions options) {
        this(createCluster(seedList, credentialsList, options), options, credentialsList);
    }
    
    Mongo(final MongoClientURI mongoURI) {
        this(createCluster(mongoURI), mongoURI.getOptions(), (mongoURI.getCredentials() != null) ? Arrays.asList(mongoURI.getCredentials()) : Collections.emptyList());
    }
    
    Mongo(final Cluster cluster, final MongoClientOptions options, final List<MongoCredential> credentialsList) {
        this.dbCache = new ConcurrentHashMap<String, DB>();
        this.bufferProvider = new PowerOfTwoBufferPool();
        this.orphanedCursors = new ConcurrentLinkedQueue<ServerCursorAndNamespace>();
        this.cluster = cluster;
        this.options = options;
        this.readPreference = ((options.getReadPreference() != null) ? options.getReadPreference() : ReadPreference.primary());
        this.writeConcern = ((options.getWriteConcern() != null) ? options.getWriteConcern() : WriteConcern.UNACKNOWLEDGED);
        this.optionHolder = new Bytes.OptionHolder(null);
        this.credentialsList = Collections.unmodifiableList((List<? extends MongoCredential>)credentialsList);
        this.cursorCleaningService = (options.isCursorFinalizerEnabled() ? this.createCursorCleaningService() : null);
    }
    
    public void setWriteConcern(final WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }
    
    public WriteConcern getWriteConcern() {
        return this.writeConcern;
    }
    
    public void setReadPreference(final ReadPreference readPreference) {
        this.readPreference = readPreference;
    }
    
    public ReadPreference getReadPreference() {
        return this.readPreference;
    }
    
    public List<ServerAddress> getAllAddress() {
        return this.getServerAddressList();
    }
    
    public List<ServerAddress> getServerAddressList() {
        final List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
        for (final ServerDescription cur : this.getClusterDescription().getAll()) {
            serverAddresses.add(cur.getAddress());
        }
        return serverAddresses;
    }
    
    private ClusterDescription getClusterDescription() {
        return this.cluster.getDescription();
    }
    
    public ServerAddress getAddress() {
        final ClusterDescription description = this.getClusterDescription();
        if (description.getPrimaries().isEmpty()) {
            return null;
        }
        return description.getPrimaries().get(0).getAddress();
    }
    
    @Deprecated
    public MongoOptions getMongoOptions() {
        return new MongoOptions(this.getMongoClientOptions());
    }
    
    public ReplicaSetStatus getReplicaSetStatus() {
        final ClusterDescription clusterDescription = this.getClusterDescription();
        return (clusterDescription.getType() == ClusterType.REPLICA_SET && clusterDescription.getConnectionMode() == ClusterConnectionMode.MULTIPLE) ? new ReplicaSetStatus(this.cluster) : null;
    }
    
    @Deprecated
    public List<String> getDatabaseNames() {
        return (List<String>)new OperationIterable(new ListDatabasesOperation(MongoClient.getCommandCodec()), ReadPreference.primary(), this.createOperationExecutor()).map((Function)new Function<DBObject, String>() {
            @Override
            public String apply(final DBObject result) {
                return (String)result.get("name");
            }
        }).into(new ArrayList());
    }
    
    @Deprecated
    public DB getDB(final String dbName) {
        DB db = this.dbCache.get(dbName);
        if (db != null) {
            return db;
        }
        db = new DB(this, dbName, this.createOperationExecutor());
        final DB temp = this.dbCache.putIfAbsent(dbName, db);
        if (temp != null) {
            return temp;
        }
        return db;
    }
    
    public Collection<DB> getUsedDatabases() {
        return this.dbCache.values();
    }
    
    public void dropDatabase(final String dbName) {
        this.getDB(dbName).dropDatabase();
    }
    
    public void close() {
        this.cluster.close();
        if (this.cursorCleaningService != null) {
            this.cursorCleaningService.shutdownNow();
        }
    }
    
    @Deprecated
    public void slaveOk() {
        this.addOption(4);
    }
    
    public void setOptions(final int options) {
        this.optionHolder.set(options);
    }
    
    public void resetOptions() {
        this.optionHolder.reset();
    }
    
    public void addOption(final int option) {
        this.optionHolder.add(option);
    }
    
    public int getOptions() {
        return this.optionHolder.get();
    }
    
    public CommandResult fsync(final boolean async) {
        final DBObject command = new BasicDBObject("fsync", 1);
        if (async) {
            command.put("async", 1);
        }
        return this.getDB("admin").command(command);
    }
    
    public CommandResult fsyncAndLock() {
        final DBObject command = new BasicDBObject("fsync", 1);
        command.put("lock", 1);
        return this.getDB("admin").command(command);
    }
    
    public DBObject unlock() {
        return this.getDB("admin").getCollection("$cmd.sys.unlock").findOne();
    }
    
    public boolean isLocked() {
        final DBCollection inprogCollection = this.getDB("admin").getCollection("$cmd.sys.inprog");
        final BasicDBObject result = (BasicDBObject)inprogCollection.findOne();
        return result.containsField("fsyncLock") && result.getInt("fsyncLock") == 1;
    }
    
    @Override
    public String toString() {
        return "Mongo{options=" + this.getMongoClientOptions() + '}';
    }
    
    public int getMaxBsonObjectSize() {
        final List<ServerDescription> primaries = this.getClusterDescription().getPrimaries();
        return primaries.isEmpty() ? ServerDescription.getDefaultMaxDocumentSize() : primaries.get(0).getMaxDocumentSize();
    }
    
    public String getConnectPoint() {
        final ServerAddress master = this.getAddress();
        return (master != null) ? String.format("%s:%d", master.getHost(), master.getPort()) : null;
    }
    
    private static MongoClientOptions createLegacyOptions() {
        return MongoClientOptions.builder().legacyDefaults().build();
    }
    
    private static Cluster createCluster(final MongoClientURI mongoURI) {
        final List<MongoCredential> credentialList = (mongoURI.getCredentials() != null) ? Arrays.asList(mongoURI.getCredentials()) : Collections.emptyList();
        if (mongoURI.getHosts().size() == 1) {
            return createCluster(new ServerAddress(mongoURI.getHosts().get(0)), credentialList, mongoURI.getOptions());
        }
        final List<ServerAddress> seedList = new ArrayList<ServerAddress>(mongoURI.getHosts().size());
        for (final String host : mongoURI.getHosts()) {
            seedList.add(new ServerAddress(host));
        }
        return createCluster(seedList, credentialList, mongoURI.getOptions());
    }
    
    private static Cluster createCluster(final List<ServerAddress> seedList, final List<MongoCredential> credentialsList, final MongoClientOptions options) {
        return createCluster(ClusterSettings.builder().hosts(createNewSeedList(seedList)).requiredReplicaSetName(options.getRequiredReplicaSetName()).serverSelectionTimeout(options.getServerSelectionTimeout(), TimeUnit.MILLISECONDS).serverSelector(createServerSelector(options)).description(options.getDescription()).maxWaitQueueSize(options.getConnectionPoolSettings().getMaxWaitQueueSize()).build(), credentialsList, options);
    }
    
    private static Cluster createCluster(final ServerAddress serverAddress, final List<MongoCredential> credentialsList, final MongoClientOptions options) {
        return createCluster(ClusterSettings.builder().mode(getSingleServerClusterMode(options)).hosts(Arrays.asList(serverAddress)).requiredReplicaSetName(options.getRequiredReplicaSetName()).serverSelectionTimeout(options.getServerSelectionTimeout(), TimeUnit.MILLISECONDS).serverSelector(createServerSelector(options)).description(options.getDescription()).maxWaitQueueSize(options.getConnectionPoolSettings().getMaxWaitQueueSize()).build(), credentialsList, options);
    }
    
    private static Cluster createCluster(final ClusterSettings settings, final List<MongoCredential> credentialsList, final MongoClientOptions options) {
        return new DefaultClusterFactory().create(settings, options.getServerSettings(), options.getConnectionPoolSettings(), new SocketStreamFactory(options.getSocketSettings(), options.getSslSettings(), options.getSocketFactory()), new SocketStreamFactory(options.getHeartbeatSocketSettings(), options.getSslSettings(), options.getSocketFactory()), credentialsList, null, new JMXConnectionPoolListener(), null, createCommandListener(options.getCommandListeners()));
    }
    
    private static CommandListener createCommandListener(final List<CommandListener> commandListeners) {
        switch (commandListeners.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return commandListeners.get(0);
            }
            default: {
                return new CommandListenerMulticaster(commandListeners);
            }
        }
    }
    
    private static List<ServerAddress> createNewSeedList(final List<ServerAddress> seedList) {
        final List<ServerAddress> retVal = new ArrayList<ServerAddress>(seedList.size());
        for (final ServerAddress cur : seedList) {
            retVal.add(cur);
        }
        return retVal;
    }
    
    private static ServerSelector createServerSelector(final MongoClientOptions options) {
        return new LatencyMinimizingServerSelector(options.getLocalThreshold(), TimeUnit.MILLISECONDS);
    }
    
    Cluster getCluster() {
        return this.cluster;
    }
    
    Bytes.OptionHolder getOptionHolder() {
        return this.optionHolder;
    }
    
    BufferProvider getBufferProvider() {
        return this.bufferProvider;
    }
    
    MongoClientOptions getMongoClientOptions() {
        return this.options;
    }
    
    List<MongoCredential> getCredentialsList() {
        return this.credentialsList;
    }
    
    WriteBinding getWriteBinding() {
        return this.getReadWriteBinding(ReadPreference.primary());
    }
    
    ReadBinding getReadBinding(final ReadPreference readPreference) {
        return this.getReadWriteBinding(readPreference);
    }
    
    private ReadWriteBinding getReadWriteBinding(final ReadPreference readPreference) {
        return new ClusterBinding(this.getCluster(), readPreference);
    }
    
    void addOrphanedCursor(final ServerCursor serverCursor, final MongoNamespace namespace) {
        this.orphanedCursors.add(new ServerCursorAndNamespace(serverCursor, namespace));
    }
    
    OperationExecutor createOperationExecutor() {
        return new OperationExecutor() {
            @Override
            public <T> T execute(final ReadOperation<T> operation, final ReadPreference readPreference) {
                return Mongo.this.execute(operation, readPreference);
            }
            
            @Override
            public <T> T execute(final WriteOperation<T> operation) {
                return Mongo.this.execute(operation);
            }
        };
    }
    
     <T> T execute(final ReadOperation<T> operation, final ReadPreference readPreference) {
        final ReadBinding binding = this.getReadBinding(readPreference);
        try {
            return operation.execute(binding);
        }
        finally {
            binding.release();
        }
    }
    
     <T> T execute(final WriteOperation<T> operation) {
        final WriteBinding binding = this.getWriteBinding();
        try {
            return operation.execute(binding);
        }
        finally {
            binding.release();
        }
    }
    
    private ExecutorService createCursorCleaningService() {
        final ScheduledExecutorService newTimer = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
        newTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Mongo.this.cleanCursors();
            }
        }, 1L, 1L, TimeUnit.SECONDS);
        return newTimer;
    }
    
    private void cleanCursors() {
        ServerCursorAndNamespace cur;
        while ((cur = this.orphanedCursors.poll()) != null) {
            final ReadWriteBinding binding = new SingleServerBinding(this.cluster, cur.serverCursor.getAddress());
            try {
                final ConnectionSource source = binding.getReadConnectionSource();
                try {
                    final Connection connection = source.getConnection();
                    try {
                        connection.killCursor(cur.namespace, Collections.singletonList(cur.serverCursor.getId()));
                    }
                    finally {
                        connection.release();
                    }
                }
                finally {
                    source.release();
                }
            }
            finally {
                binding.release();
            }
        }
    }
    
    private static ClusterConnectionMode getSingleServerClusterMode(final MongoClientOptions options) {
        if (options.getRequiredReplicaSetName() == null) {
            return ClusterConnectionMode.SINGLE;
        }
        return ClusterConnectionMode.MULTIPLE;
    }
    
    private static class ServerCursorAndNamespace
    {
        private final ServerCursor serverCursor;
        private final MongoNamespace namespace;
        
        public ServerCursorAndNamespace(final ServerCursor serverCursor, final MongoNamespace namespace) {
            this.serverCursor = serverCursor;
            this.namespace = namespace;
        }
    }
    
    public static class Holder
    {
        private static final Holder INSTANCE;
        private final ConcurrentMap<String, Mongo> clients;
        
        public Holder() {
            this.clients = new ConcurrentHashMap<String, Mongo>();
        }
        
        public static Holder singleton() {
            return Holder.INSTANCE;
        }
        
        @Deprecated
        public Mongo connect(final MongoURI uri) {
            return this.connect(uri.toClientURI());
        }
        
        public Mongo connect(final MongoClientURI uri) {
            final String key = this.toKey(uri);
            Mongo client = this.clients.get(key);
            if (client == null) {
                final Mongo newbie = new MongoClient(uri);
                client = this.clients.putIfAbsent(key, newbie);
                if (client == null) {
                    client = newbie;
                }
                else {
                    newbie.close();
                }
            }
            return client;
        }
        
        private String toKey(final MongoClientURI uri) {
            return uri.toString();
        }
        
        static {
            INSTANCE = new Holder();
        }
    }
    
    static class DaemonThreadFactory implements ThreadFactory
    {
        private static final AtomicInteger poolNumber;
        private final AtomicInteger threadNumber;
        private final String namePrefix;
        
        DaemonThreadFactory() {
            this.threadNumber = new AtomicInteger(1);
            this.namePrefix = "pool-" + DaemonThreadFactory.poolNumber.getAndIncrement() + "-thread-";
        }
        
        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread t = new Thread(runnable, this.namePrefix + this.threadNumber.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
        
        static {
            poolNumber = new AtomicInteger(1);
        }
    }
}
