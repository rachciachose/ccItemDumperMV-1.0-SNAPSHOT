// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.internal.validator;

import java.util.Arrays;
import java.util.List;
import org.bson.FieldNameValidator;

public class CollectibleDocumentFieldNameValidator implements FieldNameValidator
{
    private static final List<String> EXCEPTIONS;
    
    @Override
    public boolean validate(final String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("Field name can not be null");
        }
        return !fieldName.contains(".") && (!fieldName.startsWith("$") || CollectibleDocumentFieldNameValidator.EXCEPTIONS.contains(fieldName));
    }
    
    @Override
    public FieldNameValidator getValidatorForField(final String fieldName) {
        return this;
    }
    
    static {
        EXCEPTIONS = Arrays.asList("$db", "$ref", "$id");
    }
}
