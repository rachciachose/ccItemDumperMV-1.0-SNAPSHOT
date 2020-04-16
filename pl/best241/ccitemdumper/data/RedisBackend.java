// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper.data;

import redis.clients.jedis.Jedis;
import pl.best241.rdbplugin.JedisFactory;
import java.util.UUID;

public class RedisBackend
{
    public static Long getLastEqUpdateTime(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        String hget = null;
        try {
            final Boolean hexists = jedis.hexists("ccItemDeposit.lastEqUpdateTime", uuid.toString());
            if (hexists) {
                hget = jedis.hget("ccItemDeposit.lastEqUpdateTime", uuid.toString());
            }
            JedisFactory.getInstance().returnJedis(jedis);
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
            JedisFactory.getInstance().returnBrokenJedis(jedis);
        }
        if (hget == null) {
            return null;
        }
        return Long.valueOf(hget);
    }
    
    public static void setLastFetched(final UUID uuid, final String data) {
    }
}
