// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.assertions.Assertions;
import java.util.Set;
import java.util.Collection;
import com.mongodb.operation.ReadOperation;
import com.mongodb.operation.BatchCursor;
import java.util.Iterator;
import org.bson.BsonDocument;
import java.util.concurrent.TimeUnit;
import java.util.NoSuchElementException;
import com.mongodb.operation.FindOperation;
import java.util.ArrayList;
import com.mongodb.client.MongoCursor;
import java.util.List;
import org.bson.codecs.Decoder;
import com.mongodb.client.model.FindOptions;
import com.mongodb.operation.OperationExecutor;
import com.mongodb.annotations.NotThreadSafe;

@NotThreadSafe
public class DBCursor implements Cursor, Iterable<DBObject>
{
    private final DBCollection collection;
    private final OperationExecutor executor;
    private final DBObject filter;
    private final DBObject modifiers;
    private DBObject projection;
    private DBObject sort;
    private final FindOptions findOptions;
    private int options;
    private ReadPreference readPreference;
    private Decoder<DBObject> resultDecoder;
    private DBDecoderFactory decoderFactory;
    private IteratorOrArray iteratorOrArray;
    private DBObject currentObject;
    private int numSeen;
    private boolean closed;
    private final List<DBObject> all;
    private MongoCursor<DBObject> cursor;
    private OptionalFinalizer optionalFinalizer;
    
    public DBCursor(final DBCollection collection, final DBObject query, final DBObject fields, final ReadPreference readPreference) {
        this(collection, collection.getExecutor(), query, new BasicDBObject(), fields, null, new FindOptions(), readPreference);
        this.addOption(collection.getOptions());
        final DBObject indexKeys = lookupSuitableHints(query, collection.getHintFields());
        if (indexKeys != null) {
            this.hint(indexKeys);
        }
    }
    
    private DBCursor(final DBCollection collection, final OperationExecutor executor, final DBObject filter, final DBObject modifiers, final DBObject fields, final DBObject sort, final FindOptions findOptions, final ReadPreference readPreference) {
        this.all = new ArrayList<DBObject>();
        if (collection == null) {
            throw new IllegalArgumentException("Collection can't be null");
        }
        this.collection = collection;
        this.executor = executor;
        this.filter = filter;
        this.modifiers = modifiers;
        this.projection = fields;
        this.sort = sort;
        this.findOptions = findOptions;
        this.readPreference = readPreference;
        this.resultDecoder = collection.getObjectCodec();
        this.decoderFactory = collection.getDBDecoderFactory();
    }
    
    public DBCursor copy() {
        return new DBCursor(this.collection, this.executor, this.filter, this.modifiers, this.projection, this.sort, new FindOptions(this.findOptions), this.readPreference);
    }
    
    @Override
    public boolean hasNext() {
        if (this.closed) {
            throw new IllegalStateException("Cursor has been closed");
        }
        if (this.cursor == null) {
            final FindOperation<DBObject> operation = this.getQueryOperation(this.findOptions, this.resultDecoder);
            if (operation.getCursorType() == CursorType.Tailable) {
                operation.cursorType(CursorType.TailableAwait);
            }
            this.initializeCursor(operation);
        }
        final boolean hasNext = this.cursor.hasNext();
        this.setServerCursorOnFinalizer(this.cursor.getServerCursor());
        return hasNext;
    }
    
    @Override
    public DBObject next() {
        this.checkIteratorOrArray(IteratorOrArray.ITERATOR);
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return this.nextInternal();
    }
    
    public DBObject tryNext() {
        if (this.cursor == null) {
            final FindOperation<DBObject> operation = this.getQueryOperation(this.findOptions, this.resultDecoder);
            if (!operation.getCursorType().isTailable()) {
                throw new IllegalArgumentException("Can only be used with a tailable cursor");
            }
            this.initializeCursor(operation);
        }
        final DBObject next = this.cursor.tryNext();
        this.setServerCursorOnFinalizer(this.cursor.getServerCursor());
        return this.currentObject(next);
    }
    
    public DBObject curr() {
        return this.currentObject;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    public DBCursor addOption(final int option) {
        this.setOptions(this.options |= option);
        return this;
    }
    
    public DBCursor setOptions(final int options) {
        if ((options & 0x40) != 0x0) {
            throw new UnsupportedOperationException("exhaust query option is not supported");
        }
        this.options = options;
        return this;
    }
    
    public DBCursor resetOptions() {
        this.options = 0;
        return this;
    }
    
    public int getOptions() {
        return this.options;
    }
    
    public int getLimit() {
        return this.findOptions.getLimit();
    }
    
    public int getBatchSize() {
        return this.findOptions.getBatchSize();
    }
    
    public DBCursor addSpecial(final String name, final Object value) {
        if (name == null || value == null) {
            return this;
        }
        if ("$comment".equals(name)) {
            this.comment(value.toString());
        }
        else if ("$explain".equals(name)) {
            this.modifiers.put("$explain", true);
        }
        else if ("$hint".equals(name)) {
            if (value instanceof String) {
                this.hint((String)value);
            }
            else {
                this.hint((DBObject)value);
            }
        }
        else if ("$maxScan".equals(name)) {
            this.maxScan(((Number)value).intValue());
        }
        else if ("$maxTimeMS".equals(name)) {
            this.maxTime(((Number)value).longValue(), TimeUnit.MILLISECONDS);
        }
        else if ("$max".equals(name)) {
            this.max((DBObject)value);
        }
        else if ("$min".equals(name)) {
            this.min((DBObject)value);
        }
        else if ("$orderby".equals(name)) {
            this.sort((DBObject)value);
        }
        else if ("$returnKey".equals(name)) {
            this.returnKey();
        }
        else if ("$showDiskLoc".equals(name)) {
            this.showDiskLoc();
        }
        else if ("$snapshot".equals(name)) {
            this.snapshot();
        }
        else {
            if (!"$natural".equals(name)) {
                throw new IllegalArgumentException(name + "is not a supported modifier");
            }
            this.sort(new BasicDBObject("$natural", ((Number)value).intValue()));
        }
        return this;
    }
    
    public DBCursor comment(final String comment) {
        this.modifiers.put("$comment", comment);
        return this;
    }
    
    public DBCursor maxScan(final int max) {
        this.modifiers.put("$maxScan", max);
        return this;
    }
    
    public DBCursor max(final DBObject max) {
        this.modifiers.put("$max", max);
        return this;
    }
    
    public DBCursor min(final DBObject min) {
        this.modifiers.put("$min", min);
        return this;
    }
    
    public DBCursor returnKey() {
        this.modifiers.put("$returnKey", true);
        return this;
    }
    
    public DBCursor showDiskLoc() {
        this.modifiers.put("$showDiskLoc", true);
        return this;
    }
    
    public DBCursor hint(final DBObject indexKeys) {
        this.modifiers.put("$hint", indexKeys);
        return this;
    }
    
    public DBCursor hint(final String indexName) {
        this.modifiers.put("$hint", indexName);
        return this;
    }
    
    public DBCursor maxTime(final long maxTime, final TimeUnit timeUnit) {
        this.findOptions.maxTime(maxTime, timeUnit);
        return this;
    }
    
    public DBCursor snapshot() {
        this.modifiers.put("$snapshot", true);
        return this;
    }
    
    public DBObject explain() {
        return DBObjects.toDBObject(this.executor.execute(this.getQueryOperation(this.findOptions, this.collection.getObjectCodec()).asExplainableOperation(ExplainVerbosity.QUERY_PLANNER), this.getReadPreference()));
    }
    
    private FindOperation<DBObject> getQueryOperation(final FindOptions options, final Decoder<DBObject> decoder) {
        final FindOperation<DBObject> operation = new FindOperation<DBObject>(this.collection.getNamespace(), decoder).filter(this.collection.wrapAllowNull(this.filter)).batchSize(options.getBatchSize()).skip(options.getSkip()).limit(options.getLimit()).maxTime(options.getMaxTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS).modifiers(this.collection.wrap(this.modifiers)).projection(this.collection.wrapAllowNull(this.projection)).sort(this.collection.wrapAllowNull(this.sort)).noCursorTimeout(options.isNoCursorTimeout()).oplogReplay(options.isOplogReplay()).partial(options.isPartial());
        if ((this.options & 0x2) != 0x0) {
            if ((this.options & 0x20) != 0x0) {
                operation.cursorType(CursorType.TailableAwait);
            }
            else {
                operation.cursorType(CursorType.Tailable);
            }
        }
        if ((this.options & 0x8) != 0x0) {
            operation.oplogReplay(true);
        }
        if ((this.options & 0x10) != 0x0) {
            operation.noCursorTimeout(true);
        }
        if ((this.options & 0x80) != 0x0) {
            operation.partial(true);
        }
        return operation;
    }
    
    public DBCursor sort(final DBObject orderBy) {
        this.sort = orderBy;
        return this;
    }
    
    public DBCursor limit(final int limit) {
        this.findOptions.limit(limit);
        return this;
    }
    
    public DBCursor batchSize(final int numberOfElements) {
        this.findOptions.batchSize(numberOfElements);
        return this;
    }
    
    public DBCursor skip(final int numberOfElements) {
        this.findOptions.skip(numberOfElements);
        return this;
    }
    
    @Override
    public long getCursorId() {
        if (this.cursor != null && this.cursor.getServerCursor() != null) {
            return this.cursor.getServerCursor().getId();
        }
        return 0L;
    }
    
    public int numSeen() {
        return this.numSeen;
    }
    
    @Override
    public void close() {
        this.closed = true;
        if (this.cursor != null) {
            this.cursor.close();
            this.cursor = null;
            this.setServerCursorOnFinalizer(null);
        }
        this.currentObject = null;
    }
    
    @Deprecated
    public DBCursor slaveOk() {
        return this.addOption(4);
    }
    
    @Override
    public Iterator<DBObject> iterator() {
        return this.copy();
    }
    
    public List<DBObject> toArray() {
        return this.toArray(Integer.MAX_VALUE);
    }
    
    public List<DBObject> toArray(final int max) {
        this.checkIteratorOrArray(IteratorOrArray.ARRAY);
        this.fillArray(max - 1);
        return this.all;
    }
    
    public int count() {
        return (int)this.collection.getCount(this.getQuery(), this.getKeysWanted(), 0L, 0L, this.getReadPreferenceForCursor(), this.findOptions.getMaxTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS, this.collection.wrap(this.modifiers).get("$hint"));
    }
    
    public DBObject one() {
        return this.collection.findOne(this.getQuery(), this.getKeysWanted(), this.sort, this.getReadPreferenceForCursor(), this.findOptions.getMaxTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }
    
    public int length() {
        this.checkIteratorOrArray(IteratorOrArray.ARRAY);
        this.fillArray(Integer.MAX_VALUE);
        return this.all.size();
    }
    
    public int itcount() {
        int n = 0;
        while (this.hasNext()) {
            this.next();
            ++n;
        }
        return n;
    }
    
    public int size() {
        return (int)this.collection.getCount(this.getQuery(), this.getKeysWanted(), this.findOptions.getLimit(), this.findOptions.getSkip(), this.getReadPreference(), this.findOptions.getMaxTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }
    
    public DBObject getKeysWanted() {
        return this.projection;
    }
    
    public DBObject getQuery() {
        return this.filter;
    }
    
    public DBCollection getCollection() {
        return this.collection;
    }
    
    @Override
    public ServerAddress getServerAddress() {
        if (this.cursor != null) {
            return this.cursor.getServerAddress();
        }
        return null;
    }
    
    public DBCursor setReadPreference(final ReadPreference readPreference) {
        this.readPreference = readPreference;
        return this;
    }
    
    public ReadPreference getReadPreference() {
        return this.readPreference;
    }
    
    public DBCursor setDecoderFactory(final DBDecoderFactory factory) {
        this.decoderFactory = factory;
        this.resultDecoder = new DBDecoderAdapter(factory.create(), this.collection, this.getCollection().getBufferPool());
        return this;
    }
    
    public DBDecoderFactory getDecoderFactory() {
        return this.decoderFactory;
    }
    
    @Override
    public String toString() {
        return "DBCursor{collection=" + this.collection + ", find=" + this.findOptions + ((this.cursor != null) ? (", cursor=" + this.cursor.getServerCursor()) : "") + '}';
    }
    
    private void initializeCursor(final FindOperation<DBObject> operation) {
        this.cursor = new MongoBatchCursorAdapter<DBObject>(this.executor.execute((ReadOperation<BatchCursor<DBObject>>)operation, this.getReadPreferenceForCursor()));
        if (this.isCursorFinalizerEnabled() && this.cursor.getServerCursor() != null) {
            this.optionalFinalizer = new OptionalFinalizer(this.collection.getDB().getMongo(), this.collection.getNamespace());
        }
    }
    
    private boolean isCursorFinalizerEnabled() {
        return this.collection.getDB().getMongo().getMongoClientOptions().isCursorFinalizerEnabled();
    }
    
    private void setServerCursorOnFinalizer(final ServerCursor serverCursor) {
        if (this.optionalFinalizer != null) {
            this.optionalFinalizer.setServerCursor(serverCursor);
        }
    }
    
    private void checkIteratorOrArray(final IteratorOrArray expected) {
        if (this.iteratorOrArray == null) {
            this.iteratorOrArray = expected;
            return;
        }
        if (expected == this.iteratorOrArray) {
            return;
        }
        throw new IllegalArgumentException("Can't switch cursor access methods");
    }
    
    private ReadPreference getReadPreferenceForCursor() {
        ReadPreference readPreference = this.getReadPreference();
        if ((this.options & 0x4) != 0x0 && !readPreference.isSlaveOk()) {
            readPreference = ReadPreference.secondaryPreferred();
        }
        return readPreference;
    }
    
    private void fillArray(final int n) {
        this.checkIteratorOrArray(IteratorOrArray.ARRAY);
        while (n >= this.all.size() && this.hasNext()) {
            this.all.add(this.nextInternal());
        }
    }
    
    private DBObject nextInternal() {
        if (this.iteratorOrArray == null) {
            this.checkIteratorOrArray(IteratorOrArray.ITERATOR);
        }
        final DBObject next = this.cursor.next();
        this.setServerCursorOnFinalizer(this.cursor.getServerCursor());
        return this.currentObject(next);
    }
    
    private DBObject currentObject(final DBObject newCurrentObject) {
        if (newCurrentObject != null) {
            this.currentObject = newCurrentObject;
            ++this.numSeen;
            if (this.projection != null && !this.projection.keySet().isEmpty()) {
                this.currentObject.markAsPartialObject();
            }
        }
        return newCurrentObject;
    }
    
    private static DBObject lookupSuitableHints(final DBObject query, final List<DBObject> hints) {
        if (hints == null) {
            return null;
        }
        final Set<String> keys = query.keySet();
        for (final DBObject hint : hints) {
            if (keys.containsAll(hint.keySet())) {
                return hint;
            }
        }
        return null;
    }
    
    private enum IteratorOrArray
    {
        ITERATOR, 
        ARRAY;
    }
    
    private static class OptionalFinalizer
    {
        private final Mongo mongo;
        private final MongoNamespace namespace;
        private volatile ServerCursor serverCursor;
        
        private OptionalFinalizer(final Mongo mongo, final MongoNamespace namespace) {
            this.namespace = Assertions.notNull("namespace", namespace);
            this.mongo = Assertions.notNull("mongo", mongo);
        }
        
        private void setServerCursor(final ServerCursor serverCursor) {
            this.serverCursor = serverCursor;
        }
        
        @Override
        protected void finalize() {
            if (this.serverCursor != null) {
                this.mongo.addOrphanedCursor(this.serverCursor, this.namespace);
            }
        }
    }
}
