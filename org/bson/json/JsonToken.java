// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.json;

class JsonToken
{
    private final Object value;
    private final JsonTokenType type;
    
    public JsonToken(final JsonTokenType type, final Object value) {
        this.value = value;
        this.type = type;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public <T> T getValue(final Class<T> clazz) {
        if (Long.class == clazz) {
            if (this.value instanceof Integer) {
                return clazz.cast((long)this.value);
            }
            if (this.value instanceof String) {
                return clazz.cast(Long.valueOf((String)this.value));
            }
        }
        try {
            return clazz.cast(this.value);
        }
        catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public JsonTokenType getType() {
        return this.type;
    }
}
