// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;

public class QueryBuilder
{
    private final DBObject _query;
    private String _currentKey;
    private boolean _hasNot;
    
    public QueryBuilder() {
        this._query = new BasicDBObject();
    }
    
    public static QueryBuilder start() {
        return new QueryBuilder();
    }
    
    public static QueryBuilder start(final String key) {
        return new QueryBuilder().put(key);
    }
    
    public QueryBuilder put(final String key) {
        this._currentKey = key;
        if (this._query.get(key) == null) {
            this._query.put(this._currentKey, new NullObject());
        }
        return this;
    }
    
    public QueryBuilder and(final String key) {
        return this.put(key);
    }
    
    public QueryBuilder greaterThan(final Object object) {
        this.addOperand("$gt", object);
        return this;
    }
    
    public QueryBuilder greaterThanEquals(final Object object) {
        this.addOperand("$gte", object);
        return this;
    }
    
    public QueryBuilder lessThan(final Object object) {
        this.addOperand("$lt", object);
        return this;
    }
    
    public QueryBuilder lessThanEquals(final Object object) {
        this.addOperand("$lte", object);
        return this;
    }
    
    public QueryBuilder is(final Object object) {
        this.addOperand(null, object);
        return this;
    }
    
    public QueryBuilder notEquals(final Object object) {
        this.addOperand("$ne", object);
        return this;
    }
    
    public QueryBuilder in(final Object object) {
        this.addOperand("$in", object);
        return this;
    }
    
    public QueryBuilder notIn(final Object object) {
        this.addOperand("$nin", object);
        return this;
    }
    
    public QueryBuilder mod(final Object object) {
        this.addOperand("$mod", object);
        return this;
    }
    
    public QueryBuilder all(final Object object) {
        this.addOperand("$all", object);
        return this;
    }
    
    public QueryBuilder size(final Object object) {
        this.addOperand("$size", object);
        return this;
    }
    
    public QueryBuilder exists(final Object object) {
        this.addOperand("$exists", object);
        return this;
    }
    
    public QueryBuilder regex(final Pattern regex) {
        this.addOperand(null, regex);
        return this;
    }
    
    public QueryBuilder elemMatch(final DBObject match) {
        this.addOperand("$elemMatch", match);
        return this;
    }
    
    public QueryBuilder withinCenter(final double x, final double y, final double radius) {
        this.addOperand("$within", new BasicDBObject("$center", Arrays.asList(Arrays.asList(x, y), radius)));
        return this;
    }
    
    public QueryBuilder near(final double x, final double y) {
        this.addOperand("$near", Arrays.asList(x, y));
        return this;
    }
    
    public QueryBuilder near(final double x, final double y, final double maxDistance) {
        this.addOperand("$near", Arrays.asList(x, y));
        this.addOperand("$maxDistance", maxDistance);
        return this;
    }
    
    public QueryBuilder nearSphere(final double longitude, final double latitude) {
        this.addOperand("$nearSphere", Arrays.asList(longitude, latitude));
        return this;
    }
    
    public QueryBuilder nearSphere(final double longitude, final double latitude, final double maxDistance) {
        this.addOperand("$nearSphere", Arrays.asList(longitude, latitude));
        this.addOperand("$maxDistance", maxDistance);
        return this;
    }
    
    public QueryBuilder withinCenterSphere(final double longitude, final double latitude, final double maxDistance) {
        this.addOperand("$within", new BasicDBObject("$centerSphere", Arrays.asList(Arrays.asList(longitude, latitude), maxDistance)));
        return this;
    }
    
    public QueryBuilder withinBox(final double x, final double y, final double x2, final double y2) {
        this.addOperand("$within", new BasicDBObject("$box", new Object[] { { x, y }, { x2, y2 } }));
        return this;
    }
    
    public QueryBuilder withinPolygon(final List<Double[]> points) {
        if (points == null || points.isEmpty() || points.size() < 3) {
            throw new IllegalArgumentException("Polygon insufficient number of vertices defined");
        }
        this.addOperand("$within", new BasicDBObject("$polygon", this.convertToListOfLists(points)));
        return this;
    }
    
    private List<List<Double>> convertToListOfLists(final List<Double[]> points) {
        final List<List<Double>> listOfLists = new ArrayList<List<Double>>(points.size());
        for (final Double[] cur : points) {
            final List<Double> list = new ArrayList<Double>(cur.length);
            Collections.addAll(list, cur);
            listOfLists.add(list);
        }
        return listOfLists;
    }
    
    public QueryBuilder text(final String search) {
        return this.text(search, null);
    }
    
    public QueryBuilder text(final String search, final String language) {
        if (this._currentKey != null) {
            throw new QueryBuilderException("The text operand may only occur at the top-level of a query. It does not apply to a specific element, but rather to a document as a whole.");
        }
        this.put("$text");
        this.addOperand("$search", search);
        if (language != null) {
            this.addOperand("$language", language);
        }
        return this;
    }
    
    public QueryBuilder not() {
        this._hasNot = true;
        return this;
    }
    
    public QueryBuilder or(final DBObject... ors) {
        List l = (List)this._query.get("$or");
        if (l == null) {
            l = new ArrayList();
            this._query.put("$or", l);
        }
        Collections.addAll(l, ors);
        return this;
    }
    
    public QueryBuilder and(final DBObject... ands) {
        List l = (List)this._query.get("$and");
        if (l == null) {
            l = new ArrayList();
            this._query.put("$and", l);
        }
        Collections.addAll(l, ands);
        return this;
    }
    
    public DBObject get() {
        for (final String key : this._query.keySet()) {
            if (this._query.get(key) instanceof NullObject) {
                throw new QueryBuilderException("No operand for key:" + key);
            }
        }
        return this._query;
    }
    
    private void addOperand(final String op, final Object value) {
        Object valueToPut = value;
        if (op == null) {
            if (this._hasNot) {
                valueToPut = new BasicDBObject("$not", valueToPut);
                this._hasNot = false;
            }
            this._query.put(this._currentKey, valueToPut);
            return;
        }
        final Object storedValue = this._query.get(this._currentKey);
        BasicDBObject operand;
        if (!(storedValue instanceof DBObject)) {
            operand = new BasicDBObject();
            if (this._hasNot) {
                final DBObject notOperand = new BasicDBObject("$not", operand);
                this._query.put(this._currentKey, notOperand);
                this._hasNot = false;
            }
            else {
                this._query.put(this._currentKey, operand);
            }
        }
        else {
            operand = (BasicDBObject)this._query.get(this._currentKey);
            if (operand.get("$not") != null) {
                operand = (BasicDBObject)operand.get("$not");
            }
        }
        operand.put(op, valueToPut);
    }
    
    static class QueryBuilderException extends RuntimeException
    {
        QueryBuilderException(final String message) {
            super(message);
        }
    }
    
    private static class NullObject
    {
    }
}
