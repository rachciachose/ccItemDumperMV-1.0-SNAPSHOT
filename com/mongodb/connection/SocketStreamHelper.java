// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.connection;

import java.io.IOException;
import java.net.SocketAddress;
import com.mongodb.internal.connection.SslHelper;
import com.mongodb.MongoInternalException;
import javax.net.ssl.SSLSocket;
import java.util.concurrent.TimeUnit;
import com.mongodb.ServerAddress;
import java.net.Socket;

final class SocketStreamHelper
{
    static void initialize(final Socket socket, final ServerAddress address, final SocketSettings settings, final SslSettings sslSettings) throws IOException {
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(settings.getReadTimeout(TimeUnit.MILLISECONDS));
        socket.setKeepAlive(settings.isKeepAlive());
        if (settings.getReceiveBufferSize() > 0) {
            socket.setReceiveBufferSize(settings.getReceiveBufferSize());
        }
        if (settings.getSendBufferSize() > 0) {
            socket.setSendBufferSize(settings.getSendBufferSize());
        }
        if (sslSettings.isEnabled()) {
            if (!(socket instanceof SSLSocket)) {
                throw new MongoInternalException("SSL is enabled but the socket is not an instance of javax.net.ssl.SSLSocket");
            }
            if (!sslSettings.isInvalidHostNameAllowed()) {
                final SSLSocket sslSocket = (SSLSocket)socket;
                sslSocket.setSSLParameters(SslHelper.enableHostNameVerification(sslSocket.getSSLParameters()));
            }
        }
        socket.connect(address.getSocketAddress(), settings.getConnectTimeout(TimeUnit.MILLISECONDS));
    }
}
