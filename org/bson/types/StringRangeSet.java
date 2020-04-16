// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.types;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Set;

class StringRangeSet
{
    private final Set<String> numbersAsStrings;
    
    StringRangeSet(final int size) {
        this.numbersAsStrings = new TreeSet<String>(new NumberStringComparator());
        for (int i = 0; i < size; ++i) {
            this.numbersAsStrings.add(String.valueOf(i));
        }
    }
    
    public Set<String> getSet() {
        return this.numbersAsStrings;
    }
    
    private static class NumberStringComparator implements Comparator<String>
    {
        @Override
        public int compare(final String o1, final String o2) {
            return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
        }
    }
}
