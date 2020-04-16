// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import org.bson.BsonBoolean;
import org.bson.BsonInt32;
import org.bson.BsonValue;
import org.bson.BsonString;
import org.bson.BsonDocument;
import com.mongodb.assertions.Assertions;
import java.util.Map;
import com.mongodb.annotations.Immutable;
import java.io.Serializable;

@Immutable
public class WriteConcern implements Serializable
{
    private static final long serialVersionUID = 1884671104750417011L;
    private static final Map<String, WriteConcern> NAMED_CONCERNS;
    private final Object w;
    private final int wtimeout;
    private final boolean fsync;
    private final boolean j;
    public static final WriteConcern ACKNOWLEDGED;
    public static final WriteConcern UNACKNOWLEDGED;
    public static final WriteConcern FSYNCED;
    public static final WriteConcern JOURNALED;
    public static final WriteConcern REPLICA_ACKNOWLEDGED;
    public static final WriteConcern NORMAL;
    public static final WriteConcern SAFE;
    public static final WriteConcern MAJORITY;
    public static final WriteConcern FSYNC_SAFE;
    public static final WriteConcern JOURNAL_SAFE;
    public static final WriteConcern REPLICAS_SAFE;
    
    public WriteConcern() {
        this(0);
    }
    
    public WriteConcern(final int w) {
        this(w, 0, false);
    }
    
    public WriteConcern(final String w) {
        this(w, 0, false, false);
    }
    
    public WriteConcern(final int w, final int wtimeout) {
        this(w, wtimeout, false);
    }
    
    public WriteConcern(final boolean fsync) {
        this(1, 0, fsync);
    }
    
    public WriteConcern(final int w, final int wtimeout, final boolean fsync) {
        this(w, wtimeout, fsync, false);
    }
    
    public WriteConcern(final int w, final int wtimeout, final boolean fsync, final boolean j) {
        Assertions.isTrueArgument("w >= 0", w >= 0);
        this.w = w;
        this.wtimeout = wtimeout;
        this.fsync = fsync;
        this.j = j;
    }
    
    public WriteConcern(final String w, final int wtimeout, final boolean fsync, final boolean j) {
        this.w = Assertions.notNull("w", w);
        this.wtimeout = wtimeout;
        this.fsync = fsync;
        this.j = j;
    }
    
    public Object getWObject() {
        return this.w;
    }
    
    public int getW() {
        return (int)this.w;
    }
    
    public String getWString() {
        return (String)this.w;
    }
    
    public int getWtimeout() {
        return this.wtimeout;
    }
    
    public boolean getFsync() {
        return this.fsync();
    }
    
    public boolean fsync() {
        return this.fsync;
    }
    
    public boolean callGetLastError() {
        return this.isAcknowledged();
    }
    
    public boolean isServerDefault() {
        return this.w.equals(1) && this.wtimeout == 0 && !this.fsync && !this.j;
    }
    
    public BsonDocument asDocument() {
        final BsonDocument document = new BsonDocument();
        this.addW(document);
        this.addWTimeout(document);
        this.addFSync(document);
        this.addJ(document);
        return document;
    }
    
    public boolean isAcknowledged() {
        if (this.w instanceof Integer) {
            return (int)this.w > 0;
        }
        return this.w != null;
    }
    
    public static WriteConcern valueOf(final String name) {
        return WriteConcern.NAMED_CONCERNS.get(name.toLowerCase());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final WriteConcern that = (WriteConcern)o;
        return this.fsync == that.fsync && this.j == that.j && this.wtimeout == that.wtimeout && this.w.equals(that.w);
    }
    
    @Override
    public int hashCode() {
        int result = this.w.hashCode();
        result = 31 * result + this.wtimeout;
        result = 31 * result + (this.fsync ? 1 : 0);
        result = 31 * result + (this.j ? 1 : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "WriteConcern{w=" + this.w + ", wtimeout=" + this.wtimeout + ", fsync=" + this.fsync + ", j=" + this.j;
    }
    
    public boolean getJ() {
        return this.j;
    }
    
    public WriteConcern withW(final int w) {
        return new WriteConcern(w, this.getWtimeout(), this.getFsync(), this.getJ());
    }
    
    public WriteConcern withW(final String w) {
        return new WriteConcern(w, this.getWtimeout(), this.getFsync(), this.getJ());
    }
    
    public WriteConcern withFsync(final boolean fsync) {
        if (this.getWObject() instanceof Integer) {
            return new WriteConcern(this.getW(), this.getWtimeout(), fsync, this.getJ());
        }
        return new WriteConcern(this.getWString(), this.getWtimeout(), fsync, this.getJ());
    }
    
    public WriteConcern withJ(final boolean j) {
        if (this.getWObject() instanceof Integer) {
            return new WriteConcern(this.getW(), this.getWtimeout(), this.getFsync(), j);
        }
        return new WriteConcern(this.getWString(), this.getWtimeout(), this.getFsync(), j);
    }
    
    private void addW(final BsonDocument document) {
        if (this.w instanceof String) {
            document.put("w", new BsonString((String)this.w));
        }
        else {
            document.put("w", new BsonInt32((int)this.w));
        }
    }
    
    private void addJ(final BsonDocument document) {
        if (this.j) {
            document.put("j", BsonBoolean.TRUE);
        }
    }
    
    private void addFSync(final BsonDocument document) {
        if (this.fsync) {
            document.put("fsync", BsonBoolean.TRUE);
        }
    }
    
    private void addWTimeout(final BsonDocument document) {
        if (this.wtimeout > 0) {
            document.put("wtimeout", new BsonInt32(this.wtimeout));
        }
    }
    
    public static Majority majorityWriteConcern(final int wtimeout, final boolean fsync, final boolean j) {
        return new Majority(wtimeout, fsync, j);
    }
    
    static {
        ACKNOWLEDGED = new WriteConcern(1);
        UNACKNOWLEDGED = new WriteConcern(0);
        FSYNCED = new WriteConcern(true);
        JOURNALED = new WriteConcern(1, 0, false, true);
        REPLICA_ACKNOWLEDGED = new WriteConcern(2);
        NORMAL = WriteConcern.UNACKNOWLEDGED;
        SAFE = WriteConcern.ACKNOWLEDGED;
        MAJORITY = new WriteConcern("majority");
        FSYNC_SAFE = WriteConcern.FSYNCED;
        JOURNAL_SAFE = WriteConcern.JOURNALED;
        REPLICAS_SAFE = WriteConcern.REPLICA_ACKNOWLEDGED;
        NAMED_CONCERNS = new HashMap<String, WriteConcern>();
        for (final Field f : WriteConcern.class.getFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.getType().equals(WriteConcern.class)) {
                final String key = f.getName().toLowerCase();
                try {
                    WriteConcern.NAMED_CONCERNS.put(key, (WriteConcern)f.get(null));
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public static class Majority extends WriteConcern
    {
        private static final long serialVersionUID = -4128295115883875212L;
        
        public Majority() {
            this(0, false, false);
        }
        
        public Majority(final int wtimeout, final boolean fsync, final boolean j) {
            super("majority", wtimeout, fsync, j);
        }
    }
}
