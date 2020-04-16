// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import com.mongodb.diagnostics.logging.Loggers;
import org.bson.BsonReader;
import org.bson.codecs.DecoderContext;
import org.bson.BsonDocumentReader;
import org.bson.codecs.BsonDocumentCodec;
import com.mongodb.ServerAddress;
import org.bson.codecs.Decoder;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.diagnostics.logging.Logger;
import org.bson.BsonDocument;

class CommandResultCallback<T> extends CommandResultBaseCallback<BsonDocument>
{
    public static final Logger LOGGER;
    private final SingleResultCallback<T> callback;
    private final Decoder<T> decoder;
    
    CommandResultCallback(final SingleResultCallback<T> callback, final Decoder<T> decoder, final long requestId, final ServerAddress serverAddress) {
        super(new BsonDocumentCodec(), requestId, serverAddress);
        this.callback = callback;
        this.decoder = decoder;
    }
    
    @Override
    protected void callCallback(final BsonDocument response, final Throwable t) {
        if (t != null) {
            this.callback.onResult(null, t);
        }
        else {
            if (CommandResultCallback.LOGGER.isDebugEnabled()) {
                CommandResultCallback.LOGGER.debug("Command execution completed with status " + ProtocolHelper.isCommandOk(response));
            }
            if (!ProtocolHelper.isCommandOk(response)) {
                this.callback.onResult(null, ProtocolHelper.getCommandFailureException(response, this.getServerAddress()));
            }
            else {
                try {
                    this.callback.onResult(this.decoder.decode(new BsonDocumentReader(response), DecoderContext.builder().build()), null);
                }
                catch (Throwable t2) {
                    this.callback.onResult(null, t2);
                }
            }
        }
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.command");
    }
}
