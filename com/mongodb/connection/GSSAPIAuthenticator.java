// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.net.InetAddress;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSCredential;
import java.net.UnknownHostException;
import org.ietf.jgss.GSSException;
import javax.security.sasl.SaslException;
import com.mongodb.MongoSecurityException;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.Sasl;
import java.util.HashMap;
import javax.security.sasl.SaslClient;
import com.mongodb.ServerAddress;
import com.mongodb.MongoException;
import com.mongodb.AuthenticationMechanism;
import com.mongodb.MongoCredential;

class GSSAPIAuthenticator extends SaslAuthenticator
{
    private static final String GSSAPI_MECHANISM_NAME = "GSSAPI";
    private static final String GSSAPI_OID = "1.2.840.113554.1.2.2";
    public static final String SERVICE_NAME_KEY = "SERVICE_NAME";
    public static final String SERVICE_NAME_DEFAULT_VALUE = "mongodb";
    public static final String CANONICALIZE_HOST_NAME_KEY = "CANONICALIZE_HOST_NAME";
    public static final Boolean CANONICALIZE_HOST_NAME_DEFAULT_VALUE;
    
    GSSAPIAuthenticator(final MongoCredential credential) {
        super(credential);
        if (this.getCredential().getAuthenticationMechanism() != AuthenticationMechanism.GSSAPI) {
            throw new MongoException("Incorrect mechanism: " + this.getCredential().getMechanism());
        }
    }
    
    @Override
    public String getMechanismName() {
        return "GSSAPI";
    }
    
    @Override
    protected SaslClient createSaslClient(final ServerAddress serverAddress) {
        final MongoCredential credential = this.getCredential();
        try {
            final Map<String, Object> props = new HashMap<String, Object>();
            props.put("javax.security.sasl.credentials", this.getGSSCredential(credential.getUserName()));
            final SaslClient saslClient = Sasl.createSaslClient(new String[] { AuthenticationMechanism.GSSAPI.getMechanismName() }, credential.getUserName(), credential.getMechanismProperty("SERVICE_NAME", "mongodb"), this.getHostName(serverAddress), props, null);
            if (saslClient == null) {
                throw new MongoSecurityException(credential, String.format("No platform support for %s mechanism", AuthenticationMechanism.GSSAPI));
            }
            return saslClient;
        }
        catch (SaslException e) {
            throw new MongoSecurityException(credential, "Exception initializing SASL client", e);
        }
        catch (GSSException e2) {
            throw new MongoSecurityException(credential, "Exception initializing GSSAPI credentials", e2);
        }
        catch (UnknownHostException e3) {
            throw new MongoSecurityException(credential, "Unable to canonicalize host name + " + serverAddress);
        }
    }
    
    private GSSCredential getGSSCredential(final String userName) throws GSSException {
        final Oid krb5Mechanism = new Oid("1.2.840.113554.1.2.2");
        final GSSManager manager = GSSManager.getInstance();
        final GSSName name = manager.createName(userName, GSSName.NT_USER_NAME);
        return manager.createCredential(name, Integer.MAX_VALUE, krb5Mechanism, 1);
    }
    
    private String getHostName(final ServerAddress serverAddress) throws UnknownHostException {
        return this.getCredential().getMechanismProperty("CANONICALIZE_HOST_NAME", GSSAPIAuthenticator.CANONICALIZE_HOST_NAME_DEFAULT_VALUE) ? InetAddress.getByName(serverAddress.getHost()).getCanonicalHostName() : serverAddress.getHost();
    }
    
    static {
        CANONICALIZE_HOST_NAME_DEFAULT_VALUE = false;
    }
}
