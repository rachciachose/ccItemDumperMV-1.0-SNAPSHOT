// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.json;

enum JsonTokenType
{
    INVALID, 
    BEGIN_ARRAY, 
    BEGIN_OBJECT, 
    END_ARRAY, 
    LEFT_PAREN, 
    RIGHT_PAREN, 
    END_OBJECT, 
    COLON, 
    COMMA, 
    DOUBLE, 
    INT32, 
    INT64, 
    REGULAR_EXPRESSION, 
    STRING, 
    UNQUOTED_STRING, 
    END_OF_FILE;
}
