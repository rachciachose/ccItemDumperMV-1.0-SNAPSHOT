// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.BsonValue;
import org.bson.BsonString;
import com.mongodb.connection.ServerDescription;
import java.util.List;
import com.mongodb.connection.ClusterDescription;
import org.bson.BsonDocument;
import com.mongodb.annotations.Immutable;

@Immutable
public abstract class ReadPreference
{
    private static final ReadPreference PRIMARY;
    private static final ReadPreference SECONDARY;
    private static final ReadPreference SECONDARY_PREFERRED;
    private static final ReadPreference PRIMARY_PREFERRED;
    private static final ReadPreference NEAREST;
    
    public abstract boolean isSlaveOk();
    
    public abstract String getName();
    
    public abstract BsonDocument toDocument();
    
    public abstract List<ServerDescription> choose(final ClusterDescription p0);
    
    public static ReadPreference primary() {
        return ReadPreference.PRIMARY;
    }
    
    public static ReadPreference primaryPreferred() {
        return ReadPreference.PRIMARY_PREFERRED;
    }
    
    public static ReadPreference secondary() {
        return ReadPreference.SECONDARY;
    }
    
    public static ReadPreference secondaryPreferred() {
        return ReadPreference.SECONDARY_PREFERRED;
    }
    
    public static ReadPreference nearest() {
        return ReadPreference.NEAREST;
    }
    
    public static TaggableReadPreference primaryPreferred(final TagSet tagSet) {
        return new TaggableReadPreference.PrimaryPreferredReadPreference(tagSet);
    }
    
    public static TaggableReadPreference secondary(final TagSet tagSet) {
        return new TaggableReadPreference.SecondaryReadPreference(tagSet);
    }
    
    public static TaggableReadPreference secondaryPreferred(final TagSet tagSet) {
        return new TaggableReadPreference.SecondaryPreferredReadPreference(tagSet);
    }
    
    public static TaggableReadPreference nearest(final TagSet tagSet) {
        return new TaggableReadPreference.NearestReadPreference(tagSet);
    }
    
    public static TaggableReadPreference primaryPreferred(final List<TagSet> tagSetList) {
        return new TaggableReadPreference.PrimaryPreferredReadPreference(tagSetList);
    }
    
    public static TaggableReadPreference secondary(final List<TagSet> tagSetList) {
        return new TaggableReadPreference.SecondaryReadPreference(tagSetList);
    }
    
    public static TaggableReadPreference secondaryPreferred(final List<TagSet> tagSetList) {
        return new TaggableReadPreference.SecondaryPreferredReadPreference(tagSetList);
    }
    
    public static TaggableReadPreference nearest(final List<TagSet> tagSetList) {
        return new TaggableReadPreference.NearestReadPreference(tagSetList);
    }
    
    public static ReadPreference valueOf(final String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        final String nameToCheck = name.toLowerCase();
        if (nameToCheck.equals(ReadPreference.PRIMARY.getName().toLowerCase())) {
            return ReadPreference.PRIMARY;
        }
        if (nameToCheck.equals(ReadPreference.SECONDARY.getName().toLowerCase())) {
            return ReadPreference.SECONDARY;
        }
        if (nameToCheck.equals(ReadPreference.SECONDARY_PREFERRED.getName().toLowerCase())) {
            return ReadPreference.SECONDARY_PREFERRED;
        }
        if (nameToCheck.equals(ReadPreference.PRIMARY_PREFERRED.getName().toLowerCase())) {
            return ReadPreference.PRIMARY_PREFERRED;
        }
        if (nameToCheck.equals(ReadPreference.NEAREST.getName().toLowerCase())) {
            return ReadPreference.NEAREST;
        }
        throw new IllegalArgumentException("No match for read preference of " + name);
    }
    
    public static TaggableReadPreference valueOf(final String name, final List<TagSet> tagSetList) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        final String nameToCheck = name.toLowerCase();
        if (nameToCheck.equals(ReadPreference.PRIMARY.getName().toLowerCase())) {
            throw new IllegalArgumentException("Primary read preference can not also specify tag sets");
        }
        if (nameToCheck.equals(ReadPreference.SECONDARY.getName().toLowerCase())) {
            return new TaggableReadPreference.SecondaryReadPreference(tagSetList);
        }
        if (nameToCheck.equals(ReadPreference.SECONDARY_PREFERRED.getName().toLowerCase())) {
            return new TaggableReadPreference.SecondaryPreferredReadPreference(tagSetList);
        }
        if (nameToCheck.equals(ReadPreference.PRIMARY_PREFERRED.getName().toLowerCase())) {
            return new TaggableReadPreference.PrimaryPreferredReadPreference(tagSetList);
        }
        if (nameToCheck.equals(ReadPreference.NEAREST.getName().toLowerCase())) {
            return new TaggableReadPreference.NearestReadPreference(tagSetList);
        }
        throw new IllegalArgumentException("No match for read preference of " + name);
    }
    
    static {
        PRIMARY = new PrimaryReadPreference();
        SECONDARY = new TaggableReadPreference.SecondaryReadPreference();
        SECONDARY_PREFERRED = new TaggableReadPreference.SecondaryPreferredReadPreference();
        PRIMARY_PREFERRED = new TaggableReadPreference.PrimaryPreferredReadPreference();
        NEAREST = new TaggableReadPreference.NearestReadPreference();
    }
    
    private static final class PrimaryReadPreference extends ReadPreference
    {
        @Override
        public boolean isSlaveOk() {
            return false;
        }
        
        @Override
        public String toString() {
            return this.getName();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && this.getClass() == o.getClass();
        }
        
        @Override
        public int hashCode() {
            return this.getName().hashCode();
        }
        
        @Override
        public List<ServerDescription> choose(final ClusterDescription clusterDescription) {
            return clusterDescription.getPrimaries();
        }
        
        @Override
        public BsonDocument toDocument() {
            return new BsonDocument("mode", new BsonString(this.getName()));
        }
        
        @Override
        public String getName() {
            return "primary";
        }
    }
}
