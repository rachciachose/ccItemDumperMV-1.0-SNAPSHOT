// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.Document;
import org.bson.Transformer;

final class DocumentToDBRefTransformer implements Transformer
{
    @Override
    public Object transform(final Object value) {
        if (value instanceof Document) {
            final Document document = (Document)value;
            if (document.containsKey("$id") && document.containsKey("$ref")) {
                return new DBRef((String)document.get("$ref"), document.get("$id"));
            }
        }
        return value;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o != null && this.getClass() == o.getClass());
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
}
