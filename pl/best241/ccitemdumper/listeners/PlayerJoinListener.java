// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import pl.best241.ccitemdumper.data.DataStore;
import pl.best241.ccitemdumper.data.RedisBackend;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class PlayerJoinListener implements Listener
{
    @EventHandler
    public static void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Long lastEqUpdateTime = RedisBackend.getLastEqUpdateTime(player.getUniqueId());
        if (lastEqUpdateTime != null) {
            DataStore.lastEqUpdate.put(player.getUniqueId(), lastEqUpdateTime);
        }
    }
}
