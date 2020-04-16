// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.types;

import java.io.Serializable;

public class Code implements Serializable
{
    private static final long serialVersionUID = 475535263314046697L;
    private final String code;
    
    public Code(final String code) {
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Code code1 = (Code)o;
        return this.code.equals(code1.code);
    }
    
    @Override
    public int hashCode() {
        return this.code.hashCode();
    }
    
    @Override
    public String toString() {
        return "Code{code='" + this.code + '\'' + '}';
    }
}
