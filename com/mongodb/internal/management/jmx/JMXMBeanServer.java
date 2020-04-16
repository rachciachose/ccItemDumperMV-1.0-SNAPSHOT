// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.internal.management.jmx;

import com.mongodb.diagnostics.logging.Loggers;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import com.mongodb.diagnostics.logging.Logger;
import com.mongodb.management.MBeanServer;

public class JMXMBeanServer implements MBeanServer
{
    private static final Logger LOGGER;
    private final javax.management.MBeanServer server;
    
    public JMXMBeanServer() {
        this.server = ManagementFactory.getPlatformMBeanServer();
    }
    
    @Override
    public void registerMBean(final Object mBean, final String mBeanName) {
        try {
            this.server.registerMBean(mBean, new ObjectName(mBeanName));
        }
        catch (Exception e) {
            JMXMBeanServer.LOGGER.warn("Unable to register MBean " + mBeanName, e);
        }
    }
    
    @Override
    public void unregisterMBean(final String mBeanName) {
        try {
            final ObjectName objectName = new ObjectName(mBeanName);
            if (this.server.isRegistered(objectName)) {
                this.server.unregisterMBean(objectName);
            }
        }
        catch (Exception e) {
            JMXMBeanServer.LOGGER.warn("Unable to unregister MBean " + mBeanName, e);
        }
    }
    
    static {
        LOGGER = Loggers.getLogger("management");
    }
}
