// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

public class MongoCursorNotFoundException extends MongoQueryException
{
    private static final long serialVersionUID = -4415279469780082174L;
    private final long cursorId;
    private final ServerAddress serverAddress;
    
    public MongoCursorNotFoundException(final long cursorId, final ServerAddress serverAddress) {
        super(serverAddress, -5, "Cursor " + cursorId + " not found on server " + serverAddress);
        this.cursorId = cursorId;
        this.serverAddress = serverAddress;
    }
    
    public long getCursorId() {
        return this.cursorId;
    }
    
    @Override
    public ServerAddress getServerAddress() {
        return this.serverAddress;
    }
}
