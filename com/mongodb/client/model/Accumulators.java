// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model;

import org.bson.conversions.Bson;

public final class Accumulators
{
    public static <TExpression> BsonField sum(final String fieldName, final TExpression expression) {
        return accumulator("$sum", fieldName, expression);
    }
    
    public static <TExpression> BsonField avg(final String fieldName, final TExpression expression) {
        return accumulator("$avg", fieldName, expression);
    }
    
    public static <TExpression> BsonField first(final String fieldName, final TExpression expression) {
        return accumulator("$first", fieldName, expression);
    }
    
    public static <TExpression> BsonField last(final String fieldName, final TExpression expression) {
        return accumulator("$last", fieldName, expression);
    }
    
    public static <TExpression> BsonField max(final String fieldName, final TExpression expression) {
        return accumulator("$max", fieldName, expression);
    }
    
    public static <TExpression> BsonField min(final String fieldName, final TExpression expression) {
        return accumulator("$min", fieldName, expression);
    }
    
    public static <TExpression> BsonField push(final String fieldName, final TExpression expression) {
        return accumulator("$push", fieldName, expression);
    }
    
    public static <TExpression> BsonField addToSet(final String fieldName, final TExpression expression) {
        return accumulator("$addToSet", fieldName, expression);
    }
    
    private static <TExpression> BsonField accumulator(final String name, final String fieldName, final TExpression expression) {
        return new BsonField(fieldName, new SimpleExpression<Object>(name, expression));
    }
}
