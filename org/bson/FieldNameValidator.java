// 
// Decompiled by Procyon v0.5.30
// 

package org.bson;

public interface FieldNameValidator
{
    boolean validate(final String p0);
    
    FieldNameValidator getValidatorForField(final String p0);
}
