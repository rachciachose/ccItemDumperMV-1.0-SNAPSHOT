// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.util;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class Util
{
    public static String toHex(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : bytes) {
            final String s = Integer.toHexString(0xFF & b);
            if (s.length() < 2) {
                sb.append("0");
            }
            sb.append(s);
        }
        return sb.toString();
    }
    
    public static String hexMD5(final byte[] data) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(data);
            final byte[] digest = md5.digest();
            return toHex(digest);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error - this implementation of Java doesn't support MD5.");
        }
    }
    
    public static String hexMD5(final ByteBuffer buf, final int offset, final int len) {
        final byte[] b = new byte[len];
        for (int i = 0; i < len; ++i) {
            b[i] = buf.get(offset + i);
        }
        return hexMD5(b);
    }
}
