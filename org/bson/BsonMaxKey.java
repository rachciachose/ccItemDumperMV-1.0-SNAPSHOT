// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

public final class BsonMaxKey extends BsonValue
{
    @Override
    public BsonType getBsonType() {
        return BsonType.MAX_KEY;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof BsonMaxKey;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public String toString() {
        return "BsonMaxKey";
    }
}
