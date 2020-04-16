// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.AuthenticationMechanism;
import java.util.Iterator;
import java.util.ArrayList;
import com.mongodb.assertions.Assertions;
import com.mongodb.MongoCredential;
import java.util.List;
import com.mongodb.event.ConnectionListener;

class InternalStreamConnectionFactory implements InternalConnectionFactory
{
    private final StreamFactory streamFactory;
    private final ConnectionListener connectionListener;
    private final List<Authenticator> authenticators;
    
    public InternalStreamConnectionFactory(final StreamFactory streamFactory, final List<MongoCredential> credentialList, final ConnectionListener connectionListener) {
        this.streamFactory = Assertions.notNull("streamFactory", streamFactory);
        this.connectionListener = Assertions.notNull("connectionListener", connectionListener);
        Assertions.notNull("credentialList", credentialList);
        this.authenticators = new ArrayList<Authenticator>(credentialList.size());
        for (final MongoCredential credential : credentialList) {
            this.authenticators.add(this.createAuthenticator(credential));
        }
    }
    
    @Override
    public InternalConnection create(final ServerId serverId) {
        return new InternalStreamConnection(serverId, this.streamFactory, new InternalStreamConnectionInitializer(this.authenticators), this.connectionListener);
    }
    
    private Authenticator createAuthenticator(final MongoCredential credential) {
        if (credential.getAuthenticationMechanism() == null) {
            return new DefaultAuthenticator(credential);
        }
        switch (credential.getAuthenticationMechanism()) {
            case GSSAPI: {
                return new GSSAPIAuthenticator(credential);
            }
            case PLAIN: {
                return new PlainAuthenticator(credential);
            }
            case MONGODB_X509: {
                return new X509Authenticator(credential);
            }
            case SCRAM_SHA_1: {
                return new ScramSha1Authenticator(credential);
            }
            case MONGODB_CR: {
                return new NativeAuthenticator(credential);
            }
            default: {
                throw new IllegalArgumentException("Unsupported authentication mechanism " + credential.getAuthenticationMechanism());
            }
        }
    }
}
