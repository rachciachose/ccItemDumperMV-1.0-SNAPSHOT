// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

public enum BsonType
{
    END_OF_DOCUMENT(0), 
    DOUBLE(1), 
    STRING(2), 
    DOCUMENT(3), 
    ARRAY(4), 
    BINARY(5), 
    UNDEFINED(6), 
    OBJECT_ID(7), 
    BOOLEAN(8), 
    DATE_TIME(9), 
    NULL(10), 
    REGULAR_EXPRESSION(11), 
    DB_POINTER(12), 
    JAVASCRIPT(13), 
    SYMBOL(14), 
    JAVASCRIPT_WITH_SCOPE(15), 
    INT32(16), 
    TIMESTAMP(17), 
    INT64(18), 
    MIN_KEY(255), 
    MAX_KEY(127);
    
    private static final BsonType[] LOOKUP_TABLE;
    private final int value;
    
    private BsonType(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public static BsonType findByValue(final int value) {
        return BsonType.LOOKUP_TABLE[value & 0xFF];
    }
    
    public boolean isContainer() {
        return this == BsonType.DOCUMENT || this == BsonType.ARRAY;
    }
    
    static {
        LOOKUP_TABLE = new BsonType[BsonType.MIN_KEY.getValue() + 1];
        for (final BsonType cur : values()) {
            BsonType.LOOKUP_TABLE[cur.getValue()] = cur;
        }
    }
}
