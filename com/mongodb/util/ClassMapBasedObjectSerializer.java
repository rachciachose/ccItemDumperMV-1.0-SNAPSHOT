// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.util;

import java.util.Iterator;
import java.util.List;
import org.bson.BSON;
import org.bson.util.ClassMap;

class ClassMapBasedObjectSerializer extends AbstractObjectSerializer
{
    private final ClassMap<ObjectSerializer> _serializers;
    
    ClassMapBasedObjectSerializer() {
        this._serializers = new ClassMap<ObjectSerializer>();
    }
    
    void addObjectSerializer(final Class c, final ObjectSerializer serializer) {
        this._serializers.put(c, serializer);
    }
    
    @Override
    public void serialize(final Object obj, final StringBuilder buf) {
        final Object objectToSerialize = BSON.applyEncodingHooks(obj);
        if (objectToSerialize == null) {
            buf.append(" null ");
            return;
        }
        ObjectSerializer serializer = null;
        final List<Class<?>> ancestors = ClassMap.getAncestry(objectToSerialize.getClass());
        for (final Class<?> ancestor : ancestors) {
            serializer = this._serializers.get(ancestor);
            if (serializer != null) {
                break;
            }
        }
        if (serializer == null && objectToSerialize.getClass().isArray()) {
            serializer = this._serializers.get(Object[].class);
        }
        if (serializer == null) {
            throw new RuntimeException("json can't serialize type : " + objectToSerialize.getClass());
        }
        serializer.serialize(objectToSerialize, buf);
    }
}
