// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.types;

import java.io.Serializable;

public final class MinKey implements Serializable
{
    private static final long serialVersionUID = 4075901136671855684L;
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof MinKey;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public String toString() {
        return "MinKey";
    }
}
