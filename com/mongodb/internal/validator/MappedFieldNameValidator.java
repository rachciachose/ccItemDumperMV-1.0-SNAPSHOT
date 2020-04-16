// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.internal.validator;

import java.util.Map;
import org.bson.FieldNameValidator;

public class MappedFieldNameValidator implements FieldNameValidator
{
    private final FieldNameValidator defaultValidator;
    private final Map<String, FieldNameValidator> fieldNameToValidatorMap;
    
    public MappedFieldNameValidator(final FieldNameValidator defaultValidator, final Map<String, FieldNameValidator> fieldNameToValidatorMap) {
        this.defaultValidator = defaultValidator;
        this.fieldNameToValidatorMap = fieldNameToValidatorMap;
    }
    
    @Override
    public boolean validate(final String fieldName) {
        return this.defaultValidator.validate(fieldName);
    }
    
    @Override
    public FieldNameValidator getValidatorForField(final String fieldName) {
        if (this.fieldNameToValidatorMap.containsKey(fieldName)) {
            return this.fieldNameToValidatorMap.get(fieldName);
        }
        return this.defaultValidator;
    }
}
