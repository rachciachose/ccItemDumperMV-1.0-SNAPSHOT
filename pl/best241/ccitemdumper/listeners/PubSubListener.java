// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;
import pl.best241.ccitemdumper.parser.InventorySerializer;
import org.bukkit.Bukkit;
import pl.best241.ccitemdumper.data.InventoryType;
import pl.best241.ccitemdumper.data.DataStore;
import java.util.UUID;
import pl.best241.rdbplugin.events.PubSubRecieveMessageEvent;
import org.bukkit.event.Listener;

public class PubSubListener implements Listener
{
    @EventHandler
    public static void onMessage(final PubSubRecieveMessageEvent event) {
        if (event.getChannel().equals("ccItemDumper.lastUpdateTime")) {
            final String[] parsedData = event.getMessage().split(":");
            final UUID uuid = UUID.fromString(parsedData[0]);
            final Long lastUpdateTime = Long.valueOf(parsedData[1]);
            DataStore.lastEqUpdate.put(uuid, lastUpdateTime);
        }
        else if (event.getChannel().equals("ccItemDumper.setInventory")) {
            final String[] parsedData = event.getMessage().split("polak//##//cebula");
            final InventoryType inventoryType = InventoryType.valueOf(parsedData[0]);
            final UUID uuid2 = UUID.fromString(parsedData[1]);
            final Player player = Bukkit.getPlayer(uuid2);
            if (player != null && player.isOnline()) {
                if (inventoryType == InventoryType.INVENTORY) {
                    final Inventory inv = InventorySerializer.deserializeInventory(parsedData[2]);
                    for (int i = 0; i < inv.getSize(); ++i) {
                        player.getInventory().setItem(i, inv.getItem(i));
                    }
                }
                if (inventoryType == InventoryType.ENDERCHEST) {
                    final Inventory inv = InventorySerializer.deserializeInventory(parsedData[2]);
                    for (int i = 0; i < inv.getSize(); ++i) {
                        player.getEnderChest().setItem(i, inv.getItem(i));
                    }
                }
                if (inventoryType == InventoryType.ARMOR) {
                    final ItemStack helmet = InventorySerializer.deserializeItemStack(parsedData[2]);
                    final ItemStack chestplate = InventorySerializer.deserializeItemStack(parsedData[3]);
                    final ItemStack leggings = InventorySerializer.deserializeItemStack(parsedData[4]);
                    final ItemStack boots = InventorySerializer.deserializeItemStack(parsedData[5]);
                    player.getInventory().setHelmet(helmet);
                    player.getInventory().setChestplate(chestplate);
                    player.getInventory().setLeggings(leggings);
                    player.getInventory().setBoots(boots);
                }
                if (inventoryType == InventoryType.ALL) {
                    final Inventory inv = InventorySerializer.deserializeInventory(parsedData[2]);
                    for (int i = 0; i < inv.getSize(); ++i) {
                        player.getInventory().setItem(i, inv.getItem(i));
                    }
                    final Inventory ender = InventorySerializer.deserializeInventory(parsedData[3]);
                    for (int j = 0; j < ender.getSize(); ++j) {
                        player.getEnderChest().setItem(j, ender.getItem(j));
                    }
                    final ItemStack helmet2 = InventorySerializer.deserializeItemStack(parsedData[4]);
                    final ItemStack chestplate2 = InventorySerializer.deserializeItemStack(parsedData[5]);
                    final ItemStack leggings2 = InventorySerializer.deserializeItemStack(parsedData[6]);
                    final ItemStack boots2 = InventorySerializer.deserializeItemStack(parsedData[7]);
                    player.getInventory().setHelmet(helmet2);
                    player.getInventory().setChestplate(chestplate2);
                    player.getInventory().setLeggings(leggings2);
                    player.getInventory().setBoots(boots2);
                }
            }
        }
    }
}
