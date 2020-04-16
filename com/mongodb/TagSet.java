// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import com.mongodb.assertions.Assertions;
import java.util.Collections;
import java.util.List;
import com.mongodb.annotations.Immutable;

@Immutable
public final class TagSet implements Iterable<Tag>
{
    private final List<Tag> wrapped;
    
    public TagSet() {
        this.wrapped = Collections.emptyList();
    }
    
    public TagSet(final Tag tag) {
        Assertions.notNull("tag", tag);
        this.wrapped = Collections.singletonList(tag);
    }
    
    public TagSet(final List<Tag> tagList) {
        Assertions.notNull("tagList", tagList);
        final Set<String> tagNames = new HashSet<String>();
        for (final Tag tag : tagList) {
            if (tag == null) {
                throw new IllegalArgumentException("Null tags are not allowed");
            }
            if (!tagNames.add(tag.getName())) {
                throw new IllegalArgumentException("Duplicate tag names not allowed in a tag set: " + tag.getName());
            }
        }
        this.wrapped = Collections.unmodifiableList((List<? extends Tag>)new ArrayList<Tag>(tagList));
    }
    
    @Override
    public Iterator<Tag> iterator() {
        return this.wrapped.iterator();
    }
    
    public boolean containsAll(final TagSet tagSet) {
        return this.wrapped.containsAll(tagSet.wrapped);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TagSet tags = (TagSet)o;
        return this.wrapped.equals(tags.wrapped);
    }
    
    @Override
    public int hashCode() {
        return this.wrapped.hashCode();
    }
    
    @Override
    public String toString() {
        return "TagSet{" + this.wrapped + '}';
    }
}
