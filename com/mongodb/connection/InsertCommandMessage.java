// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.BsonBinaryWriter;
import org.bson.io.BsonOutput;
import java.util.Collections;
import java.util.Map;
import com.mongodb.internal.validator.MappedFieldNameValidator;
import com.mongodb.internal.validator.NoOpFieldNameValidator;
import com.mongodb.internal.validator.CollectibleDocumentFieldNameValidator;
import java.util.HashMap;
import org.bson.FieldNameValidator;
import com.mongodb.assertions.Assertions;
import com.mongodb.WriteConcern;
import com.mongodb.MongoNamespace;
import com.mongodb.bulk.InsertRequest;
import java.util.List;

class InsertCommandMessage extends BaseWriteCommandMessage
{
    private final List<InsertRequest> insertRequestList;
    
    public InsertCommandMessage(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern, final List<InsertRequest> insertRequestList, final MessageSettings settings) {
        super(namespace, ordered, writeConcern, settings);
        this.insertRequestList = Assertions.notNull("insertRequestList", insertRequestList);
    }
    
    @Override
    public int getItemCount() {
        return this.insertRequestList.size();
    }
    
    @Override
    protected FieldNameValidator getFieldNameValidator() {
        final Map<String, FieldNameValidator> map = new HashMap<String, FieldNameValidator>();
        map.put("documents", new CollectibleDocumentFieldNameValidator());
        return new MappedFieldNameValidator(new NoOpFieldNameValidator(), map);
    }
    
    public List<InsertRequest> getRequests() {
        return Collections.unmodifiableList((List<? extends InsertRequest>)this.insertRequestList);
    }
    
    @Override
    protected String getCommandName() {
        return "insert";
    }
    
    @Override
    protected InsertCommandMessage writeTheWrites(final BsonOutput bsonOutput, final int commandStartPosition, final BsonBinaryWriter writer) {
        InsertCommandMessage nextMessage = null;
        writer.writeStartArray("documents");
        writer.pushMaxDocumentSize(this.getSettings().getMaxDocumentSize());
        for (int i = 0; i < this.insertRequestList.size(); ++i) {
            writer.mark();
            final BsonDocument document = this.insertRequestList.get(i).getDocument();
            this.getCodec(document).encode(writer, document, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
            if (this.exceedsLimits(bsonOutput.getPosition() - commandStartPosition, i + 1)) {
                writer.reset();
                nextMessage = new InsertCommandMessage(this.getWriteNamespace(), this.isOrdered(), this.getWriteConcern(), this.insertRequestList.subList(i, this.insertRequestList.size()), this.getSettings());
                break;
            }
        }
        writer.popMaxDocumentSize();
        writer.writeEndArray();
        return nextMessage;
    }
}
