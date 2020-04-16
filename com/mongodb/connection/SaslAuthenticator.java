// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.MongoException;
import javax.security.sasl.SaslException;
import org.bson.BsonBinary;
import org.bson.BsonString;
import org.bson.BsonValue;
import com.mongodb.ServerAddress;
import com.mongodb.async.SingleResultCallback;
import org.bson.BsonInt32;
import org.bson.BsonDocument;
import javax.security.sasl.SaslClient;
import com.mongodb.MongoSecurityException;
import com.mongodb.MongoCredential;

abstract class SaslAuthenticator extends Authenticator
{
    SaslAuthenticator(final MongoCredential credential) {
        super(credential);
    }
    
    public void authenticate(final InternalConnection connection, final ConnectionDescription connectionDescription) {
        final SaslClient saslClient = this.createSaslClient(connection.getDescription().getServerAddress());
        try {
            byte[] response = (byte[])(saslClient.hasInitialResponse() ? saslClient.evaluateChallenge(new byte[0]) : null);
            BsonDocument res = this.sendSaslStart(response, connection);
            for (BsonInt32 conversationId = res.getInt32("conversationId"); !res.getBoolean("done").getValue(); res = this.sendSaslContinue(conversationId, response, connection)) {
                response = saslClient.evaluateChallenge(res.getBinary("payload").getData());
                if (response == null) {
                    throw new MongoSecurityException(this.getCredential(), "SASL protocol error: no client response to challenge for credential " + this.getCredential());
                }
            }
        }
        catch (Exception e) {
            throw new MongoSecurityException(this.getCredential(), "Exception authenticating " + this.getCredential(), e);
        }
        finally {
            this.disposeOfSaslClient(saslClient);
        }
    }
    
    @Override
    void authenticateAsync(final InternalConnection connection, final ConnectionDescription connectionDescription, final SingleResultCallback<Void> callback) {
        try {
            final SaslClient saslClient = this.createSaslClient(connection.getDescription().getServerAddress());
            final byte[] response = (byte[])(saslClient.hasInitialResponse() ? saslClient.evaluateChallenge(new byte[0]) : null);
            this.sendSaslStartAsync(response, connection, new SingleResultCallback<BsonDocument>() {
                @Override
                public void onResult(final BsonDocument result, final Throwable t) {
                    if (t != null) {
                        callback.onResult(null, SaslAuthenticator.this.translateThrowable(t));
                    }
                    else if (result.getBoolean("done").getValue()) {
                        callback.onResult(null, null);
                    }
                    else {
                        new Continuator(saslClient, result, connection, callback).start();
                    }
                }
            });
        }
        catch (Exception e) {
            callback.onResult(null, this.translateThrowable(e));
        }
    }
    
    public abstract String getMechanismName();
    
    protected abstract SaslClient createSaslClient(final ServerAddress p0);
    
    private BsonDocument sendSaslStart(final byte[] outToken, final InternalConnection connection) {
        return CommandHelper.executeCommand(this.getCredential().getSource(), this.createSaslStartCommandDocument(outToken), connection);
    }
    
    private BsonDocument sendSaslContinue(final BsonInt32 conversationId, final byte[] outToken, final InternalConnection connection) {
        return CommandHelper.executeCommand(this.getCredential().getSource(), this.createSaslContinueDocument(conversationId, outToken), connection);
    }
    
    private void sendSaslStartAsync(final byte[] outToken, final InternalConnection connection, final SingleResultCallback<BsonDocument> callback) {
        CommandHelper.executeCommandAsync(this.getCredential().getSource(), this.createSaslStartCommandDocument(outToken), connection, callback);
    }
    
    private void sendSaslContinueAsync(final BsonInt32 conversationId, final byte[] outToken, final InternalConnection connection, final SingleResultCallback<BsonDocument> callback) {
        CommandHelper.executeCommandAsync(this.getCredential().getSource(), this.createSaslContinueDocument(conversationId, outToken), connection, callback);
    }
    
    private BsonDocument createSaslStartCommandDocument(final byte[] outToken) {
        return new BsonDocument("saslStart", new BsonInt32(1)).append("mechanism", new BsonString(this.getMechanismName())).append("payload", new BsonBinary((outToken != null) ? outToken : new byte[0]));
    }
    
    private BsonDocument createSaslContinueDocument(final BsonInt32 conversationId, final byte[] outToken) {
        return new BsonDocument("saslContinue", new BsonInt32(1)).append("conversationId", conversationId).append("payload", new BsonBinary(outToken));
    }
    
    private void disposeOfSaslClient(final SaslClient saslClient) {
        try {
            saslClient.dispose();
        }
        catch (SaslException ex) {}
    }
    
    private MongoException translateThrowable(final Throwable t) {
        return new MongoSecurityException(this.getCredential(), "Exception authenticating", t);
    }
    
    private final class Continuator implements SingleResultCallback<BsonDocument>
    {
        private final SaslClient saslClient;
        private final BsonDocument saslStartDocument;
        private final InternalConnection connection;
        private final SingleResultCallback<Void> callback;
        
        public Continuator(final SaslClient saslClient, final BsonDocument saslStartDocument, final InternalConnection connection, final SingleResultCallback<Void> callback) {
            this.saslClient = saslClient;
            this.saslStartDocument = saslStartDocument;
            this.connection = connection;
            this.callback = callback;
        }
        
        @Override
        public void onResult(final BsonDocument result, final Throwable t) {
            if (t != null) {
                this.callback.onResult(null, SaslAuthenticator.this.translateThrowable(t));
                SaslAuthenticator.this.disposeOfSaslClient(this.saslClient);
            }
            else if (result.getBoolean("done").getValue()) {
                this.callback.onResult(null, null);
                SaslAuthenticator.this.disposeOfSaslClient(this.saslClient);
            }
            else {
                this.continueConversation(result);
            }
        }
        
        public void start() {
            this.continueConversation(this.saslStartDocument);
        }
        
        private void continueConversation(final BsonDocument result) {
            try {
                SaslAuthenticator.this.sendSaslContinueAsync(this.saslStartDocument.getInt32("conversationId"), this.saslClient.evaluateChallenge(result.getBinary("payload").getData()), this.connection, this);
            }
            catch (SaslException e) {
                this.callback.onResult(null, SaslAuthenticator.this.translateThrowable(e));
                SaslAuthenticator.this.disposeOfSaslClient(this.saslClient);
            }
        }
    }
}
