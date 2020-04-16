// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.internal.connection;

import javax.net.ssl.SSLParameters;

public final class SslHelper
{
    public static SSLParameters enableHostNameVerification(final SSLParameters sslParameters) {
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
        return sslParameters;
    }
}
