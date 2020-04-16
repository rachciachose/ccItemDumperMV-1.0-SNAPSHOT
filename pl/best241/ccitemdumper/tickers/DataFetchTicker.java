// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper.tickers;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bson.Document;
import org.bukkit.inventory.Inventory;
import pl.best241.ccitemdumper.parser.InventorySerializer;
import pl.best241.ccitemdumper.Config;
import pl.best241.ccitemdumper.data.DataStore;
import pl.best241.ccitemdumper.CcItemDumper;
import org.bukkit.Bukkit;

public class DataFetchTicker
{
    public static void run() {
        Bukkit.getScheduler().runTaskTimer((Plugin)CcItemDumper.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final Long lastEqFetch = DataStore.lastEqUpdate.get(player.getUniqueId());
                    Label_0413: {
                        if (lastEqFetch == null || System.currentTimeMillis() - lastEqFetch >= Config.eqBackupTimeoutsInMillis) {
                            final Long fetchTime = System.currentTimeMillis();
                            final String serializedInventory = InventorySerializer.serializeInventory((Inventory)player.getInventory());
                            final String serializedEnderchest = InventorySerializer.serializeInventory(player.getEnderChest());
                            final String parsedHelmet = InventorySerializer.serializeItemStack(player.getInventory().getHelmet());
                            final String parsedChestplate = InventorySerializer.serializeItemStack(player.getInventory().getChestplate());
                            final String parsedLeggings = InventorySerializer.serializeItemStack(player.getInventory().getLeggings());
                            final String parsedBoots = InventorySerializer.serializeItemStack(player.getInventory().getBoots());
                            final Document lastFetched = DataStore.lastFetchedData.get(player.getUniqueId());
                            if (lastFetched != null) {
                                boolean shoudSkip = true;
                                if (!serializedEnderchest.equals(lastFetched.getString("enderchest"))) {
                                    shoudSkip = false;
                                }
                                if (!serializedInventory.equals(lastFetched.getString("inventory"))) {
                                    shoudSkip = false;
                                }
                                if (!DataFetchTicker.hasChanged(parsedHelmet, lastFetched.getString("helmet"))) {
                                    shoudSkip = false;
                                }
                                if (!DataFetchTicker.hasChanged(parsedChestplate, lastFetched.getString("chestplate"))) {
                                    shoudSkip = false;
                                }
                                if (!DataFetchTicker.hasChanged(parsedLeggings, lastFetched.getString("leggings"))) {
                                    shoudSkip = false;
                                }
                                if (!DataFetchTicker.hasChanged(parsedBoots, lastFetched.getString("boots"))) {
                                    shoudSkip = false;
                                }
                                if (shoudSkip) {
                                    break Label_0413;
                                }
                            }
                            final Document doc = new Document("uuid", player.getUniqueId().toString()).append("time", fetchTime).append("enderchest", serializedEnderchest).append("inventory", serializedInventory).append("helmet", parsedHelmet).append("chestplate", parsedChestplate).append("leggings", parsedLeggings).append("boots", parsedBoots);
                            DataStore.dataToInsert.put(doc, fetchTime);
                            DataStore.lastFetchedData.put(player.getUniqueId(), doc);
                            DataStore.lastEqUpdate.put(player.getUniqueId(), fetchTime);
                        }
                    }
                }
            }
        }, 600L, 600L);
    }
    
    public static boolean hasChanged(final String itemBefore, final String itemAfter) {
        return (itemBefore == null && itemAfter == null) || itemBefore == null || itemAfter == null || itemBefore.equals(itemAfter);
    }
}
