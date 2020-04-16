// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper.listeners;

import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import pl.best241.ccitemdumper.data.DataStore;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;

public class PlayerQuitKickListener implements Listener
{
    @EventHandler
    public static void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        DataStore.lastEqUpdate.remove(player.getUniqueId());
    }
    
    @EventHandler
    public static void onPlayerKick(final PlayerKickEvent event) {
        final Player player = event.getPlayer();
        DataStore.lastEqUpdate.remove(player.getUniqueId());
    }
}
