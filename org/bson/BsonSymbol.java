// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

public class BsonSymbol extends BsonValue
{
    private final String symbol;
    
    public BsonSymbol(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value can not be null");
        }
        this.symbol = value;
    }
    
    @Override
    public BsonType getBsonType() {
        return BsonType.SYMBOL;
    }
    
    public String getSymbol() {
        return this.symbol;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final BsonSymbol symbol1 = (BsonSymbol)o;
        return this.symbol.equals(symbol1.symbol);
    }
    
    @Override
    public int hashCode() {
        return this.symbol.hashCode();
    }
    
    @Override
    public String toString() {
        return this.symbol;
    }
}
