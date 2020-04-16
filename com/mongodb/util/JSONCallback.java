// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.util;

import org.bson.BsonUndefined;
import org.bson.types.Binary;
import javax.xml.bind.DatatypeConverter;
import java.util.UUID;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import com.mongodb.DBRef;
import org.bson.types.Code;
import org.bson.types.CodeWScope;
import com.mongodb.DBObject;
import org.bson.types.BSONTimestamp;
import java.util.regex.Pattern;
import org.bson.BSON;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.types.ObjectId;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.bson.BSONObject;
import org.bson.BasicBSONCallback;

public class JSONCallback extends BasicBSONCallback
{
    private boolean _lastArray;
    public static final String _msDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String _secDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    public JSONCallback() {
        this._lastArray = false;
    }
    
    @Override
    public BSONObject create() {
        return new BasicDBObject();
    }
    
    @Override
    protected BSONObject createList() {
        return new BasicDBList();
    }
    
    @Override
    public void arrayStart(final String name) {
        this._lastArray = true;
        super.arrayStart(name);
    }
    
    @Override
    public void objectStart(final String name) {
        this._lastArray = false;
        super.objectStart(name);
    }
    
    @Override
    public Object objectDone() {
        final String name = this.curName();
        Object o = super.objectDone();
        if (this._lastArray) {
            return o;
        }
        final BSONObject b = (BSONObject)o;
        if (b.containsField("$oid")) {
            o = new ObjectId((String)b.get("$oid"));
        }
        else if (b.containsField("$date")) {
            if (b.get("$date") instanceof Number) {
                o = new Date(((Number)b.get("$date")).longValue());
            }
            else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                format.setCalendar(new GregorianCalendar(new SimpleTimeZone(0, "GMT")));
                o = format.parse(b.get("$date").toString(), new ParsePosition(0));
                if (o == null) {
                    format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    format.setCalendar(new GregorianCalendar(new SimpleTimeZone(0, "GMT")));
                    o = format.parse(b.get("$date").toString(), new ParsePosition(0));
                }
            }
        }
        else if (b.containsField("$regex")) {
            o = Pattern.compile((String)b.get("$regex"), BSON.regexFlags((String)b.get("$options")));
        }
        else if (b.containsField("$ts")) {
            final Integer ts = ((Number)b.get("$ts")).intValue();
            final Integer inc = ((Number)b.get("$inc")).intValue();
            o = new BSONTimestamp(ts, inc);
        }
        else if (b.containsField("$timestamp")) {
            final BSONObject tsObject = (BSONObject)b.get("$timestamp");
            final Integer ts2 = ((Number)tsObject.get("t")).intValue();
            final Integer inc2 = ((Number)tsObject.get("i")).intValue();
            o = new BSONTimestamp(ts2, inc2);
        }
        else if (b.containsField("$code")) {
            if (b.containsField("$scope")) {
                o = new CodeWScope((String)b.get("$code"), (BSONObject)b.get("$scope"));
            }
            else {
                o = new Code((String)b.get("$code"));
            }
        }
        else if (b.containsField("$ref")) {
            o = new DBRef((String)b.get("$ref"), b.get("$id"));
        }
        else if (b.containsField("$minKey")) {
            o = new MinKey();
        }
        else if (b.containsField("$maxKey")) {
            o = new MaxKey();
        }
        else if (b.containsField("$uuid")) {
            o = UUID.fromString((String)b.get("$uuid"));
        }
        else if (b.containsField("$binary")) {
            final int type = (int)b.get("$type");
            final byte[] bytes = DatatypeConverter.parseBase64Binary((String)b.get("$binary"));
            o = new Binary((byte)type, bytes);
        }
        else if (b.containsField("$undefined") && b.get("$undefined").equals(true)) {
            o = new BsonUndefined();
        }
        else if (b.containsField("$numberLong")) {
            o = Long.valueOf((String)b.get("$numberLong"));
        }
        if (!this.isStackEmpty()) {
            this._put(name, o);
        }
        else {
            o = (BSON.hasDecodeHooks() ? BSON.applyDecodingHooks(o) : o);
            this.setRoot(o);
        }
        return o;
    }
}
