// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.util.Map;
import com.mongodb.Tag;
import java.util.ArrayList;
import com.mongodb.TagSet;
import org.bson.BsonString;
import org.bson.BsonBoolean;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import java.util.concurrent.TimeUnit;
import org.bson.BsonInt32;
import org.bson.BsonArray;
import com.mongodb.ServerAddress;
import org.bson.BsonDocument;

final class DescriptionHelper
{
    static ConnectionDescription createConnectionDescription(final ConnectionId connectionId, final BsonDocument isMasterResult, final BsonDocument buildInfoResult) {
        return new ConnectionDescription(connectionId, getVersion(buildInfoResult), getServerType(isMasterResult), getMaxWriteBatchSize(isMasterResult), getMaxBsonObjectSize(isMasterResult), getMaxMessageSizeBytes(isMasterResult));
    }
    
    static ServerDescription createServerDescription(final ServerAddress serverAddress, final BsonDocument isMasterResult, final ServerVersion serverVersion, final long roundTripTime) {
        return ServerDescription.builder().state(ServerConnectionState.CONNECTED).version(serverVersion).address(serverAddress).type(getServerType(isMasterResult)).canonicalAddress(isMasterResult.containsKey("me") ? isMasterResult.getString("me").getValue() : null).hosts(listToSet(isMasterResult.getArray("hosts", new BsonArray()))).passives(listToSet(isMasterResult.getArray("passives", new BsonArray()))).arbiters(listToSet(isMasterResult.getArray("arbiters", new BsonArray()))).primary(getString(isMasterResult, "primary")).maxDocumentSize(getMaxBsonObjectSize(isMasterResult)).tagSet(getTagSetFromDocument(isMasterResult.getDocument("tags", new BsonDocument()))).setName(getString(isMasterResult, "setName")).minWireVersion(isMasterResult.getInt32("minWireVersion", new BsonInt32(ServerDescription.getDefaultMinWireVersion())).getValue()).maxWireVersion(isMasterResult.getInt32("maxWireVersion", new BsonInt32(ServerDescription.getDefaultMaxWireVersion())).getValue()).electionId(getElectionId(isMasterResult)).roundTripTime(roundTripTime, TimeUnit.NANOSECONDS).ok(CommandHelper.isCommandOk(isMasterResult)).build();
    }
    
    private static ObjectId getElectionId(final BsonDocument isMasterResult) {
        return isMasterResult.containsKey("electionId") ? isMasterResult.getObjectId("electionId").getValue() : null;
    }
    
    private static int getMaxMessageSizeBytes(final BsonDocument isMasterResult) {
        return isMasterResult.getInt32("maxMessageSizeBytes", new BsonInt32(ConnectionDescription.getDefaultMaxMessageSize())).getValue();
    }
    
    private static int getMaxBsonObjectSize(final BsonDocument isMasterResult) {
        return isMasterResult.getInt32("maxBsonObjectSize", new BsonInt32(ServerDescription.getDefaultMaxDocumentSize())).getValue();
    }
    
    private static int getMaxWriteBatchSize(final BsonDocument isMasterResult) {
        return isMasterResult.getInt32("maxWriteBatchSize", new BsonInt32(ConnectionDescription.getDefaultMaxWriteBatchSize())).getValue();
    }
    
    private static String getString(final BsonDocument response, final String key) {
        if (response.containsKey(key)) {
            return response.getString(key).getValue();
        }
        return null;
    }
    
    static ServerVersion getVersion(final BsonDocument buildInfoResult) {
        final List<BsonValue> versionArray = buildInfoResult.getArray("versionArray").subList(0, 3);
        return new ServerVersion(Arrays.asList(versionArray.get(0).asInt32().getValue(), versionArray.get(1).asInt32().getValue(), versionArray.get(2).asInt32().getValue()));
    }
    
    private static Set<String> listToSet(final BsonArray array) {
        if (array == null || array.isEmpty()) {
            return Collections.emptySet();
        }
        final Set<String> set = new HashSet<String>();
        for (final BsonValue value : array) {
            set.add(value.asString().getValue());
        }
        return set;
    }
    
    private static ServerType getServerType(final BsonDocument isMasterResult) {
        if (!CommandHelper.isCommandOk(isMasterResult)) {
            return ServerType.UNKNOWN;
        }
        if (isReplicaSetMember(isMasterResult)) {
            if (isMasterResult.getBoolean("hidden", BsonBoolean.FALSE).getValue()) {
                return ServerType.REPLICA_SET_OTHER;
            }
            if (isMasterResult.getBoolean("ismaster", BsonBoolean.FALSE).getValue()) {
                return ServerType.REPLICA_SET_PRIMARY;
            }
            if (isMasterResult.getBoolean("secondary", BsonBoolean.FALSE).getValue()) {
                return ServerType.REPLICA_SET_SECONDARY;
            }
            if (isMasterResult.getBoolean("arbiterOnly", BsonBoolean.FALSE).getValue()) {
                return ServerType.REPLICA_SET_ARBITER;
            }
            if (isMasterResult.containsKey("setName") && isMasterResult.containsKey("hosts")) {
                return ServerType.REPLICA_SET_OTHER;
            }
            return ServerType.REPLICA_SET_GHOST;
        }
        else {
            if (isMasterResult.containsKey("msg") && isMasterResult.get("msg").equals(new BsonString("isdbgrid"))) {
                return ServerType.SHARD_ROUTER;
            }
            return ServerType.STANDALONE;
        }
    }
    
    private static boolean isReplicaSetMember(final BsonDocument isMasterResult) {
        return isMasterResult.containsKey("setName") || isMasterResult.getBoolean("isreplicaset", BsonBoolean.FALSE).getValue();
    }
    
    private static TagSet getTagSetFromDocument(final BsonDocument tagsDocuments) {
        final List<Tag> tagList = new ArrayList<Tag>();
        for (final Map.Entry<String, BsonValue> curEntry : tagsDocuments.entrySet()) {
            tagList.add(new Tag(curEntry.getKey(), curEntry.getValue().asString().getValue()));
        }
        return new TagSet(tagList);
    }
}
