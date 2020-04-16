// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.util.Iterator;
import org.bson.io.BsonOutput;
import com.mongodb.assertions.Assertions;
import java.util.List;

class KillCursorsMessage extends RequestMessage
{
    private final List<Long> cursors;
    
    public KillCursorsMessage(final List<Long> cursors) {
        super(OpCode.OP_KILL_CURSORS, MessageSettings.builder().build());
        this.cursors = Assertions.notNull("cursors", cursors);
    }
    
    @Override
    protected RequestMessage encodeMessageBody(final BsonOutput bsonOutput, final int messageStartPosition) {
        return this.encodeMessageBodyWithMetadata(bsonOutput, messageStartPosition).getNextMessage();
    }
    
    @Override
    protected EncodingMetadata encodeMessageBodyWithMetadata(final BsonOutput bsonOutput, final int messageStartPosition) {
        this.writeKillCursorsPrologue(this.cursors.size(), bsonOutput);
        for (final Long cur : this.cursors) {
            bsonOutput.writeInt64(cur);
        }
        return new EncodingMetadata(null, bsonOutput.getPosition());
    }
    
    private void writeKillCursorsPrologue(final int numCursors, final BsonOutput bsonOutput) {
        bsonOutput.writeInt32(0);
        bsonOutput.writeInt32(numCursors);
    }
}
