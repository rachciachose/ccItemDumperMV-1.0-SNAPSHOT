// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

public final class BsonInt64 extends BsonNumber implements Comparable<BsonInt64>
{
    private final long value;
    
    public BsonInt64(final long value) {
        this.value = value;
    }
    
    @Override
    public int compareTo(final BsonInt64 o) {
        return (this.value < o.value) ? -1 : ((this.value == o.value) ? 0 : 1);
    }
    
    @Override
    public BsonType getBsonType() {
        return BsonType.INT64;
    }
    
    public long getValue() {
        return this.value;
    }
    
    @Override
    public int intValue() {
        return (int)this.value;
    }
    
    @Override
    public long longValue() {
        return this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final BsonInt64 bsonInt64 = (BsonInt64)o;
        return this.value == bsonInt64.value;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }
    
    @Override
    public String toString() {
        return "BsonInt64{value=" + this.value + '}';
    }
}
