// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.internal.async;

import com.mongodb.assertions.Assertions;
import com.mongodb.diagnostics.logging.Logger;
import com.mongodb.async.SingleResultCallback;

public class ErrorHandlingResultCallback<T> implements SingleResultCallback<T>
{
    private final SingleResultCallback<T> wrapped;
    private final Logger logger;
    
    public static <T> SingleResultCallback<T> errorHandlingCallback(final SingleResultCallback<T> callback) {
        return errorHandlingCallback(callback, null);
    }
    
    public static <T> SingleResultCallback<T> errorHandlingCallback(final SingleResultCallback<T> callback, final Logger logger) {
        if (callback instanceof ErrorHandlingResultCallback) {
            return callback;
        }
        return new ErrorHandlingResultCallback<T>(callback, logger);
    }
    
    ErrorHandlingResultCallback(final SingleResultCallback<T> wrapped, final Logger logger) {
        this.wrapped = Assertions.notNull("wrapped", wrapped);
        this.logger = logger;
    }
    
    @Override
    public void onResult(final T result, final Throwable t) {
        try {
            this.wrapped.onResult(result, t);
        }
        catch (Exception e) {
            if (this.logger != null) {
                this.logger.warn("Callback onResult call produced an error", e);
            }
        }
    }
}
