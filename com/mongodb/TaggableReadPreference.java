// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.connection.ServerDescription;
import com.mongodb.connection.ClusterDescription;
import org.bson.BsonArray;
import java.util.Collections;
import org.bson.BsonValue;
import org.bson.BsonString;
import org.bson.BsonDocument;
import java.util.Iterator;
import com.mongodb.assertions.Assertions;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.annotations.Immutable;

@Immutable
public abstract class TaggableReadPreference extends ReadPreference
{
    private final List<TagSet> tagSetList;
    
    TaggableReadPreference() {
        this.tagSetList = new ArrayList<TagSet>();
    }
    
    TaggableReadPreference(final TagSet tagSet) {
        (this.tagSetList = new ArrayList<TagSet>()).add(tagSet);
    }
    
    TaggableReadPreference(final List<TagSet> tagSetList) {
        this.tagSetList = new ArrayList<TagSet>();
        Assertions.notNull("tagSetList", tagSetList);
        for (final TagSet tagSet : tagSetList) {
            this.tagSetList.add(tagSet);
        }
    }
    
    @Override
    public boolean isSlaveOk() {
        return true;
    }
    
    @Override
    public BsonDocument toDocument() {
        final BsonDocument readPrefObject = new BsonDocument("mode", new BsonString(this.getName()));
        if (!this.tagSetList.isEmpty()) {
            readPrefObject.put("tags", this.tagsListToBsonArray());
        }
        return readPrefObject;
    }
    
    public List<TagSet> getTagSetList() {
        return Collections.unmodifiableList((List<? extends TagSet>)this.tagSetList);
    }
    
    @Override
    public String toString() {
        return this.getName() + (this.tagSetList.isEmpty() ? "" : (": " + this.tagSetList));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TaggableReadPreference that = (TaggableReadPreference)o;
        return this.tagSetList.equals(that.tagSetList);
    }
    
    @Override
    public int hashCode() {
        int result = this.tagSetList.hashCode();
        result = 31 * result + this.getName().hashCode();
        return result;
    }
    
    private BsonArray tagsListToBsonArray() {
        final BsonArray bsonArray = new BsonArray();
        for (final TagSet tagSet : this.tagSetList) {
            bsonArray.add(this.toDocument(tagSet));
        }
        return bsonArray;
    }
    
    private BsonDocument toDocument(final TagSet tagSet) {
        final BsonDocument document = new BsonDocument();
        for (final Tag tag : tagSet) {
            document.put(tag.getName(), new BsonString(tag.getValue()));
        }
        return document;
    }
    
    static class SecondaryReadPreference extends TaggableReadPreference
    {
        SecondaryReadPreference() {
        }
        
        SecondaryReadPreference(final TagSet tagSet) {
            super(tagSet);
        }
        
        SecondaryReadPreference(final List<TagSet> tagSetList) {
            super(tagSetList);
        }
        
        @Override
        public String getName() {
            return "secondary";
        }
        
        @Override
        public List<ServerDescription> choose(final ClusterDescription clusterDescription) {
            if (this.getTagSetList().isEmpty()) {
                return clusterDescription.getSecondaries();
            }
            for (final TagSet tagSet : this.getTagSetList()) {
                final List<ServerDescription> servers = clusterDescription.getSecondaries(tagSet);
                if (!servers.isEmpty()) {
                    return servers;
                }
            }
            return Collections.emptyList();
        }
    }
    
    static class SecondaryPreferredReadPreference extends SecondaryReadPreference
    {
        SecondaryPreferredReadPreference() {
        }
        
        SecondaryPreferredReadPreference(final TagSet tagSet) {
            super(tagSet);
        }
        
        SecondaryPreferredReadPreference(final List<TagSet> tagSetList) {
            super(tagSetList);
        }
        
        @Override
        public String getName() {
            return "secondaryPreferred";
        }
        
        @Override
        public List<ServerDescription> choose(final ClusterDescription clusterDescription) {
            final List<ServerDescription> servers = super.choose(clusterDescription);
            return servers.isEmpty() ? clusterDescription.getPrimaries() : servers;
        }
    }
    
    static class NearestReadPreference extends TaggableReadPreference
    {
        NearestReadPreference() {
        }
        
        NearestReadPreference(final TagSet tagSet) {
            super(tagSet);
        }
        
        NearestReadPreference(final List<TagSet> tagSetList) {
            super(tagSetList);
        }
        
        @Override
        public String getName() {
            return "nearest";
        }
        
        @Override
        public List<ServerDescription> choose(final ClusterDescription clusterDescription) {
            if (this.getTagSetList().isEmpty()) {
                return clusterDescription.getAnyPrimaryOrSecondary();
            }
            for (final TagSet tagSet : this.getTagSetList()) {
                final List<ServerDescription> servers = clusterDescription.getAnyPrimaryOrSecondary(tagSet);
                if (!servers.isEmpty()) {
                    return servers;
                }
            }
            return Collections.emptyList();
        }
    }
    
    static class PrimaryPreferredReadPreference extends SecondaryReadPreference
    {
        PrimaryPreferredReadPreference() {
        }
        
        PrimaryPreferredReadPreference(final TagSet tagSet) {
            super(tagSet);
        }
        
        PrimaryPreferredReadPreference(final List<TagSet> tagSetList) {
            super(tagSetList);
        }
        
        @Override
        public String getName() {
            return "primaryPreferred";
        }
        
        @Override
        public List<ServerDescription> choose(final ClusterDescription clusterDescription) {
            final List<ServerDescription> servers = clusterDescription.getPrimaries();
            return servers.isEmpty() ? super.choose(clusterDescription) : servers;
        }
    }
}
