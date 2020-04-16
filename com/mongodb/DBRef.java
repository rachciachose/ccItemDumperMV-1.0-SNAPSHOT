// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.assertions.Assertions;
import java.io.Serializable;

public class DBRef implements Serializable
{
    private static final long serialVersionUID = -849581217713362618L;
    private final Object id;
    private final String collectionName;
    
    public DBRef(final String collectionName, final Object id) {
        this.id = Assertions.notNull("id", id);
        this.collectionName = Assertions.notNull("ns", collectionName);
    }
    
    public Object getId() {
        return this.id;
    }
    
    public String getCollectionName() {
        return this.collectionName;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DBRef dbRef = (DBRef)o;
        return this.collectionName.equals(dbRef.collectionName) && this.id.equals(dbRef.id);
    }
    
    @Override
    public int hashCode() {
        int result = this.id.hashCode();
        result = 31 * result + this.collectionName.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "{ \"$ref\" : \"" + this.collectionName + "\", \"$id\" : \"" + this.id + "\" }";
    }
}
