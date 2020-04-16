// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

class NoOpFieldNameValidator implements FieldNameValidator
{
    @Override
    public boolean validate(final String fieldName) {
        return true;
    }
    
    @Override
    public FieldNameValidator getValidatorForField(final String fieldName) {
        return this;
    }
}
