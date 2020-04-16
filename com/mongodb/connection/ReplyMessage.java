// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.util.ArrayList;
import com.mongodb.MongoInternalException;
import org.bson.io.BsonInput;
import org.bson.BsonReader;
import org.bson.codecs.DecoderContext;
import org.bson.BsonBinaryReader;
import org.bson.io.ByteBufferBsonInput;
import org.bson.codecs.Decoder;
import java.util.List;

class ReplyMessage<T>
{
    private final ReplyHeader replyHeader;
    private final List<T> documents;
    
    public ReplyMessage(final ResponseBuffers responseBuffers, final Decoder<T> decoder, final long requestId) {
        this(responseBuffers.getReplyHeader(), requestId);
        if (this.replyHeader.getNumberReturned() > 0) {
            final BsonInput bsonInput = new ByteBufferBsonInput(responseBuffers.getBodyByteBuffer());
            while (this.documents.size() < this.replyHeader.getNumberReturned()) {
                final BsonBinaryReader reader = new BsonBinaryReader(bsonInput);
                try {
                    this.documents.add(decoder.decode(reader, DecoderContext.builder().build()));
                }
                finally {
                    reader.close();
                }
            }
        }
    }
    
    ReplyMessage(final ReplyHeader replyHeader, final long requestId) {
        if (requestId != replyHeader.getResponseTo()) {
            throw new MongoInternalException(String.format("The responseTo (%d) in the response does not match the requestId (%d) in the request", replyHeader.getResponseTo(), requestId));
        }
        this.replyHeader = replyHeader;
        this.documents = new ArrayList<T>(replyHeader.getNumberReturned());
    }
    
    public ReplyHeader getReplyHeader() {
        return this.replyHeader;
    }
    
    public List<T> getDocuments() {
        return this.documents;
    }
}
