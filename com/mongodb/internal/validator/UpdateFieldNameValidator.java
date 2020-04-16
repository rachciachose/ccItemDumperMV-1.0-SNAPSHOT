// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.internal.validator;

import org.bson.FieldNameValidator;

public class UpdateFieldNameValidator implements FieldNameValidator
{
    @Override
    public boolean validate(final String fieldName) {
        return fieldName.startsWith("$");
    }
    
    @Override
    public FieldNameValidator getValidatorForField(final String fieldName) {
        return new NoOpFieldNameValidator();
    }
}
