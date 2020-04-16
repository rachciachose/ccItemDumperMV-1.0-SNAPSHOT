// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.FieldNameValidator;
import org.bson.BsonBinaryWriter;
import org.bson.BsonBinaryWriterSettings;
import org.bson.BsonWriterSettings;
import org.bson.io.BsonOutput;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;

abstract class BaseWriteCommandMessage extends RequestMessage
{
    private static final int HEADROOM = 16384;
    private final MongoNamespace writeNamespace;
    private final boolean ordered;
    private final WriteConcern writeConcern;
    
    public BaseWriteCommandMessage(final MongoNamespace writeNamespace, final boolean ordered, final WriteConcern writeConcern, final MessageSettings settings) {
        super(new MongoNamespace(writeNamespace.getDatabaseName(), "$cmd").getFullName(), OpCode.OP_QUERY, settings);
        this.writeNamespace = writeNamespace;
        this.ordered = ordered;
        this.writeConcern = writeConcern;
    }
    
    public MongoNamespace getWriteNamespace() {
        return this.writeNamespace;
    }
    
    public WriteConcern getWriteConcern() {
        return this.writeConcern;
    }
    
    public boolean isOrdered() {
        return this.ordered;
    }
    
    @Override
    public BaseWriteCommandMessage encode(final BsonOutput outputStream) {
        return (BaseWriteCommandMessage)super.encode(outputStream);
    }
    
    public abstract int getItemCount();
    
    @Override
    protected BaseWriteCommandMessage encodeMessageBody(final BsonOutput outputStream, final int messageStartPosition) {
        return (BaseWriteCommandMessage)this.encodeMessageBodyWithMetadata(outputStream, messageStartPosition).getNextMessage();
    }
    
    @Override
    protected EncodingMetadata encodeMessageBodyWithMetadata(final BsonOutput outputStream, final int messageStartPosition) {
        BaseWriteCommandMessage nextMessage = null;
        this.writeCommandHeader(outputStream);
        final int commandStartPosition = outputStream.getPosition();
        final int firstDocumentStartPosition = outputStream.getPosition();
        final BsonBinaryWriter writer = new BsonBinaryWriter(new BsonWriterSettings(), new BsonBinaryWriterSettings(this.getSettings().getMaxDocumentSize() + 16384), outputStream, this.getFieldNameValidator());
        try {
            writer.writeStartDocument();
            this.writeCommandPrologue(writer);
            nextMessage = this.writeTheWrites(outputStream, commandStartPosition, writer);
            writer.writeEndDocument();
        }
        finally {
            writer.close();
        }
        return new EncodingMetadata(nextMessage, firstDocumentStartPosition);
    }
    
    protected abstract FieldNameValidator getFieldNameValidator();
    
    private void writeCommandHeader(final BsonOutput outputStream) {
        outputStream.writeInt32(0);
        outputStream.writeCString(this.getCollectionName());
        outputStream.writeInt32(0);
        outputStream.writeInt32(-1);
    }
    
    protected abstract String getCommandName();
    
    protected abstract BaseWriteCommandMessage writeTheWrites(final BsonOutput p0, final int p1, final BsonBinaryWriter p2);
    
    boolean exceedsLimits(final int batchLength, final int batchItemCount) {
        return this.exceedsBatchLengthLimit(batchLength, batchItemCount) || this.exceedsBatchItemCountLimit(batchItemCount);
    }
    
    private boolean exceedsBatchLengthLimit(final int batchLength, final int batchItemCount) {
        return batchLength > this.getSettings().getMaxDocumentSize() && batchItemCount > 1;
    }
    
    private boolean exceedsBatchItemCountLimit(final int batchItemCount) {
        return batchItemCount > this.getSettings().getMaxBatchCount();
    }
    
    private void writeCommandPrologue(final BsonBinaryWriter writer) {
        writer.writeString(this.getCommandName(), this.getWriteNamespace().getCollectionName());
        writer.writeBoolean("ordered", this.ordered);
        if (!this.getWriteConcern().isServerDefault()) {
            writer.writeName("writeConcern");
            final BsonDocument document = this.getWriteConcern().asDocument();
            this.getCodec(document).encode(writer, document, EncoderContext.builder().build());
        }
    }
}
