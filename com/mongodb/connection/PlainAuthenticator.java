// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import javax.security.sasl.SaslException;
import com.mongodb.MongoSecurityException;
import java.util.Map;
import javax.security.sasl.Sasl;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import com.mongodb.assertions.Assertions;
import javax.security.sasl.SaslClient;
import com.mongodb.ServerAddress;
import com.mongodb.AuthenticationMechanism;
import com.mongodb.MongoCredential;

class PlainAuthenticator extends SaslAuthenticator
{
    private static final String DEFAULT_PROTOCOL = "mongodb";
    
    PlainAuthenticator(final MongoCredential credential) {
        super(credential);
    }
    
    @Override
    public String getMechanismName() {
        return AuthenticationMechanism.PLAIN.getMechanismName();
    }
    
    @Override
    protected SaslClient createSaslClient(final ServerAddress serverAddress) {
        final MongoCredential credential = this.getCredential();
        Assertions.isTrue("mechanism is PLAIN", credential.getAuthenticationMechanism() == AuthenticationMechanism.PLAIN);
        try {
            return Sasl.createSaslClient(new String[] { AuthenticationMechanism.PLAIN.getMechanismName() }, credential.getUserName(), "mongodb", serverAddress.getHost(), null, new CallbackHandler() {
                @Override
                public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    for (final Callback callback : callbacks) {
                        if (callback instanceof PasswordCallback) {
                            ((PasswordCallback)callback).setPassword(credential.getPassword());
                        }
                        else if (callback instanceof NameCallback) {
                            ((NameCallback)callback).setName(credential.getUserName());
                        }
                    }
                }
            });
        }
        catch (SaslException e) {
            throw new MongoSecurityException(credential, "Exception initializing SASL client", e);
        }
    }
}
