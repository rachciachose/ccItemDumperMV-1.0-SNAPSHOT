// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper;

import java.util.concurrent.TimeUnit;

public class Config
{
    public static final String lastUpdateTimePubSubChannelName = "ccItemDumper.lastUpdateTime";
    public static final long fetchTimeUnitInTicks = 600L;
    public static final long insertTimeUnitInTicks;
    public static final long eqBackupTimeoutsInMillis;
    public static final String setInventoryChannelName = "ccItemDumper.setInventory";
    
    static {
        insertTimeUnitInTicks = TimeUnit.MINUTES.toSeconds(1L) * 20L;
        eqBackupTimeoutsInMillis = TimeUnit.MINUTES.toMillis(2L);
    }
}
