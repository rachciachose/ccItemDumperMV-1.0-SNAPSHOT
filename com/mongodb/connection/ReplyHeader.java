// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.MongoInternalException;
import org.bson.io.BsonInput;

class ReplyHeader
{
    public static final int REPLY_HEADER_LENGTH = 36;
    private static final int CURSOR_NOT_FOUND_RESPONSE_FLAG = 1;
    private static final int QUERY_FAILURE_RESPONSE_FLAG = 2;
    private static final int OP_REPLY_OP_CODE = 1;
    private final int messageLength;
    private final int requestId;
    private final int responseTo;
    private final int responseFlags;
    private final long cursorId;
    private final int startingFrom;
    private final int numberReturned;
    
    public ReplyHeader(final BsonInput header) {
        this.messageLength = header.readInt32();
        this.requestId = header.readInt32();
        this.responseTo = header.readInt32();
        final int opCode = header.readInt32();
        if (opCode != 1) {
            throw new MongoInternalException(String.format("The opCode (%d) in the response does not match the expected opCode (%d)", opCode, 1));
        }
        this.responseFlags = header.readInt32();
        this.cursorId = header.readInt64();
        this.startingFrom = header.readInt32();
        this.numberReturned = header.readInt32();
    }
    
    public int getMessageLength() {
        return this.messageLength;
    }
    
    public int getRequestId() {
        return this.requestId;
    }
    
    public int getResponseTo() {
        return this.responseTo;
    }
    
    public int getResponseFlags() {
        return this.responseFlags;
    }
    
    public long getCursorId() {
        return this.cursorId;
    }
    
    public int getStartingFrom() {
        return this.startingFrom;
    }
    
    public int getNumberReturned() {
        return this.numberReturned;
    }
    
    public boolean isCursorNotFound() {
        return (this.responseFlags & 0x1) == 0x1;
    }
    
    public boolean isQueryFailure() {
        return (this.responseFlags & 0x2) == 0x2;
    }
}
