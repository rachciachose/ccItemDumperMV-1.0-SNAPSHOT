// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.internal.validator.UpdateFieldNameValidator;
import com.mongodb.internal.validator.CollectibleDocumentFieldNameValidator;
import java.util.Map;
import com.mongodb.internal.validator.MappedFieldNameValidator;
import com.mongodb.internal.validator.NoOpFieldNameValidator;
import java.util.HashMap;
import org.bson.FieldNameValidator;
import com.mongodb.bulk.WriteRequest;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.BsonBinaryWriter;
import org.bson.io.BsonOutput;
import java.util.Collections;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.bulk.UpdateRequest;
import java.util.List;

class UpdateCommandMessage extends BaseWriteCommandMessage
{
    private final List<UpdateRequest> updates;
    
    public UpdateCommandMessage(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<UpdateRequest> updates, final MessageSettings settings) {
        super(namespace, ordered, writeConcern, settings);
        this.updates = updates;
    }
    
    public List<UpdateRequest> getRequests() {
        return Collections.unmodifiableList((List<? extends UpdateRequest>)this.updates);
    }
    
    @Override
    protected UpdateCommandMessage writeTheWrites(final BsonOutput bsonOutput, final int commandStartPosition, final BsonBinaryWriter writer) {
        UpdateCommandMessage nextMessage = null;
        writer.writeStartArray("updates");
        for (int i = 0; i < this.updates.size(); ++i) {
            writer.mark();
            final UpdateRequest update = this.updates.get(i);
            writer.writeStartDocument();
            writer.pushMaxDocumentSize(this.getSettings().getMaxDocumentSize());
            writer.writeName("q");
            this.getCodec(update.getFilter()).encode(writer, update.getFilter(), EncoderContext.builder().build());
            writer.writeName("u");
            final int bufferPosition = bsonOutput.getPosition();
            this.getCodec(update.getUpdate()).encode(writer, update.getUpdate(), EncoderContext.builder().build());
            if (update.getType() == WriteRequest.Type.UPDATE && bsonOutput.getPosition() == bufferPosition + 8) {
                throw new IllegalArgumentException("Invalid BSON document for an update");
            }
            if (update.isMulti()) {
                writer.writeBoolean("multi", update.isMulti());
            }
            if (update.isUpsert()) {
                writer.writeBoolean("upsert", update.isUpsert());
            }
            writer.popMaxDocumentSize();
            writer.writeEndDocument();
            if (this.exceedsLimits(bsonOutput.getPosition() - commandStartPosition, i + 1)) {
                writer.reset();
                nextMessage = new UpdateCommandMessage(this.getWriteNamespace(), this.isOrdered(), this.getWriteConcern(), this.updates.subList(i, this.updates.size()), this.getSettings());
                break;
            }
        }
        writer.writeEndArray();
        return nextMessage;
    }
    
    @Override
    public int getItemCount() {
        return this.updates.size();
    }
    
    @Override
    protected FieldNameValidator getFieldNameValidator() {
        final Map<String, FieldNameValidator> rootMap = new HashMap<String, FieldNameValidator>();
        rootMap.put("updates", new UpdatesValidator());
        return new MappedFieldNameValidator(new NoOpFieldNameValidator(), rootMap);
    }
    
    @Override
    protected String getCommandName() {
        return "update";
    }
    
    private class UpdatesValidator implements FieldNameValidator
    {
        private int i;
        
        private UpdatesValidator() {
            this.i = 0;
        }
        
        @Override
        public boolean validate(final String fieldName) {
            return true;
        }
        
        @Override
        public FieldNameValidator getValidatorForField(final String fieldName) {
            if (!fieldName.equals("u")) {
                return new NoOpFieldNameValidator();
            }
            final UpdateRequest updateRequest = UpdateCommandMessage.this.getRequests().get(this.i);
            ++this.i;
            if (updateRequest.getType() == WriteRequest.Type.REPLACE) {
                return new CollectibleDocumentFieldNameValidator();
            }
            return new UpdateFieldNameValidator();
        }
    }
}
