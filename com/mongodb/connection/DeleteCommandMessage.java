// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.BsonBinaryWriter;
import org.bson.io.BsonOutput;
import java.util.Collections;
import com.mongodb.internal.validator.NoOpFieldNameValidator;
import org.bson.FieldNameValidator;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.bulk.DeleteRequest;
import java.util.List;

class DeleteCommandMessage extends BaseWriteCommandMessage
{
    private final List<DeleteRequest> deletes;
    
    public DeleteCommandMessage(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<DeleteRequest> deletes, final MessageSettings settings) {
        super(namespace, ordered, writeConcern, settings);
        this.deletes = deletes;
    }
    
    @Override
    public int getItemCount() {
        return this.deletes.size();
    }
    
    @Override
    protected FieldNameValidator getFieldNameValidator() {
        return new NoOpFieldNameValidator();
    }
    
    public List<DeleteRequest> getRequests() {
        return Collections.unmodifiableList((List<? extends DeleteRequest>)this.deletes);
    }
    
    @Override
    protected String getCommandName() {
        return "delete";
    }
    
    @Override
    protected BaseWriteCommandMessage writeTheWrites(final BsonOutput bsonOutput, final int commandStartPosition, final BsonBinaryWriter writer) {
        DeleteCommandMessage nextMessage = null;
        writer.writeStartArray("deletes");
        for (int i = 0; i < this.deletes.size(); ++i) {
            writer.mark();
            final DeleteRequest deleteRequest = this.deletes.get(i);
            writer.writeStartDocument();
            writer.pushMaxDocumentSize(this.getSettings().getMaxDocumentSize());
            writer.writeName("q");
            this.getCodec(deleteRequest.getFilter()).encode(writer, deleteRequest.getFilter(), EncoderContext.builder().build());
            writer.writeInt32("limit", deleteRequest.isMulti() ? 0 : 1);
            writer.popMaxDocumentSize();
            writer.writeEndDocument();
            if (this.exceedsLimits(bsonOutput.getPosition() - commandStartPosition, i + 1)) {
                writer.reset();
                nextMessage = new DeleteCommandMessage(this.getWriteNamespace(), this.isOrdered(), this.getWriteConcern(), this.deletes.subList(i, this.deletes.size()), this.getSettings());
                break;
            }
        }
        writer.writeEndArray();
        return nextMessage;
    }
}
