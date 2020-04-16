// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Loggers;
import org.bson.BsonDouble;
import java.util.Iterator;
import org.bson.BsonString;
import org.bson.BsonInt32;
import org.bson.BsonValue;
import org.bson.BsonInt64;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import com.mongodb.async.SingleResultCallback;
import org.bson.io.BsonOutput;
import com.mongodb.event.CommandListener;
import java.util.List;
import com.mongodb.MongoNamespace;
import com.mongodb.diagnostics.logging.Logger;

class KillCursorProtocol implements Protocol<Void>
{
    public static final Logger LOGGER;
    private static final String COMMAND_NAME = "killCursors";
    private final MongoNamespace namespace;
    private final List<Long> cursors;
    private CommandListener commandListener;
    
    public KillCursorProtocol(final MongoNamespace namespace, final List<Long> cursors) {
        this.namespace = namespace;
        this.cursors = cursors;
    }
    
    @Override
    public Void execute(final InternalConnection connection) {
        if (KillCursorProtocol.LOGGER.isDebugEnabled()) {
            KillCursorProtocol.LOGGER.debug(String.format("Killing cursors [%s] on connection [%s] to server %s", this.getCursorIdListAsString(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
        }
        final ByteBufferBsonOutput bsonOutput = new ByteBufferBsonOutput(connection);
        final long startTimeNanos = System.nanoTime();
        KillCursorsMessage message = null;
        try {
            message = new KillCursorsMessage(this.cursors);
            if (this.commandListener != null && this.namespace != null) {
                ProtocolHelper.sendCommandStartedEvent(message, this.namespace.getDatabaseName(), "killCursors", this.asCommandDocument(), connection.getDescription(), this.commandListener);
            }
            message.encode(bsonOutput);
            connection.sendMessage(bsonOutput.getByteBuffers(), message.getId());
            if (this.commandListener != null && this.namespace != null) {
                ProtocolHelper.sendCommandSucceededEvent(message, "killCursors", this.asCommandResponseDocument(), connection.getDescription(), startTimeNanos, this.commandListener);
            }
            return null;
        }
        catch (RuntimeException e) {
            if (this.commandListener != null && this.namespace != null) {
                ProtocolHelper.sendCommandFailedEvent(message, "killCursors", connection.getDescription(), startTimeNanos, e, this.commandListener);
            }
            throw e;
        }
        finally {
            bsonOutput.close();
        }
    }
    
    @Override
    public void executeAsync(final InternalConnection connection, final SingleResultCallback<Void> callback) {
        try {
            if (KillCursorProtocol.LOGGER.isDebugEnabled()) {
                KillCursorProtocol.LOGGER.debug(String.format("Asynchronously killing cursors [%s] on connection [%s] to server %s", this.getCursorIdListAsString(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
            }
            final ByteBufferBsonOutput bsonOutput = new ByteBufferBsonOutput(connection);
            final KillCursorsMessage message = new KillCursorsMessage(this.cursors);
            message.encode(bsonOutput);
            connection.sendMessageAsync(bsonOutput.getByteBuffers(), message.getId(), new SingleResultCallback<Void>() {
                @Override
                public void onResult(final Void result, final Throwable t) {
                    bsonOutput.close();
                    callback.onResult(result, t);
                }
            });
        }
        catch (Throwable t) {
            callback.onResult(null, t);
        }
    }
    
    @Override
    public void setCommandListener(final CommandListener commandListener) {
        this.commandListener = commandListener;
    }
    
    private BsonDocument asCommandDocument() {
        final BsonArray array = new BsonArray();
        for (final long cursor : this.cursors) {
            array.add(new BsonInt64(cursor));
        }
        return new BsonDocument("killCursors", (this.namespace == null) ? new BsonInt32(1) : new BsonString(this.namespace.getCollectionName())).append("cursors", array);
    }
    
    private BsonDocument asCommandResponseDocument() {
        final BsonArray cursorIdArray = new BsonArray();
        for (final long cursorId : this.cursors) {
            cursorIdArray.add(new BsonInt64(cursorId));
        }
        return new BsonDocument("ok", new BsonDouble(1.0)).append("cursorsUnknown", cursorIdArray);
    }
    
    private String getCursorIdListAsString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.cursors.size(); ++i) {
            final Long cursor = this.cursors.get(i);
            builder.append(cursor);
            if (i < this.cursors.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.killcursor");
    }
}
