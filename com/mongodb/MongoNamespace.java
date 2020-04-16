// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.assertions.Assertions;
import com.mongodb.annotations.Immutable;

@Immutable
public final class MongoNamespace
{
    public static final String COMMAND_COLLECTION_NAME = "$cmd";
    private final String databaseName;
    private final String collectionName;
    private final String fullName;
    
    public MongoNamespace(final String fullName) {
        Assertions.notNull("fullName", fullName);
        Assertions.isTrueArgument("fullName is of form <db>.<collection>", isFullNameValid(fullName));
        this.databaseName = getDatatabaseNameFromFullName(fullName);
        this.collectionName = getCollectionNameFullName(fullName);
        this.fullName = fullName;
    }
    
    public MongoNamespace(final String databaseName, final String collectionName) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.collectionName = Assertions.notNull("collectionName", collectionName);
        this.fullName = databaseName + "." + collectionName;
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    public String getCollectionName() {
        return this.collectionName;
    }
    
    public String getFullName() {
        return this.fullName;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MongoNamespace that = (MongoNamespace)o;
        return this.collectionName.equals(that.collectionName) && this.databaseName.equals(that.databaseName);
    }
    
    @Override
    public String toString() {
        return this.fullName;
    }
    
    @Override
    public int hashCode() {
        int result = this.databaseName.hashCode();
        result = 31 * result + this.collectionName.hashCode();
        return result;
    }
    
    private static boolean isFullNameValid(final String fullName) {
        final int firstDotIndex = fullName.indexOf(".");
        return firstDotIndex != -1 && firstDotIndex != 0 && fullName.charAt(fullName.length() - 1) != '.' && fullName.charAt(firstDotIndex + 1) != '.';
    }
    
    private static String getCollectionNameFullName(final String namespace) {
        return namespace.substring(namespace.indexOf(46) + 1);
    }
    
    private static String getDatatabaseNameFromFullName(final String namespace) {
        return namespace.substring(0, namespace.indexOf(46));
    }
}
