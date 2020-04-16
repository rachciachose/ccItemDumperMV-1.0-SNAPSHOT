// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.mongodb.annotations.Immutable;

@Immutable
final class DBCollectionObjectFactory implements DBObjectFactory
{
    private final Map<List<String>, Class<? extends DBObject>> pathToClassMap;
    private final ReflectionDBObject.JavaWrapper wrapper;
    
    public DBCollectionObjectFactory() {
        this(Collections.emptyMap(), null);
    }
    
    private DBCollectionObjectFactory(final Map<List<String>, Class<? extends DBObject>> pathToClassMap, final ReflectionDBObject.JavaWrapper wrapper) {
        this.pathToClassMap = pathToClassMap;
        this.wrapper = wrapper;
    }
    
    @Override
    public DBObject getInstance() {
        return this.getInstance(Collections.emptyList());
    }
    
    @Override
    public DBObject getInstance(final List<String> path) {
        final Class<? extends DBObject> aClass = this.getClassForPath(path);
        try {
            return (DBObject)aClass.newInstance();
        }
        catch (InstantiationException e) {
            throw this.createInternalException(aClass, e);
        }
        catch (IllegalAccessException e2) {
            throw this.createInternalException(aClass, e2);
        }
    }
    
    public DBCollectionObjectFactory update(final Class<? extends DBObject> aClass) {
        return new DBCollectionObjectFactory(this.updatePathToClassMap(aClass, Collections.emptyList()), this.isReflectionDBObject(aClass) ? ReflectionDBObject.getWrapper(aClass) : this.wrapper);
    }
    
    public DBCollectionObjectFactory update(final Class<? extends DBObject> aClass, final List<String> path) {
        return new DBCollectionObjectFactory(this.updatePathToClassMap(aClass, path), this.wrapper);
    }
    
    private Map<List<String>, Class<? extends DBObject>> updatePathToClassMap(final Class<? extends DBObject> aClass, final List<String> path) {
        final Map<List<String>, Class<? extends DBObject>> map = new HashMap<List<String>, Class<? extends DBObject>>(this.pathToClassMap);
        if (aClass != null) {
            map.put(path, aClass);
        }
        else {
            map.remove(path);
        }
        return map;
    }
    
    Class<? extends DBObject> getClassForPath(final List<String> path) {
        if (this.pathToClassMap.containsKey(path)) {
            return this.pathToClassMap.get(path);
        }
        final Class<? extends DBObject> aClass = (this.wrapper != null) ? this.wrapper.getInternalClass(path) : null;
        return (aClass != null) ? aClass : BasicDBObject.class;
    }
    
    private boolean isReflectionDBObject(final Class<? extends DBObject> aClass) {
        return aClass != null && ReflectionDBObject.class.isAssignableFrom(aClass);
    }
    
    private MongoInternalException createInternalException(final Class<? extends DBObject> aClass, final Exception e) {
        throw new MongoInternalException("Can't instantiate class " + aClass, e);
    }
}
