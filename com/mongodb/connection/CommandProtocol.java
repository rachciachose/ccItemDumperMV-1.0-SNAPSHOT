// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import com.mongodb.diagnostics.logging.Loggers;
import org.bson.io.OutputBuffer;
import org.bson.io.BsonOutput;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.MongoCommandException;
import org.bson.BsonReader;
import org.bson.codecs.DecoderContext;
import org.bson.BsonDocumentReader;
import org.bson.codecs.BsonDocumentCodec;
import com.mongodb.assertions.Assertions;
import com.mongodb.event.CommandListener;
import org.bson.FieldNameValidator;
import org.bson.codecs.Decoder;
import org.bson.BsonDocument;
import com.mongodb.MongoNamespace;
import java.util.Set;
import com.mongodb.diagnostics.logging.Logger;

class CommandProtocol<T> implements Protocol<T>
{
    public static final Logger LOGGER;
    private static final Set<String> SECURITY_SENSITIVE_COMMANDS;
    private final MongoNamespace namespace;
    private final BsonDocument command;
    private final Decoder<T> commandResultDecoder;
    private final FieldNameValidator fieldNameValidator;
    private boolean slaveOk;
    private CommandListener commandListener;
    private volatile String commandName;
    
    public CommandProtocol(final String database, final BsonDocument command, final FieldNameValidator fieldNameValidator, final Decoder<T> commandResultDecoder) {
        Assertions.notNull("database", database);
        this.namespace = new MongoNamespace(database, "$cmd");
        this.command = Assertions.notNull("command", command);
        this.commandResultDecoder = Assertions.notNull("commandResultDecoder", commandResultDecoder);
        this.fieldNameValidator = Assertions.notNull("fieldNameValidator", fieldNameValidator);
    }
    
    public boolean isSlaveOk() {
        return this.slaveOk;
    }
    
    public CommandProtocol<T> slaveOk(final boolean slaveOk) {
        this.slaveOk = slaveOk;
        return this;
    }
    
    @Override
    public T execute(final InternalConnection connection) {
        if (CommandProtocol.LOGGER.isDebugEnabled()) {
            CommandProtocol.LOGGER.debug(String.format("Sending command {%s : %s} to database %s on connection [%s] to server %s", this.getCommandName(), this.command.values().iterator().next(), this.namespace.getDatabaseName(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
        }
        final long startTimeNanos = System.nanoTime();
        CommandMessage commandMessage = null;
        try {
            commandMessage = this.sendMessage(connection);
            final ResponseBuffers responseBuffers = connection.receiveMessage(commandMessage.getId());
            ReplyMessage<BsonDocument> replyMessage;
            try {
                replyMessage = new ReplyMessage<BsonDocument>(responseBuffers, new BsonDocumentCodec(), commandMessage.getId());
            }
            finally {
                responseBuffers.close();
            }
            final BsonDocument response = replyMessage.getDocuments().get(0);
            if (!ProtocolHelper.isCommandOk(response)) {
                throw ProtocolHelper.getCommandFailureException(response, connection.getDescription().getServerAddress());
            }
            final T retval = this.commandResultDecoder.decode(new BsonDocumentReader(response), DecoderContext.builder().build());
            if (this.commandListener != null) {
                final BsonDocument responseDocumentForEvent = CommandProtocol.SECURITY_SENSITIVE_COMMANDS.contains(this.getCommandName()) ? new BsonDocument() : response;
                ProtocolHelper.sendCommandSucceededEvent(commandMessage, this.getCommandName(), responseDocumentForEvent, connection.getDescription(), startTimeNanos, this.commandListener);
            }
            CommandProtocol.LOGGER.debug("Command execution completed");
            return retval;
        }
        catch (RuntimeException e) {
            if (this.commandListener != null) {
                RuntimeException commandEventException = e;
                if (e instanceof MongoCommandException && CommandProtocol.SECURITY_SENSITIVE_COMMANDS.contains(this.getCommandName())) {
                    commandEventException = new MongoCommandException(new BsonDocument(), connection.getDescription().getServerAddress());
                }
                ProtocolHelper.sendCommandFailedEvent(commandMessage, this.getCommandName(), connection.getDescription(), startTimeNanos, commandEventException, this.commandListener);
            }
            throw e;
        }
    }
    
    @Override
    public void executeAsync(final InternalConnection connection, final SingleResultCallback<T> callback) {
        try {
            if (CommandProtocol.LOGGER.isDebugEnabled()) {
                CommandProtocol.LOGGER.debug(String.format("Asynchronously sending command {%s : %s} to database %s on connection [%s] to server %s", this.getCommandName(), this.command.values().iterator().next(), this.namespace.getDatabaseName(), connection.getDescription().getConnectionId(), connection.getDescription().getServerAddress()));
            }
            final ByteBufferBsonOutput bsonOutput = new ByteBufferBsonOutput(connection);
            final CommandMessage message = new CommandMessage(this.namespace.getFullName(), this.command, this.slaveOk, this.fieldNameValidator, ProtocolHelper.getMessageSettings(connection.getDescription()));
            ProtocolHelper.encodeMessage(message, bsonOutput);
            final SingleResultCallback<ResponseBuffers> receiveCallback = new CommandResultCallback<Object>(callback, this.commandResultDecoder, message.getId(), connection.getDescription().getServerAddress());
            connection.sendMessageAsync(bsonOutput.getByteBuffers(), message.getId(), new SendMessageCallback<Object>(connection, bsonOutput, message.getId(), callback, receiveCallback));
        }
        catch (Throwable t) {
            callback.onResult(null, t);
        }
    }
    
    @Override
    public void setCommandListener(final CommandListener commandListener) {
        this.commandListener = commandListener;
    }
    
    private String getCommandName() {
        return (this.commandName != null) ? this.commandName : this.command.keySet().iterator().next();
    }
    
    private CommandMessage sendMessage(final InternalConnection connection) {
        final ByteBufferBsonOutput bsonOutput = new ByteBufferBsonOutput(connection);
        try {
            final CommandMessage message = new CommandMessage(this.namespace.getFullName(), this.command, this.slaveOk, this.fieldNameValidator, ProtocolHelper.getMessageSettings(connection.getDescription()));
            final int documentPosition = message.encodeWithMetadata(bsonOutput).getFirstDocumentPosition();
            if (this.commandListener != null) {
                final ByteBufBsonDocument byteBufBsonDocument = ByteBufBsonDocument.createOne(bsonOutput, documentPosition);
                if (byteBufBsonDocument.containsKey("$query")) {
                    final BsonDocument commandDocument = byteBufBsonDocument.getDocument("$query");
                    this.commandName = commandDocument.keySet().iterator().next();
                }
                else {
                    final BsonDocument commandDocument = byteBufBsonDocument;
                    this.commandName = byteBufBsonDocument.getFirstKey();
                }
                BsonDocument commandDocument;
                final BsonDocument commandDocumentForEvent = CommandProtocol.SECURITY_SENSITIVE_COMMANDS.contains(this.commandName) ? new BsonDocument() : commandDocument;
                ProtocolHelper.sendCommandStartedEvent(message, this.namespace.getDatabaseName(), this.commandName, commandDocumentForEvent, connection.getDescription(), this.commandListener);
            }
            connection.sendMessage(bsonOutput.getByteBuffers(), message.getId());
            return message;
        }
        finally {
            bsonOutput.close();
        }
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.command");
        SECURITY_SENSITIVE_COMMANDS = new HashSet<String>(Arrays.asList("authenticate", "saslStart", "saslContinue", "getnonce", "createUser", "updateUser", "copydbgetnonce", "copydbsaslstart", "copydb"));
    }
}
