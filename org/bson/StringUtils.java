// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

import java.util.Iterator;
import java.util.Collection;

final class StringUtils
{
    public static String join(final String delimiter, final Collection<?> s) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
}
