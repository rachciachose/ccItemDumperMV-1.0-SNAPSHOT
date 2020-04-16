// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import java.util.Arrays;
import org.bson.BsonNull;
import org.bson.BsonBoolean;
import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.Function;
import org.bson.codecs.Decoder;
import org.bson.codecs.BsonDocumentCodec;
import com.mongodb.ExplainVerbosity;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.binding.WriteBinding;
import java.util.concurrent.TimeUnit;
import com.mongodb.assertions.Assertions;
import java.util.List;
import org.bson.BsonDocument;
import org.bson.BsonJavaScript;
import com.mongodb.MongoNamespace;

public class MapReduceToCollectionOperation implements AsyncWriteOperation<MapReduceStatistics>, WriteOperation<MapReduceStatistics>
{
    private final MongoNamespace namespace;
    private final BsonJavaScript mapFunction;
    private final BsonJavaScript reduceFunction;
    private final String collectionName;
    private BsonJavaScript finalizeFunction;
    private BsonDocument scope;
    private BsonDocument filter;
    private BsonDocument sort;
    private int limit;
    private boolean jsMode;
    private boolean verbose;
    private long maxTimeMS;
    private String action;
    private String databaseName;
    private boolean sharded;
    private boolean nonAtomic;
    private static final List<String> VALID_ACTIONS;
    
    public MapReduceToCollectionOperation(final MongoNamespace namespace, final BsonJavaScript mapFunction, final BsonJavaScript reduceFunction, final String collectionName) {
        this.action = "replace";
        this.namespace = Assertions.notNull("namespace", namespace);
        this.mapFunction = Assertions.notNull("mapFunction", mapFunction);
        this.reduceFunction = Assertions.notNull("reduceFunction", reduceFunction);
        this.collectionName = Assertions.notNull("collectionName", collectionName);
    }
    
    public BsonJavaScript getMapFunction() {
        return this.mapFunction;
    }
    
    public BsonJavaScript getReduceFunction() {
        return this.reduceFunction;
    }
    
    public String getCollectionName() {
        return this.collectionName;
    }
    
    public BsonJavaScript getFinalizeFunction() {
        return this.finalizeFunction;
    }
    
    public MapReduceToCollectionOperation finalizeFunction(final BsonJavaScript finalizeFunction) {
        this.finalizeFunction = finalizeFunction;
        return this;
    }
    
    public BsonDocument getScope() {
        return this.scope;
    }
    
    public MapReduceToCollectionOperation scope(final BsonDocument scope) {
        this.scope = scope;
        return this;
    }
    
    public BsonDocument getFilter() {
        return this.filter;
    }
    
    public MapReduceToCollectionOperation filter(final BsonDocument filter) {
        this.filter = filter;
        return this;
    }
    
    public BsonDocument getSort() {
        return this.sort;
    }
    
    public MapReduceToCollectionOperation sort(final BsonDocument sort) {
        this.sort = sort;
        return this;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public MapReduceToCollectionOperation limit(final int limit) {
        this.limit = limit;
        return this;
    }
    
    public boolean isJsMode() {
        return this.jsMode;
    }
    
    public MapReduceToCollectionOperation jsMode(final boolean jsMode) {
        this.jsMode = jsMode;
        return this;
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public MapReduceToCollectionOperation verbose(final boolean verbose) {
        this.verbose = verbose;
        return this;
    }
    
    public long getMaxTime(final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        return timeUnit.convert(this.maxTimeMS, TimeUnit.MILLISECONDS);
    }
    
    public MapReduceToCollectionOperation maxTime(final long maxTime, final TimeUnit timeUnit) {
        Assertions.notNull("timeUnit", timeUnit);
        this.maxTimeMS = TimeUnit.MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }
    
    public String getAction() {
        return this.action;
    }
    
    public MapReduceToCollectionOperation action(final String action) {
        Assertions.notNull("action", action);
        Assertions.isTrue("action must be one of: \"replace\", \"merge\", \"reduce\"", MapReduceToCollectionOperation.VALID_ACTIONS.contains(action));
        this.action = action;
        return this;
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    public MapReduceToCollectionOperation databaseName(final String databaseName) {
        this.databaseName = databaseName;
        return this;
    }
    
    public boolean isSharded() {
        return this.sharded;
    }
    
    public MapReduceToCollectionOperation sharded(final boolean sharded) {
        this.sharded = sharded;
        return this;
    }
    
    public boolean isNonAtomic() {
        return this.nonAtomic;
    }
    
    public MapReduceToCollectionOperation nonAtomic(final boolean nonAtomic) {
        this.nonAtomic = nonAtomic;
        return this;
    }
    
    @Override
    public MapReduceStatistics execute(final WriteBinding binding) {
        return CommandOperationHelper.executeWrappedCommandProtocol(binding, this.namespace.getDatabaseName(), this.getCommand(), this.transformer());
    }
    
    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<MapReduceStatistics> callback) {
        CommandOperationHelper.executeWrappedCommandProtocolAsync(binding, this.namespace.getDatabaseName(), this.getCommand(), this.transformer(), callback);
    }
    
    public ReadOperation<BsonDocument> asExplainableOperation(final ExplainVerbosity explainVerbosity) {
        return this.createExplainableOperation(explainVerbosity);
    }
    
    public AsyncReadOperation<BsonDocument> asExplainableOperationAsync(final ExplainVerbosity explainVerbosity) {
        return this.createExplainableOperation(explainVerbosity);
    }
    
    private CommandReadOperation<BsonDocument> createExplainableOperation(final ExplainVerbosity explainVerbosity) {
        return new CommandReadOperation<BsonDocument>(this.namespace.getDatabaseName(), ExplainHelper.asExplainCommand(this.getCommand(), explainVerbosity), new BsonDocumentCodec());
    }
    
    private Function<BsonDocument, MapReduceStatistics> transformer() {
        return new Function<BsonDocument, MapReduceStatistics>() {
            @Override
            public MapReduceStatistics apply(final BsonDocument result) {
                return MapReduceHelper.createStatistics(result);
            }
        };
    }
    
    private BsonDocument getCommand() {
        final BsonDocument outputDocument = new BsonDocument(this.getAction(), new BsonString(this.getCollectionName()));
        outputDocument.append("sharded", BsonBoolean.valueOf(this.isSharded()));
        outputDocument.append("nonAtomic", BsonBoolean.valueOf(this.isNonAtomic()));
        if (this.getDatabaseName() != null) {
            outputDocument.put("db", new BsonString(this.getDatabaseName()));
        }
        final BsonDocument commandDocument = new BsonDocument("mapreduce", new BsonString(this.namespace.getCollectionName())).append("map", this.getMapFunction()).append("reduce", this.getReduceFunction()).append("out", outputDocument).append("query", asValueOrNull(this.getFilter())).append("sort", asValueOrNull(this.getSort())).append("finalize", asValueOrNull(this.getFinalizeFunction())).append("scope", asValueOrNull(this.getScope())).append("verbose", BsonBoolean.valueOf(this.isVerbose()));
        DocumentHelper.putIfNotZero(commandDocument, "limit", this.getLimit());
        DocumentHelper.putIfNotZero(commandDocument, "maxTimeMS", this.getMaxTime(TimeUnit.MILLISECONDS));
        DocumentHelper.putIfTrue(commandDocument, "jsMode", this.isJsMode());
        return commandDocument;
    }
    
    private static BsonValue asValueOrNull(final BsonValue value) {
        return (value == null) ? BsonNull.VALUE : value;
    }
    
    static {
        VALID_ACTIONS = Arrays.asList("replace", "merge", "reduce");
    }
}
