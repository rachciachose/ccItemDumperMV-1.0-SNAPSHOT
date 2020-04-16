// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.management;

import com.mongodb.internal.management.jmx.JMXMBeanServer;

public final class MBeanServerFactory
{
    private static final MBeanServer M_BEAN_SERVER;
    
    public static MBeanServer getMBeanServer() {
        return MBeanServerFactory.M_BEAN_SERVER;
    }
    
    static {
        MBeanServer tmp;
        try {
            tmp = new JMXMBeanServer();
        }
        catch (Throwable e) {
            tmp = new NullMBeanServer();
        }
        M_BEAN_SERVER = tmp;
    }
}
