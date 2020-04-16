// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.codecs.EncoderContext;
import org.bson.BsonWriter;
import org.bson.codecs.Encoder;

class DBEncoderFactoryAdapter implements Encoder<DBObject>
{
    private final DBEncoderFactory encoderFactory;
    
    public DBEncoderFactoryAdapter(final DBEncoderFactory encoderFactory) {
        this.encoderFactory = encoderFactory;
    }
    
    @Override
    public void encode(final BsonWriter writer, final DBObject value, final EncoderContext encoderContext) {
        new DBEncoderAdapter(this.encoderFactory.create()).encode(writer, value, encoderContext);
    }
    
    @Override
    public Class<DBObject> getEncoderClass() {
        return DBObject.class;
    }
}
