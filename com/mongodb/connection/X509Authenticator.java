// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.AuthenticationMechanism;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonInt32;
import com.mongodb.async.SingleResultCallback;
import org.bson.BsonDocument;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoSecurityException;
import com.mongodb.MongoCredential;

class X509Authenticator extends Authenticator
{
    X509Authenticator(final MongoCredential credential) {
        super(credential);
    }
    
    @Override
    void authenticate(final InternalConnection connection, final ConnectionDescription connectionDescription) {
        try {
            final BsonDocument authCommand = this.getAuthCommand(this.getCredential().getUserName());
            CommandHelper.executeCommand(this.getCredential().getSource(), authCommand, connection);
        }
        catch (MongoCommandException e) {
            throw new MongoSecurityException(this.getCredential(), "Exception authenticating", e);
        }
    }
    
    @Override
    void authenticateAsync(final InternalConnection connection, final ConnectionDescription connectionDescription, final SingleResultCallback<Void> callback) {
        CommandHelper.executeCommandAsync(this.getCredential().getSource(), this.getAuthCommand(this.getCredential().getUserName()), connection, new SingleResultCallback<BsonDocument>() {
            @Override
            public void onResult(final BsonDocument nonceResult, final Throwable t) {
                if (t != null) {
                    callback.onResult(null, X509Authenticator.this.translateThrowable(t));
                }
                else {
                    callback.onResult(null, null);
                }
            }
        });
    }
    
    private BsonDocument getAuthCommand(final String userName) {
        final BsonDocument cmd = new BsonDocument();
        cmd.put("authenticate", new BsonInt32(1));
        cmd.put("user", new BsonString(userName));
        cmd.put("mechanism", new BsonString(AuthenticationMechanism.MONGODB_X509.getMechanismName()));
        return cmd;
    }
    
    private Throwable translateThrowable(final Throwable t) {
        if (t instanceof MongoCommandException) {
            return new MongoSecurityException(this.getCredential(), "Exception authenticating", t);
        }
        return t;
    }
}
