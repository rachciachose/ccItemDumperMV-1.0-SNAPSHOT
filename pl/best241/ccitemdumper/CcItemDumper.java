// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper;

import java.util.Iterator;
import pl.best241.ccsectors.data.PlayerData;
import pl.best241.rdbplugin.pubsub.PubSub;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bson.Document;
import pl.best241.ccitemdumper.parser.InventorySerializer;
import java.util.UUID;
import org.bukkit.Bukkit;
import pl.best241.ccsectors.api.CcSectorsAPI;
import org.apache.commons.lang.StringUtils;
import pl.best241.ccitemdumper.data.InventoryType;
import org.bukkit.entity.Player;
import java.util.Date;
import pl.best241.ccsectors.CcSectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.best241.ccitemdumper.managers.MongoDBManager;
import pl.best241.ccitemdumper.listeners.PubSubListener;
import pl.best241.ccitemdumper.listeners.PlayerQuitKickListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import pl.best241.ccitemdumper.listeners.PlayerJoinListener;
import pl.best241.ccitemdumper.tickers.DataInsertTicker;
import pl.best241.ccitemdumper.tickers.DataFetchTicker;
import pl.best241.ccitemdumper.config.ConfigManager;
import pl.best241.ccsectors.data.SectorType;
import pl.best241.ccsectors.managers.SectorManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CcItemDumper extends JavaPlugin
{
    private static CcItemDumper plugin;
    public static final String pubsubSeparator = "polak//##//cebula";
    
    public void onEnable() {
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
            this.setEnabled(false);
            return;
        }
        new ConfigManager(this);
        CcItemDumper.plugin = this;
        DataFetchTicker.run();
        DataInsertTicker.run();
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerJoinListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerQuitKickListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PubSubListener(), (Plugin)this);
        MongoDBManager.createIndexes();
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("itemdumper") && sender.hasPermission("ccItemDumper.backupItems")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("size")) {
                    sender.sendMessage("Rozmiar bazy danych: " + MongoDBManager.getPlayerInventoriesSize());
                }
                else if (args[0].equalsIgnoreCase("list")) {
                    if (args.length == 2) {
                        final String nick = args[1];
                        final UUID playerLastUUID = CcSectors.getBackend().getPlayerLastUUID(nick);
                        if (playerLastUUID == null) {
                            sender.sendMessage("Nie znaleziono uuid gracza!");
                            return false;
                        }
                        final ArrayList<Long> timesOfBackups = MongoDBManager.getTimesOfBackups(playerLastUUID);
                        int number = 1;
                        for (final Long time : timesOfBackups) {
                            final String parsedData = new Date(time).toString();
                            sender.sendMessage(number + ". " + parsedData);
                            ++number;
                        }
                    }
                    else {
                        sender.sendMessage("Uzycie: /itemdumper list nick");
                    }
                }
                else if (args[0].equalsIgnoreCase("view")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("Komenda dostepna tylko w grze!");
                        return false;
                    }
                    final Player player = (Player)sender;
                    InventoryType inventoryType = InventoryType.INVENTORY;
                    String nick2;
                    String numberParsed;
                    if (args.length == 3) {
                        nick2 = args[1];
                        numberParsed = args[2];
                    }
                    else {
                        if (args.length != 4) {
                            sender.sendMessage("Uzycie: /itemdumper view nick number [ENDERCHEST|INVENTORY|ARMOR]");
                            return false;
                        }
                        nick2 = args[1];
                        numberParsed = args[2];
                        if (!args[3].equalsIgnoreCase("enderchest") && !args[3].equalsIgnoreCase("inventory") && !args[3].equalsIgnoreCase("armor")) {
                            sender.sendMessage("Uzycie: /itemdumper view nick number [ENDERCHEST|INVENTORY|ARMOR]");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("enderchest")) {
                            inventoryType = InventoryType.ENDERCHEST;
                        }
                        else if (args[3].equalsIgnoreCase("inventory")) {
                            inventoryType = InventoryType.INVENTORY;
                        }
                        else if (args[3].equalsIgnoreCase("armor")) {
                            inventoryType = InventoryType.ARMOR;
                        }
                    }
                    final InventoryType inventoryTypeFinal = inventoryType;
                    if (StringUtils.isNumeric(numberParsed)) {
                        final int number2 = Integer.parseInt(numberParsed) - 1;
                        final UUID uuid = CcSectorsAPI.getUUID(nick2);
                        if (uuid == null) {
                            sender.sendMessage("Gracz nie istnieje!");
                        }
                        if (number2 < 0) {
                            sender.sendMessage("Niepoprawny numer!");
                            return false;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously((Plugin)getPlugin(), (Runnable)new Runnable() {
                            @Override
                            public void run() {
                                Inventory deserializedInventory = null;
                                if (null != inventoryTypeFinal) {
                                    switch (inventoryTypeFinal) {
                                        case ENDERCHEST: {
                                            final ArrayList<String> parsedData = MongoDBManager.getParsedEnders(uuid);
                                            if (parsedData.size() <= number2) {
                                                sender.sendMessage("Niepoprawny numer!");
                                                return;
                                            }
                                            deserializedInventory = InventorySerializer.deserializeInventory(parsedData.get(number2));
                                            break;
                                        }
                                        case INVENTORY: {
                                            final ArrayList<String> parsedData = MongoDBManager.getParsedInventories(uuid);
                                            if (parsedData.size() <= number2) {
                                                sender.sendMessage("Niepoprawny numer!");
                                                return;
                                            }
                                            deserializedInventory = InventorySerializer.deserializeInventory(parsedData.get(number2));
                                            break;
                                        }
                                        case ARMOR: {
                                            final ArrayList<Document> documents = MongoDBManager.getDocuments(uuid);
                                            if (documents.size() <= number2) {
                                                sender.sendMessage("Niepoprawny numer!");
                                                return;
                                            }
                                            final Document document = documents.get(number2);
                                            final ItemStack helmet = InventorySerializer.deserializeItemStack(document.getString("helmet"));
                                            final ItemStack chestplate = InventorySerializer.deserializeItemStack(document.getString("chestplate"));
                                            final ItemStack leggings = InventorySerializer.deserializeItemStack(document.getString("leggings"));
                                            final ItemStack boots = InventorySerializer.deserializeItemStack(document.getString("boots"));
                                            final Inventory inv = Bukkit.createInventory((InventoryHolder)null, org.bukkit.event.inventory.InventoryType.HOPPER);
                                            inv.setItem(0, helmet);
                                            inv.setItem(1, chestplate);
                                            inv.setItem(2, leggings);
                                            inv.setItem(3, boots);
                                            deserializedInventory = inv;
                                            break;
                                        }
                                    }
                                }
                                player.openInventory(deserializedInventory);
                            }
                        });
                    }
                    else {
                        sender.sendMessage("Uzycie: /itemdumper view nick number [ENDERCHEST|INVENTORY|ARMOR]");
                    }
                }
                else if (args[0].equalsIgnoreCase("restore")) {
                    final Player player = (Player)sender;
                    InventoryType inventoryType = InventoryType.ALL;
                    String nick2;
                    String numberParsed;
                    if (args.length == 3) {
                        nick2 = args[1];
                        numberParsed = args[2];
                    }
                    else {
                        if (args.length != 4) {
                            sender.sendMessage("Uzycie: /itemdumper restore nick number [ENDERCHEST|INVENTORY|ARMOR]");
                            return false;
                        }
                        nick2 = args[1];
                        numberParsed = args[2];
                        if (!args[3].equalsIgnoreCase("enderchest") && !args[3].equalsIgnoreCase("inventory") && !args[3].equalsIgnoreCase("armor")) {
                            sender.sendMessage("Uzycie: /itemdumper restore nick number [ENDERCHEST|INVENTORY|ARMOR]");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("enderchest")) {
                            inventoryType = InventoryType.ENDERCHEST;
                        }
                        else if (args[3].equalsIgnoreCase("inventory")) {
                            inventoryType = InventoryType.INVENTORY;
                        }
                        else if (args[3].equalsIgnoreCase("armor")) {
                            inventoryType = InventoryType.ARMOR;
                        }
                    }
                    final InventoryType inventoryTypeFinal = inventoryType;
                    if (StringUtils.isNumeric(numberParsed)) {
                        final int number2 = Integer.parseInt(numberParsed) - 1;
                        final UUID uuid = CcSectorsAPI.getUUID(nick2);
                        if (uuid == null) {
                            sender.sendMessage("Gracz nie istnieje!");
                        }
                        if (number2 < 0) {
                            sender.sendMessage("Niepoprawny numer!");
                            return false;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously((Plugin)getPlugin(), (Runnable)new Runnable() {
                            @Override
                            public void run() {
                                Inventory deserializedInventory = null;
                                if (null != inventoryTypeFinal) {
                                    switch (inventoryTypeFinal) {
                                        case ENDERCHEST: {
                                            final ArrayList<String> parsedData = MongoDBManager.getParsedEnders(uuid);
                                            if (parsedData.size() <= number2) {
                                                sender.sendMessage("Niepoprawny numer!");
                                                return;
                                            }
                                            if (CcSectorsAPI.isPlayerOnline(uuid)) {
                                                PubSub.broadcast("ccItemDumper.setInventory", InventoryType.ENDERCHEST + "polak//##//cebula" + uuid.toString() + "polak//##//cebula" + parsedData.get(number2));
                                                break;
                                            }
                                            deserializedInventory = InventorySerializer.deserializeInventory(parsedData.get(number2));
                                            final PlayerData playerData = CcSectors.getBackend().getPlayerData(uuid);
                                            playerData.updateEnderchest(deserializedInventory);
                                            CcSectors.getBackend().setPlayerData(uuid, playerData);
                                            break;
                                        }
                                        case INVENTORY: {
                                            final ArrayList<String> parsedData = MongoDBManager.getParsedInventories(uuid);
                                            if (parsedData.size() <= number2) {
                                                sender.sendMessage("Niepoprawny numer!");
                                                return;
                                            }
                                            if (CcSectorsAPI.isPlayerOnline(uuid)) {
                                                PubSub.broadcast("ccItemDumper.setInventory", InventoryType.INVENTORY + "polak//##//cebula" + uuid.toString() + "polak//##//cebula" + parsedData.get(number2));
                                                break;
                                            }
                                            deserializedInventory = InventorySerializer.deserializeInventory(parsedData.get(number2));
                                            final PlayerData playerData = CcSectors.getBackend().getPlayerData(uuid);
                                            playerData.updateInventory(deserializedInventory);
                                            CcSectors.getBackend().setPlayerData(uuid, playerData);
                                            break;
                                        }
                                        case ARMOR: {
                                            final ArrayList<Document> documents = MongoDBManager.getDocuments(uuid);
                                            if (documents.size() <= number2) {
                                                sender.sendMessage("Niepoprawny numer!");
                                                return;
                                            }
                                            final Document document = documents.get(number2);
                                            final ItemStack helmet = InventorySerializer.deserializeItemStack(document.getString("helmet"));
                                            final ItemStack chestplate = InventorySerializer.deserializeItemStack(document.getString("chestplate"));
                                            final ItemStack leggings = InventorySerializer.deserializeItemStack(document.getString("leggings"));
                                            final ItemStack boots = InventorySerializer.deserializeItemStack(document.getString("boots"));
                                            if (CcSectorsAPI.isPlayerOnline(uuid)) {
                                                PubSub.broadcast("ccItemDumper.setInventory", InventoryType.ARMOR + "polak//##//cebula" + uuid.toString() + "polak//##//cebula" + document.getString("helmet") + "polak//##//cebula" + document.getString("chestplate") + "polak//##//cebula" + document.getString("leggings") + "polak//##//cebula" + document.getString("boots"));
                                                break;
                                            }
                                            final PlayerData playerData2 = CcSectors.getBackend().getPlayerData(uuid);
                                            playerData2.getArmor().setHelmet(helmet);
                                            playerData2.getArmor().setChestplate(chestplate);
                                            playerData2.getArmor().setLeggins(leggings);
                                            playerData2.getArmor().setBoots(boots);
                                            CcSectors.getBackend().setPlayerData(uuid, playerData2);
                                            break;
                                        }
                                        case ALL: {
                                            final ArrayList<Document> documents = MongoDBManager.getDocuments(uuid);
                                            if (documents.size() <= number2) {
                                                sender.sendMessage("Niepoprawny numer!");
                                                return;
                                            }
                                            final Document document = documents.get(number2);
                                            final ItemStack helmet = InventorySerializer.deserializeItemStack(document.getString("helmet"));
                                            final ItemStack chestplate = InventorySerializer.deserializeItemStack(document.getString("chestplate"));
                                            final ItemStack leggings = InventorySerializer.deserializeItemStack(document.getString("leggings"));
                                            final ItemStack boots = InventorySerializer.deserializeItemStack(document.getString("boots"));
                                            if (CcSectorsAPI.isPlayerOnline(uuid)) {
                                                PubSub.broadcast("ccItemDumper.setInventory", InventoryType.ALL + "polak//##//cebula" + uuid.toString() + "polak//##//cebula" + document.getString("inventory") + "polak//##//cebula" + document.getString("enderchest") + "polak//##//cebula" + document.getString("helmet") + "polak//##//cebula" + document.getString("chestplate") + "polak//##//cebula" + document.getString("leggings") + "polak//##//cebula" + document.getString("boots"));
                                                break;
                                            }
                                            final Inventory deserializedPlayerInventory = InventorySerializer.deserializeInventory(document.getString("inventory"));
                                            final Inventory deserializedPlayerEnderchest = InventorySerializer.deserializeInventory(document.getString("enderchest"));
                                            final PlayerData playerData3 = CcSectors.getBackend().getPlayerData(uuid);
                                            playerData3.updateEnderchest(deserializedPlayerEnderchest);
                                            playerData3.updateInventory(deserializedPlayerInventory);
                                            playerData3.getArmor().setHelmet(helmet);
                                            playerData3.getArmor().setChestplate(chestplate);
                                            playerData3.getArmor().setLeggins(leggings);
                                            playerData3.getArmor().setBoots(boots);
                                            CcSectors.getBackend().setPlayerData(uuid, playerData3);
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                    else {
                        sender.sendMessage("Uzycie: /itemdumper restore nick number [ENDERCHEST|INVENTORY|ARMOR]");
                    }
                }
                else {
                    sender.sendMessage("Uzycie: /itemdumper view|list|size|restore");
                }
            }
            else {
                sender.sendMessage("Uzycie: /itemdumper view|list|size|restore");
            }
        }
        return false;
    }
    
    public void onDisable() {
        DataInsertTicker.insert();
    }
    
    public static CcItemDumper getPlugin() {
        return CcItemDumper.plugin;
    }
    
    public static void main(final String[] args) {
        MongoDBManager.createIndexes();
    }
}
