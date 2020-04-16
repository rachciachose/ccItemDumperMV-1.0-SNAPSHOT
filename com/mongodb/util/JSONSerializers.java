// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.util;

import org.bson.BSONObject;
import java.util.HashMap;
import javax.xml.bind.DatatypeConverter;
import org.bson.BSON;
import java.util.Iterator;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import com.mongodb.BasicDBObject;
import java.lang.reflect.Array;
import org.bson.BsonUndefined;
import java.util.UUID;
import org.bson.types.Symbol;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.bson.types.MinKey;
import org.bson.types.MaxKey;
import java.util.Map;
import com.mongodb.DBRef;
import com.mongodb.DBObject;
import org.bson.types.CodeWScope;
import org.bson.types.Code;
import org.bson.types.Binary;
import org.bson.types.BSONTimestamp;
import java.util.Date;

public class JSONSerializers
{
    public static ObjectSerializer getLegacy() {
        final ClassMapBasedObjectSerializer serializer = addCommonSerializers();
        serializer.addObjectSerializer(Date.class, new LegacyDateSerializer(serializer));
        serializer.addObjectSerializer(BSONTimestamp.class, new LegacyBSONTimestampSerializer(serializer));
        serializer.addObjectSerializer(Binary.class, new LegacyBinarySerializer());
        serializer.addObjectSerializer(byte[].class, new LegacyBinarySerializer());
        return serializer;
    }
    
    public static ObjectSerializer getStrict() {
        final ClassMapBasedObjectSerializer serializer = addCommonSerializers();
        serializer.addObjectSerializer(Date.class, new DateSerializer(serializer));
        serializer.addObjectSerializer(BSONTimestamp.class, new BSONTimestampSerializer(serializer));
        serializer.addObjectSerializer(Binary.class, new BinarySerializer(serializer));
        serializer.addObjectSerializer(byte[].class, new ByteArraySerializer(serializer));
        return serializer;
    }
    
    static ClassMapBasedObjectSerializer addCommonSerializers() {
        final ClassMapBasedObjectSerializer serializer = new ClassMapBasedObjectSerializer();
        serializer.addObjectSerializer(Object[].class, new ObjectArraySerializer(serializer));
        serializer.addObjectSerializer(Boolean.class, new ToStringSerializer());
        serializer.addObjectSerializer(Code.class, new CodeSerializer(serializer));
        serializer.addObjectSerializer(CodeWScope.class, new CodeWScopeSerializer(serializer));
        serializer.addObjectSerializer(DBObject.class, new DBObjectSerializer(serializer));
        serializer.addObjectSerializer(DBRef.class, new DBRefBaseSerializer(serializer));
        serializer.addObjectSerializer(Iterable.class, new IterableSerializer(serializer));
        serializer.addObjectSerializer(Map.class, new MapSerializer(serializer));
        serializer.addObjectSerializer(MaxKey.class, new MaxKeySerializer(serializer));
        serializer.addObjectSerializer(MinKey.class, new MinKeySerializer(serializer));
        serializer.addObjectSerializer(Number.class, new ToStringSerializer());
        serializer.addObjectSerializer(ObjectId.class, new ObjectIdSerializer(serializer));
        serializer.addObjectSerializer(Pattern.class, new PatternSerializer(serializer));
        serializer.addObjectSerializer(String.class, new StringSerializer());
        serializer.addObjectSerializer(Symbol.class, new SymbolSerializer(serializer));
        serializer.addObjectSerializer(UUID.class, new UuidSerializer(serializer));
        serializer.addObjectSerializer(BsonUndefined.class, new UndefinedSerializer(serializer));
        return serializer;
    }
    
    private abstract static class CompoundObjectSerializer extends AbstractObjectSerializer
    {
        protected final ObjectSerializer serializer;
        
        CompoundObjectSerializer(final ObjectSerializer serializer) {
            this.serializer = serializer;
        }
    }
    
    private static class LegacyBinarySerializer extends AbstractObjectSerializer
    {
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            buf.append("<Binary Data>");
        }
    }
    
    private static class ObjectArraySerializer extends CompoundObjectSerializer
    {
        ObjectArraySerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            buf.append("[ ");
            for (int i = 0; i < Array.getLength(obj); ++i) {
                if (i > 0) {
                    buf.append(" , ");
                }
                this.serializer.serialize(Array.get(obj, i), buf);
            }
            buf.append("]");
        }
    }
    
    private static class ToStringSerializer extends AbstractObjectSerializer
    {
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            buf.append(obj.toString());
        }
    }
    
    private static class LegacyBSONTimestampSerializer extends CompoundObjectSerializer
    {
        LegacyBSONTimestampSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final BSONTimestamp t = (BSONTimestamp)obj;
            final BasicDBObject temp = new BasicDBObject();
            ((HashMap<String, Integer>)temp).put("$ts", t.getTime());
            ((HashMap<String, Integer>)temp).put("$inc", t.getInc());
            this.serializer.serialize(temp, buf);
        }
    }
    
    private static class CodeSerializer extends CompoundObjectSerializer
    {
        CodeSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final Code c = (Code)obj;
            final BasicDBObject temp = new BasicDBObject();
            ((HashMap<String, String>)temp).put("$code", c.getCode());
            this.serializer.serialize(temp, buf);
        }
    }
    
    private static class CodeWScopeSerializer extends CompoundObjectSerializer
    {
        CodeWScopeSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final CodeWScope c = (CodeWScope)obj;
            final BasicDBObject temp = new BasicDBObject();
            ((HashMap<String, String>)temp).put("$code", c.getCode());
            ((HashMap<String, BSONObject>)temp).put("$scope", c.getScope());
            this.serializer.serialize(temp, buf);
        }
    }
    
    private static class LegacyDateSerializer extends CompoundObjectSerializer
    {
        LegacyDateSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final Date d = (Date)obj;
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format.setCalendar(new GregorianCalendar(new SimpleTimeZone(0, "GMT")));
            this.serializer.serialize(new BasicDBObject("$date", format.format(d)), buf);
        }
    }
    
    private static class DBObjectSerializer extends CompoundObjectSerializer
    {
        DBObjectSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            boolean first = true;
            buf.append("{ ");
            final DBObject dbo = (DBObject)obj;
            for (final String name : dbo.keySet()) {
                final String s = name;
                if (first) {
                    first = false;
                }
                else {
                    buf.append(" , ");
                }
                JSON.string(buf, name);
                buf.append(" : ");
                this.serializer.serialize(dbo.get(name), buf);
            }
            buf.append("}");
        }
    }
    
    private static class DBRefBaseSerializer extends CompoundObjectSerializer
    {
        DBRefBaseSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final DBRef ref = (DBRef)obj;
            final BasicDBObject temp = new BasicDBObject();
            ((HashMap<String, String>)temp).put("$ref", ref.getCollectionName());
            temp.put("$id", ref.getId());
            this.serializer.serialize(temp, buf);
        }
    }
    
    private static class IterableSerializer extends CompoundObjectSerializer
    {
        IterableSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            boolean first = true;
            buf.append("[ ");
            for (final Object o : (Iterable)obj) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(" , ");
                }
                this.serializer.serialize(o, buf);
            }
            buf.append("]");
        }
    }
    
    private static class MapSerializer extends CompoundObjectSerializer
    {
        MapSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            boolean first = true;
            buf.append("{ ");
            final Map m = (Map)obj;
            for (final Object o : m.entrySet()) {
                final Map.Entry entry = (Map.Entry)o;
                if (first) {
                    first = false;
                }
                else {
                    buf.append(" , ");
                }
                JSON.string(buf, entry.getKey().toString());
                buf.append(" : ");
                this.serializer.serialize(entry.getValue(), buf);
            }
            buf.append("}");
        }
    }
    
    private static class MaxKeySerializer extends CompoundObjectSerializer
    {
        MaxKeySerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            this.serializer.serialize(new BasicDBObject("$maxKey", 1), buf);
        }
    }
    
    private static class MinKeySerializer extends CompoundObjectSerializer
    {
        MinKeySerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            this.serializer.serialize(new BasicDBObject("$minKey", 1), buf);
        }
    }
    
    private static class ObjectIdSerializer extends CompoundObjectSerializer
    {
        ObjectIdSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            this.serializer.serialize(new BasicDBObject("$oid", obj.toString()), buf);
        }
    }
    
    private static class PatternSerializer extends CompoundObjectSerializer
    {
        PatternSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final DBObject externalForm = new BasicDBObject();
            externalForm.put("$regex", obj.toString());
            if (((Pattern)obj).flags() != 0) {
                externalForm.put("$options", BSON.regexFlags(((Pattern)obj).flags()));
            }
            this.serializer.serialize(externalForm, buf);
        }
    }
    
    private static class StringSerializer extends AbstractObjectSerializer
    {
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            JSON.string(buf, (String)obj);
        }
    }
    
    private static class SymbolSerializer extends CompoundObjectSerializer
    {
        SymbolSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final Symbol symbol = (Symbol)obj;
            final BasicDBObject temp = new BasicDBObject();
            ((HashMap<String, String>)temp).put("$symbol", symbol.toString());
            this.serializer.serialize(temp, buf);
        }
    }
    
    private static class UuidSerializer extends CompoundObjectSerializer
    {
        UuidSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final UUID uuid = (UUID)obj;
            final BasicDBObject temp = new BasicDBObject();
            ((HashMap<String, String>)temp).put("$uuid", uuid.toString());
            this.serializer.serialize(temp, buf);
        }
    }
    
    private static class BSONTimestampSerializer extends CompoundObjectSerializer
    {
        BSONTimestampSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final BSONTimestamp t = (BSONTimestamp)obj;
            final BasicDBObject temp = new BasicDBObject();
            ((HashMap<String, Integer>)temp).put("t", t.getTime());
            ((HashMap<String, Integer>)temp).put("i", t.getInc());
            final BasicDBObject timestampObj = new BasicDBObject();
            ((HashMap<String, BasicDBObject>)timestampObj).put("$timestamp", temp);
            this.serializer.serialize(timestampObj, buf);
        }
    }
    
    private static class DateSerializer extends CompoundObjectSerializer
    {
        DateSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final Date d = (Date)obj;
            this.serializer.serialize(new BasicDBObject("$date", d.getTime()), buf);
        }
    }
    
    private abstract static class BinarySerializerBase extends CompoundObjectSerializer
    {
        BinarySerializerBase(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        protected void serialize(final byte[] bytes, final byte type, final StringBuilder buf) {
            final DBObject temp = new BasicDBObject();
            temp.put("$binary", DatatypeConverter.printBase64Binary(bytes));
            temp.put("$type", type);
            this.serializer.serialize(temp, buf);
        }
    }
    
    private static class BinarySerializer extends BinarySerializerBase
    {
        BinarySerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final Binary bin = (Binary)obj;
            this.serialize(bin.getData(), bin.getType(), buf);
        }
    }
    
    private static class ByteArraySerializer extends BinarySerializerBase
    {
        ByteArraySerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            this.serialize((byte[])obj, (byte)0, buf);
        }
    }
    
    private static class UndefinedSerializer extends CompoundObjectSerializer
    {
        UndefinedSerializer(final ObjectSerializer serializer) {
            super(serializer);
        }
        
        @Override
        public void serialize(final Object obj, final StringBuilder buf) {
            final BasicDBObject temp = new BasicDBObject();
            ((HashMap<String, Boolean>)temp).put("$undefined", true);
            this.serializer.serialize(temp, buf);
        }
    }
}
