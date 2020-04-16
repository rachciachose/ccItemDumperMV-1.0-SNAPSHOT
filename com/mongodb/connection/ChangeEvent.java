// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.assertions.Assertions;
import com.mongodb.annotations.Beta;

@Beta
public class ChangeEvent<T>
{
    private final T oldValue;
    private final T newValue;
    
    public ChangeEvent(final T oldValue, final T newValue) {
        this.oldValue = Assertions.notNull("oldValue", oldValue);
        this.newValue = Assertions.notNull("newValue", newValue);
    }
    
    public T getOldValue() {
        return this.oldValue;
    }
    
    public T getNewValue() {
        return this.newValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ChangeEvent<?> that = (ChangeEvent<?>)o;
        if (!this.newValue.equals(that.newValue)) {
            return false;
        }
        if (this.oldValue != null) {
            if (this.oldValue.equals(that.oldValue)) {
                return true;
            }
        }
        else if (that.oldValue == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.oldValue != null) ? this.oldValue.hashCode() : 0;
        result = 31 * result + this.newValue.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "ChangeEvent{oldValue=" + this.oldValue + ", newValue=" + this.newValue + '}';
    }
}
