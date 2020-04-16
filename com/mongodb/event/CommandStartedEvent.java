// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.connection.ConnectionDescription;
import org.bson.BsonDocument;

public final class CommandStartedEvent extends CommandEvent
{
    private final String databaseName;
    private final BsonDocument command;
    
    public CommandStartedEvent(final int requestId, final ConnectionDescription connectionDescription, final String databaseName, final String commandName, final BsonDocument command) {
        super(requestId, connectionDescription, commandName);
        this.command = command;
        this.databaseName = databaseName;
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    public BsonDocument getCommand() {
        return this.command;
    }
}
