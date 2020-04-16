// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.HashSet;
import org.bson.codecs.Encoder;
import org.bson.BsonDocumentWrapper;
import com.mongodb.connection.BufferProvider;
import com.mongodb.operation.CommandReadOperation;
import com.mongodb.operation.CommandWriteOperation;
import org.bson.codecs.BsonDocumentCodec;
import com.mongodb.operation.DropUserOperation;
import com.mongodb.operation.CreateUserOperation;
import com.mongodb.operation.UpdateUserOperation;
import com.mongodb.operation.UserExistsOperation;
import java.util.Arrays;
import java.util.Iterator;
import com.mongodb.operation.CreateCollectionOperation;
import com.mongodb.operation.WriteOperation;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import com.mongodb.operation.ReadOperation;
import org.bson.codecs.Decoder;
import com.mongodb.operation.ListCollectionsOperation;
import java.util.List;
import org.bson.BsonValue;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import java.util.Set;
import org.bson.codecs.Codec;
import java.util.concurrent.ConcurrentHashMap;
import com.mongodb.operation.OperationExecutor;
import com.mongodb.annotations.ThreadSafe;

@ThreadSafe
public class DB
{
    private final Mongo mongo;
    private final String name;
    private final OperationExecutor executor;
    private final ConcurrentHashMap<String, DBCollection> collectionCache;
    private final Bytes.OptionHolder optionHolder;
    private final Codec<DBObject> commandCodec;
    private volatile ReadPreference readPreference;
    private volatile WriteConcern writeConcern;
    private static final Set<String> OBEDIENT_COMMANDS;
    
    DB(final Mongo mongo, final String name, final OperationExecutor executor) {
        if (!this.isValidName(name)) {
            throw new IllegalArgumentException("Invalid database name format. Database name is either empty or it contains spaces.");
        }
        this.mongo = mongo;
        this.name = name;
        this.executor = executor;
        this.collectionCache = new ConcurrentHashMap<String, DBCollection>();
        this.optionHolder = new Bytes.OptionHolder(mongo.getOptionHolder());
        this.commandCodec = MongoClient.getCommandCodec();
    }
    
    public DB(final Mongo mongo, final String name) {
        this(mongo, name, mongo.createOperationExecutor());
    }
    
    public Mongo getMongo() {
        return this.mongo;
    }
    
    public void setReadPreference(final ReadPreference readPreference) {
        this.readPreference = readPreference;
    }
    
    public void setWriteConcern(final WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }
    
    public ReadPreference getReadPreference() {
        return (this.readPreference != null) ? this.readPreference : this.mongo.getReadPreference();
    }
    
    public WriteConcern getWriteConcern() {
        return (this.writeConcern != null) ? this.writeConcern : this.mongo.getWriteConcern();
    }
    
    protected DBCollection doGetCollection(final String name) {
        return this.getCollection(name);
    }
    
    public DBCollection getCollection(final String name) {
        DBCollection collection = this.collectionCache.get(name);
        if (collection != null) {
            return collection;
        }
        collection = new DBCollection(name, this, this.executor);
        if (this.mongo.getMongoClientOptions().getDbDecoderFactory() != DefaultDBDecoder.FACTORY) {
            collection.setDBDecoderFactory(this.mongo.getMongoClientOptions().getDbDecoderFactory());
        }
        if (this.mongo.getMongoClientOptions().getDbEncoderFactory() != DefaultDBEncoder.FACTORY) {
            collection.setDBEncoderFactory(this.mongo.getMongoClientOptions().getDbEncoderFactory());
        }
        final DBCollection old = this.collectionCache.putIfAbsent(name, collection);
        return (old != null) ? old : collection;
    }
    
    public void dropDatabase() {
        this.executeCommand(new BsonDocument("dropDatabase", new BsonInt32(1)));
    }
    
    public DBCollection getCollectionFromString(final String collectionName) {
        return this.getCollection(collectionName);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Set<String> getCollectionNames() {
        final List<String> collectionNames = (List<String>)new OperationIterable(new ListCollectionsOperation(this.name, this.commandCodec), ReadPreference.primary(), this.executor).map((Function)new Function<DBObject, String>() {
            @Override
            public String apply(final DBObject result) {
                return (String)result.get("name");
            }
        }).into(new ArrayList());
        Collections.sort(collectionNames);
        return new LinkedHashSet<String>(collectionNames);
    }
    
    public DBCollection createCollection(final String collectionName, final DBObject options) {
        if (options != null) {
            this.executor.execute((WriteOperation<Object>)this.getCreateCollectionOperation(collectionName, options));
        }
        return this.getCollection(collectionName);
    }
    
    private CreateCollectionOperation getCreateCollectionOperation(final String collectionName, final DBObject options) {
        if (options.get("size") != null && !(options.get("size") instanceof Number)) {
            throw new IllegalArgumentException("'size' should be Number");
        }
        if (options.get("max") != null && !(options.get("max") instanceof Number)) {
            throw new IllegalArgumentException("'max' should be Number");
        }
        if (options.get("capped") != null && !(options.get("capped") instanceof Boolean)) {
            throw new IllegalArgumentException("'capped' should be Boolean");
        }
        if (options.get("autoIndexId") != null && !(options.get("capped") instanceof Boolean)) {
            throw new IllegalArgumentException("'capped' should be Boolean");
        }
        if (options.get("storageEngine") != null && !(options.get("storageEngine") instanceof DBObject)) {
            throw new IllegalArgumentException("storageEngine' should be DBObject");
        }
        boolean capped = false;
        boolean autoIndex = true;
        long sizeInBytes = 0L;
        long maxDocuments = 0L;
        Boolean usePowerOfTwoSizes = null;
        BsonDocument storageEngineOptions = null;
        if (options.get("capped") != null) {
            capped = (boolean)options.get("capped");
        }
        if (options.get("size") != null) {
            sizeInBytes = ((Number)options.get("size")).longValue();
        }
        if (options.get("autoIndexId") != null) {
            autoIndex = (boolean)options.get("autoIndexId");
        }
        if (options.get("max") != null) {
            maxDocuments = ((Number)options.get("max")).longValue();
        }
        if (options.get("usePowerOfTwoSizes") != null) {
            usePowerOfTwoSizes = (Boolean)options.get("usePowerOfTwoSizes");
        }
        if (options.get("storageEngine") != null) {
            storageEngineOptions = this.wrap((DBObject)options.get("storageEngine"));
        }
        return new CreateCollectionOperation(this.getName(), collectionName).capped(capped).sizeInBytes(sizeInBytes).autoIndex(autoIndex).maxDocuments(maxDocuments).usePowerOf2Sizes(usePowerOfTwoSizes).storageEngineOptions(storageEngineOptions);
    }
    
    public CommandResult command(final String command) {
        return this.command(new BasicDBObject(command, Boolean.TRUE), this.getReadPreference());
    }
    
    public CommandResult command(final DBObject command) {
        return this.command(command, this.getReadPreference());
    }
    
    public CommandResult command(final DBObject command, final DBEncoder encoder) {
        return this.command(command, this.getReadPreference(), encoder);
    }
    
    public CommandResult command(final DBObject command, final ReadPreference readPreference, final DBEncoder encoder) {
        try {
            return this.executeCommand(this.wrap(command, encoder), this.getCommandReadPreference(command, readPreference));
        }
        catch (MongoCommandException ex) {
            return new CommandResult(ex.getResponse(), ex.getServerAddress());
        }
    }
    
    public CommandResult command(final DBObject command, final ReadPreference readPreference) {
        return this.command(command, readPreference, null);
    }
    
    public CommandResult command(final String command, final ReadPreference readPreference) {
        return this.command(new BasicDBObject(command, true), readPreference);
    }
    
    public DB getSisterDB(final String name) {
        return this.mongo.getDB(name);
    }
    
    public boolean collectionExists(final String collectionName) {
        final Set<String> collectionNames = this.getCollectionNames();
        for (final String name : collectionNames) {
            if (name.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }
    
    public CommandResult doEval(final String code, final Object... args) {
        final DBObject commandDocument = new BasicDBObject("$eval", code).append("args", Arrays.asList(args));
        return this.executeCommand(this.wrap(commandDocument));
    }
    
    public Object eval(final String code, final Object... args) {
        final CommandResult result = this.doEval(code, args);
        result.throwOnError();
        return result.get("retval");
    }
    
    public CommandResult getStats() {
        final BsonDocument commandDocument = new BsonDocument("dbStats", new BsonInt32(1)).append("scale", new BsonInt32(1));
        return this.executeCommand(commandDocument);
    }
    
    @Deprecated
    public WriteResult addUser(final String userName, final char[] password) {
        return this.addUser(userName, password, false);
    }
    
    @Deprecated
    public WriteResult addUser(final String userName, final char[] password, final boolean readOnly) {
        final MongoCredential credential = MongoCredential.createMongoCRCredential(userName, this.getName(), password);
        boolean userExists = false;
        try {
            userExists = this.executor.execute((ReadOperation<Boolean>)new UserExistsOperation(this.getName(), userName), ReadPreference.primary());
        }
        catch (MongoCommandException e) {
            if (e.getCode() != 13) {
                throw e;
            }
        }
        if (userExists) {
            this.executor.execute((WriteOperation<Object>)new UpdateUserOperation(credential, readOnly));
            return new WriteResult(1, true, null);
        }
        this.executor.execute((WriteOperation<Object>)new CreateUserOperation(credential, readOnly));
        return new WriteResult(1, false, null);
    }
    
    @Deprecated
    public WriteResult removeUser(final String userName) {
        this.executor.execute((WriteOperation<Object>)new DropUserOperation(this.getName(), userName));
        return new WriteResult(1, true, null);
    }
    
    @Deprecated
    public void slaveOk() {
        this.addOption(4);
    }
    
    public void addOption(final int option) {
        this.optionHolder.add(option);
    }
    
    public void setOptions(final int options) {
        this.optionHolder.set(options);
    }
    
    public void resetOptions() {
        this.optionHolder.reset();
    }
    
    public int getOptions() {
        return this.optionHolder.get();
    }
    
    @Override
    public String toString() {
        return "DB{name='" + this.name + '\'' + '}';
    }
    
    CommandResult executeCommand(final BsonDocument commandDocument) {
        return new CommandResult(this.executor.execute(new CommandWriteOperation<BsonDocument>(this.getName(), commandDocument, new BsonDocumentCodec())));
    }
    
    CommandResult executeCommand(final BsonDocument commandDocument, final ReadPreference readPreference) {
        return new CommandResult(this.executor.execute(new CommandReadOperation<BsonDocument>(this.getName(), commandDocument, new BsonDocumentCodec()), readPreference));
    }
    
    OperationExecutor getExecutor() {
        return this.executor;
    }
    
    Bytes.OptionHolder getOptionHolder() {
        return this.optionHolder;
    }
    
    BufferProvider getBufferPool() {
        return this.getMongo().getBufferProvider();
    }
    
    private boolean isValidName(final String databaseName) {
        return databaseName.length() != 0 && !databaseName.contains(" ");
    }
    
    private BsonDocument wrap(final DBObject document) {
        return new BsonDocumentWrapper<Object>(document, this.commandCodec);
    }
    
    private BsonDocument wrap(final DBObject document, final DBEncoder encoder) {
        if (encoder == null) {
            return this.wrap(document);
        }
        return new BsonDocumentWrapper<Object>(document, new DBEncoderAdapter(encoder));
    }
    
    ReadPreference getCommandReadPreference(final DBObject command, final ReadPreference requestedPreference) {
        final String comString = command.keySet().iterator().next().toLowerCase();
        final boolean primaryRequired = !DB.OBEDIENT_COMMANDS.contains(comString);
        if (primaryRequired) {
            return ReadPreference.primary();
        }
        if (requestedPreference == null) {
            return ReadPreference.primary();
        }
        return requestedPreference;
    }
    
    static {
        (OBEDIENT_COMMANDS = new HashSet<String>()).add("aggregate");
        DB.OBEDIENT_COMMANDS.add("collstats");
        DB.OBEDIENT_COMMANDS.add("count");
        DB.OBEDIENT_COMMANDS.add("dbstats");
        DB.OBEDIENT_COMMANDS.add("distinct");
        DB.OBEDIENT_COMMANDS.add("geonear");
        DB.OBEDIENT_COMMANDS.add("geosearch");
        DB.OBEDIENT_COMMANDS.add("geowalk");
        DB.OBEDIENT_COMMANDS.add("group");
        DB.OBEDIENT_COMMANDS.add("listcollections");
        DB.OBEDIENT_COMMANDS.add("listindexes");
        DB.OBEDIENT_COMMANDS.add("parallelcollectionscan");
        DB.OBEDIENT_COMMANDS.add("text");
    }
}
