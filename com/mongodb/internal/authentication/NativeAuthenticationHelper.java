// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.internal.authentication;

import java.security.NoSuchAlgorithmException;
import com.mongodb.MongoInternalException;
import java.security.MessageDigest;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonInt32;
import org.bson.BsonDocument;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public final class NativeAuthenticationHelper
{
    private static final Charset UTF_8_CHARSET;
    
    public static String createAuthenticationHash(final String userName, final char[] password) {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream(userName.length() + 20 + password.length);
        try {
            bout.write(userName.getBytes(NativeAuthenticationHelper.UTF_8_CHARSET));
            bout.write(":mongo:".getBytes(NativeAuthenticationHelper.UTF_8_CHARSET));
            for (final char ch : password) {
                if (ch >= '\u0080') {
                    throw new IllegalArgumentException("can't handle non-ascii passwords yet");
                }
                bout.write((byte)ch);
            }
        }
        catch (IOException ioe) {
            throw new RuntimeException("impossible", ioe);
        }
        return hexMD5(bout.toByteArray());
    }
    
    public static BsonDocument getAuthCommand(final String userName, final char[] password, final String nonce) {
        return getAuthCommand(userName, createAuthenticationHash(userName, password), nonce);
    }
    
    public static BsonDocument getAuthCommand(final String userName, final String authHash, final String nonce) {
        final String key = nonce + userName + authHash;
        final BsonDocument cmd = new BsonDocument();
        cmd.put("authenticate", new BsonInt32(1));
        cmd.put("user", new BsonString(userName));
        cmd.put("nonce", new BsonString(nonce));
        cmd.put("key", new BsonString(hexMD5(key.getBytes(NativeAuthenticationHelper.UTF_8_CHARSET))));
        return cmd;
    }
    
    public static BsonDocument getNonceCommand() {
        return new BsonDocument("getnonce", new BsonInt32(1));
    }
    
    static String hexMD5(final byte[] data) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(data);
            final byte[] digest = md5.digest();
            return toHex(digest);
        }
        catch (NoSuchAlgorithmException e) {
            throw new MongoInternalException("Error - this implementation of Java doesn't support MD5.", e);
        }
    }
    
    static String toHex(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (final byte aByte : bytes) {
            final String s = Integer.toHexString(0xFF & aByte);
            if (s.length() < 2) {
                sb.append("0");
            }
            sb.append(s);
        }
        return sb.toString();
    }
    
    static {
        UTF_8_CHARSET = Charset.forName("UTF-8");
    }
}
