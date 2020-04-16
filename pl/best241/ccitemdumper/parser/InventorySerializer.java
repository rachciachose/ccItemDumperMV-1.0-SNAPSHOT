// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper.parser;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import java.util.Collection;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

public class InventorySerializer
{
    private static final String item_start = "#";
    private static final String item_separator = ";";
    private static final String attribute_start = ":";
    private static final String attribute_separator = "@";
    
    public static String convertSymbolsForSaving(String str) {
        str = str.replaceAll(";", "<item_separator>").replaceAll("#", "<item_start>").replaceAll(":", "<attribute_start>").replaceAll("@", "<attribute_separator>").replaceAll("\n", "<Nline>").replaceAll("\r", "<Rline>").replaceAll("(?i)§b", "&b").replaceAll("§0", "&0").replaceAll("§9", "&9").replaceAll("(?i)§l", "&l").replaceAll("§3", "&3").replaceAll("§1", "&1").replaceAll("§8", "&8").replaceAll("§2", "&2").replaceAll("§5", "&5").replaceAll("§4", "&4").replaceAll("§6", "&6").replaceAll("§7", "&7").replaceAll("(?i)§a", "&a").replaceAll("(?i)§o", "&o").replaceAll("(?i)§d", "&d").replaceAll("(?i)§k", "&k").replaceAll("(?i)§c", "&c").replaceAll("(?i)§m", "&m").replaceAll("(?i)§n", "&n").replaceAll("(?i)§f", "&f").replaceAll("(?i)§e", "&e").replaceAll("(?i)§r", "&r");
        return str;
    }
    
    public static String convertSymbolsForLoading(String str) {
        str = str.replaceAll("<item_separator>", ";").replaceAll("<item_start>", "#").replaceAll("<attribute_start>", ":").replaceAll("<attribute_separator>", "@").replaceAll("<Nline>", "\n").replaceAll("<Rline>", "\r").replaceAll("(?i)&0", ChatColor.BLACK + "").replaceAll("(?i)&9", ChatColor.BLUE + "").replaceAll("(?i)&l", ChatColor.BOLD + "").replaceAll("(?i)&3", ChatColor.DARK_AQUA + "").replaceAll("(?i)&1", ChatColor.DARK_BLUE + "").replaceAll("(?i)&8", ChatColor.DARK_GRAY + "").replaceAll("(?i)&2", ChatColor.DARK_GREEN + "").replaceAll("(?i)&5", ChatColor.DARK_PURPLE + "").replaceAll("(?i)&4", ChatColor.DARK_RED + "").replaceAll("(?i)&6", ChatColor.GOLD + "").replaceAll("(?i)&7", ChatColor.GRAY + "").replaceAll("(?i)&a", ChatColor.GREEN + "").replaceAll("(?i)&b", ChatColor.AQUA + "").replaceAll("(?i)&o", ChatColor.ITALIC + "").replaceAll("(?i)&d", ChatColor.LIGHT_PURPLE + "").replaceAll("(?i)&k", ChatColor.MAGIC + "").replaceAll("(?i)&c", ChatColor.RED + "").replaceAll("(?i)&m", ChatColor.STRIKETHROUGH + "").replaceAll("(?i)&n", ChatColor.UNDERLINE + "").replaceAll("(?i)&f", ChatColor.WHITE + "").replaceAll("(?i)&e", ChatColor.YELLOW + "").replaceAll("(?i)&r", ChatColor.RESET + "");
        return str;
    }
    
    public static ItemStack deserializeItemStack(final String invString) {
        if (invString == null) {
            return null;
        }
        ItemStack is = null;
        Boolean createdItemStack = false;
        final String[] split;
        final String[] serializedItemStack = split = invString.split(":");
        for (final String itemInfo : split) {
            final String[] itemAttribute = itemInfo.split("@");
            if (itemAttribute[0].equals("t")) {
                is = new ItemStack(Material.getMaterial((int)Integer.valueOf(itemAttribute[1])));
                createdItemStack = true;
            }
            if (createdItemStack) {
                if (itemAttribute[0].equals("d")) {
                    is.setDurability((short)Short.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("a")) {
                    is.setAmount((int)Integer.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("n")) {
                    ItemMeta meta = null;
                    if (is.getItemMeta() == null) {
                        meta = Bukkit.getServer().getItemFactory().getItemMeta(is.getType());
                    }
                    else {
                        meta = is.getItemMeta();
                    }
                    meta.setDisplayName(convertSymbolsForLoading(itemAttribute[1]));
                    is.setItemMeta(meta);
                }
                else if (itemAttribute[0].equals("e")) {
                    is.addUnsafeEnchantment(Enchantment.getById((int)Integer.valueOf(itemAttribute[1])), (int)Integer.valueOf(itemAttribute[2]));
                }
                else if (itemAttribute[0].equals("l")) {
                    final ArrayList<String> lores = new ArrayList<String>();
                    for (int j = 1; j < itemAttribute.length; ++j) {
                        lores.add(convertSymbolsForLoading(itemAttribute[j]));
                    }
                    ItemMeta meta2 = null;
                    if (is.getItemMeta() == null) {
                        meta2 = Bukkit.getServer().getItemFactory().getItemMeta(is.getType());
                    }
                    else {
                        meta2 = is.getItemMeta();
                    }
                    meta2.setLore((List)lores);
                    is.setItemMeta(meta2);
                }
                else if (itemAttribute[0].equals("b")) {
                    is = deserializeBook(is, itemAttribute);
                }
            }
        }
        return is;
    }
    
    public static String serializeItemStack(final ItemStack is) {
        if (is != null) {
            String serializedItemStack = new String();
            final String isType = String.valueOf(is.getType().getId());
            serializedItemStack = serializedItemStack + "t@" + isType;
            if (is.getDurability() != 0) {
                final String isDurability = String.valueOf(is.getDurability());
                serializedItemStack = serializedItemStack + ":d@" + isDurability;
            }
            if (is.getAmount() != 1) {
                final String isAmount = String.valueOf(is.getAmount());
                serializedItemStack = serializedItemStack + ":a@" + isAmount;
            }
            final Map<Enchantment, Integer> isEnch = (Map<Enchantment, Integer>)is.getEnchantments();
            if (isEnch.size() > 0) {
                for (final Map.Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
                    serializedItemStack = serializedItemStack + ":e@" + ench.getKey().getId() + "@" + ench.getValue();
                }
            }
            if (is.getItemMeta().hasDisplayName()) {
                serializedItemStack = serializedItemStack + ":n@" + convertSymbolsForSaving(is.getItemMeta().getDisplayName());
            }
            if (is.getItemMeta().hasLore()) {
                final Iterator<String> it = is.getItemMeta().getLore().iterator();
                String lores = ":l";
                if (!it.hasNext()) {
                    lores += "@";
                }
                while (it.hasNext()) {
                    lores = lores + "@" + convertSymbolsForSaving(it.next());
                }
                serializedItemStack += lores;
            }
            if (is.getType() == Material.BOOK_AND_QUILL || is.getType() == Material.WRITTEN_BOOK) {
                serializedItemStack += serializeBook(is);
            }
            else if (is.getType() == Material.ENCHANTED_BOOK) {
                serializedItemStack += serializeEnchantedBook(is);
            }
            return serializedItemStack;
        }
        return null;
    }
    
    public static String serializeInventory(final Inventory inv) {
        String serialization = inv.getSize() + ";" + inv.getTitle() + ";";
        for (int i = 0; i < inv.getSize(); ++i) {
            final ItemStack is = inv.getItem(i);
            if (is != null) {
                String serializedItemStack = new String();
                final String isType = String.valueOf(is.getType().getId());
                serializedItemStack = serializedItemStack + "t@" + isType;
                if (is.getDurability() != 0) {
                    final String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack = serializedItemStack + ":d@" + isDurability;
                }
                if (is.getAmount() != 1) {
                    final String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack = serializedItemStack + ":a@" + isAmount;
                }
                final Map<Enchantment, Integer> isEnch = (Map<Enchantment, Integer>)is.getEnchantments();
                if (isEnch.size() > 0) {
                    for (final Map.Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
                        serializedItemStack = serializedItemStack + ":e@" + ench.getKey().getId() + "@" + ench.getValue();
                    }
                }
                if (is.getItemMeta().hasDisplayName()) {
                    serializedItemStack = serializedItemStack + ":n@" + convertSymbolsForSaving(is.getItemMeta().getDisplayName());
                }
                if (is.getItemMeta().hasLore()) {
                    final Iterator<String> it = is.getItemMeta().getLore().iterator();
                    String lores = ":l";
                    if (!it.hasNext()) {
                        lores += "@";
                    }
                    while (it.hasNext()) {
                        lores = lores + "@" + convertSymbolsForSaving(it.next());
                    }
                    serializedItemStack += lores;
                }
                if (is.getType() == Material.BOOK_AND_QUILL || is.getType() == Material.WRITTEN_BOOK) {
                    serializedItemStack += serializeBook(is);
                }
                else if (is.getType() == Material.ENCHANTED_BOOK) {
                    serializedItemStack += serializeEnchantedBook(is);
                }
                serialization = serialization + i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
    }
    
    public static Inventory deserializeInventory(final String invString) {
        if (invString == null) {
            return null;
        }
        return deserializeInventory(invString, (Player)null);
    }
    
    public static Inventory deserializeInventory(final String invString, final Player player) {
        if (invString == null) {
            return null;
        }
        if (!invString.equals("")) {
            final String[] serializedBlocks = invString.split(";");
            System.out.println(serializedBlocks[0]);
            final Inventory deserializedInventory = Bukkit.getServer().createInventory((InventoryHolder)player, (int)Integer.valueOf(serializedBlocks[0]), String.valueOf(serializedBlocks[1]));
            for (int i = 2; i < serializedBlocks.length; ++i) {
                final String[] serializedBlock = serializedBlocks[i].split("#");
                final int stackPosition = Integer.valueOf(serializedBlock[0]);
                if (stackPosition < deserializedInventory.getSize()) {
                    ItemStack is = null;
                    Boolean createdItemStack = false;
                    final String[] split;
                    final String[] serializedItemStack = split = serializedBlock[1].split(":");
                    for (final String itemInfo : split) {
                        final String[] itemAttribute = itemInfo.split("@");
                        if (itemAttribute[0].equals("t")) {
                            is = new ItemStack(Material.getMaterial((int)Integer.valueOf(itemAttribute[1])));
                            createdItemStack = true;
                        }
                        if (createdItemStack) {
                            if (itemAttribute[0].equals("d")) {
                                is.setDurability((short)Short.valueOf(itemAttribute[1]));
                            }
                            else if (itemAttribute[0].equals("a")) {
                                is.setAmount((int)Integer.valueOf(itemAttribute[1]));
                            }
                            else if (itemAttribute[0].equals("n")) {
                                ItemMeta meta = null;
                                if (is.getItemMeta() == null) {
                                    meta = Bukkit.getServer().getItemFactory().getItemMeta(is.getType());
                                }
                                else {
                                    meta = is.getItemMeta();
                                }
                                meta.setDisplayName(convertSymbolsForLoading(itemAttribute[1]));
                                is.setItemMeta(meta);
                            }
                            else if (itemAttribute[0].equals("e")) {
                                is.addUnsafeEnchantment(Enchantment.getById((int)Integer.valueOf(itemAttribute[1])), (int)Integer.valueOf(itemAttribute[2]));
                            }
                            else if (itemAttribute[0].equals("z")) {
                                is = deserializeEnchantedBook(is, itemAttribute);
                            }
                            else if (itemAttribute[0].equals("l")) {
                                final ArrayList<String> lores = new ArrayList<String>();
                                for (int j = 1; j < itemAttribute.length; ++j) {
                                    lores.add(convertSymbolsForLoading(itemAttribute[j]));
                                }
                                ItemMeta meta2 = null;
                                if (is.getItemMeta() == null) {
                                    meta2 = Bukkit.getServer().getItemFactory().getItemMeta(is.getType());
                                }
                                else {
                                    meta2 = is.getItemMeta();
                                }
                                meta2.setLore((List)lores);
                                is.setItemMeta(meta2);
                            }
                            else if (itemAttribute[0].equals("b")) {
                                is = deserializeBook(is, itemAttribute);
                            }
                        }
                    }
                    deserializedInventory.setItem(stackPosition, is);
                }
            }
            return deserializedInventory;
        }
        if (player != null) {
            return (Inventory)player.getInventory();
        }
        return null;
    }
    
    public static Inventory deserializeInventory(final String invString, final String targetName) {
        if (invString == null) {
            return null;
        }
        if (!invString.equals("")) {
            final String[] serializedBlocks = invString.split(";");
            final Inventory deserializedInventory = Bukkit.getServer().createInventory((InventoryHolder)Bukkit.getPlayer(targetName), (int)Integer.valueOf(serializedBlocks[0]), String.valueOf(serializedBlocks[1]));
            for (int i = 2; i < serializedBlocks.length; ++i) {
                final String[] serializedBlock = serializedBlocks[i].split("#");
                final int stackPosition = Integer.valueOf(serializedBlock[0]);
                if (stackPosition < deserializedInventory.getSize()) {
                    ItemStack is = null;
                    Boolean createdItemStack = false;
                    final String[] split;
                    final String[] serializedItemStack = split = serializedBlock[1].split(":");
                    for (final String itemInfo : split) {
                        final String[] itemAttribute = itemInfo.split("@");
                        if (itemAttribute[0].equals("t")) {
                            is = new ItemStack(Material.getMaterial((int)Integer.valueOf(itemAttribute[1])));
                            createdItemStack = true;
                        }
                        if (createdItemStack) {
                            if (itemAttribute[0].equals("d")) {
                                is.setDurability((short)Short.valueOf(itemAttribute[1]));
                            }
                            else if (itemAttribute[0].equals("a")) {
                                is.setAmount((int)Integer.valueOf(itemAttribute[1]));
                            }
                            else if (itemAttribute[0].equals("n")) {
                                ItemMeta meta = null;
                                if (is.getItemMeta() == null) {
                                    meta = Bukkit.getServer().getItemFactory().getItemMeta(is.getType());
                                }
                                else {
                                    meta = is.getItemMeta();
                                }
                                meta.setDisplayName(convertSymbolsForLoading(itemAttribute[1]));
                                is.setItemMeta(meta);
                            }
                            else if (itemAttribute[0].equals("e")) {
                                is.addUnsafeEnchantment(Enchantment.getById((int)Integer.valueOf(itemAttribute[1])), (int)Integer.valueOf(itemAttribute[2]));
                            }
                            else if (itemAttribute[0].equals("l")) {
                                final ArrayList<String> lores = new ArrayList<String>();
                                for (int j = 1; j < itemAttribute.length; ++j) {
                                    lores.add(convertSymbolsForLoading(itemAttribute[j]));
                                }
                                ItemMeta meta2 = null;
                                if (is.getItemMeta() == null) {
                                    meta2 = Bukkit.getServer().getItemFactory().getItemMeta(is.getType());
                                }
                                else {
                                    meta2 = is.getItemMeta();
                                }
                                meta2.setLore((List)lores);
                                is.setItemMeta(meta2);
                            }
                            else if (itemAttribute[0].equals("b")) {
                                is = deserializeBook(is, itemAttribute);
                            }
                        }
                    }
                    deserializedInventory.setItem(stackPosition, is);
                }
            }
            return deserializedInventory;
        }
        return null;
    }
    
    public static String serializeInventory(final Player player, final String invToConvert) {
        if (player != null) {
            Inventory invInventory = null;
            if (invToConvert != null) {
                if (invToConvert.equalsIgnoreCase("inventory")) {
                    invInventory = (Inventory)player.getInventory();
                }
                else if (invToConvert.equalsIgnoreCase("armor")) {
                    final Inventory newInv = Bukkit.getServer().createInventory((InventoryHolder)player, 9);
                    int num = 0;
                    for (final ItemStack curItem : player.getInventory().getArmorContents()) {
                        newInv.setItem(num, curItem);
                        ++num;
                    }
                    invInventory = newInv;
                }
                else if (invToConvert.equalsIgnoreCase("enderchest")) {
                    invInventory = player.getEnderChest();
                }
                else {
                    invInventory = (Inventory)player.getInventory();
                }
            }
            else {
                invInventory = (Inventory)player.getInventory();
            }
            return serializeInventory(invInventory);
        }
        return "";
    }
    
    public static String serializeInventory(final Player player) {
        return serializeInventory(player, null);
    }
    
    public static String serializeBook(final ItemStack item) {
        String rtrn = ":b@";
        if (item.getType() == Material.BOOK_AND_QUILL || item.getType() == Material.WRITTEN_BOOK) {
            final BookMeta meta = (BookMeta)item.getItemMeta();
            final String author = meta.hasAuthor() ? meta.getAuthor() : null;
            final String title = meta.hasTitle() ? meta.getTitle() : null;
            if (author != null) {
                rtrn += convertSymbolsForSaving(author);
            }
            else {
                rtrn += "<NO-AUTHOR>";
            }
            if (title != null) {
                rtrn = rtrn + "@" + convertSymbolsForSaving(meta.getTitle());
            }
            else {
                rtrn += "@<NO-TITLE>";
            }
            if (meta.hasPages()) {
                final List<String> pages = (List<String>)meta.getPages();
                for (final String curPage : pages) {
                    rtrn = rtrn + "@" + convertSymbolsForSaving(curPage);
                }
            }
        }
        return rtrn.equals(":b@") ? "" : rtrn;
    }
    
    public static String serializeEnchantedBook(final ItemStack item) {
        String rtrn = "";
        if (item.getType() == Material.ENCHANTED_BOOK) {
            final EnchantmentStorageMeta EnchantMeta = (EnchantmentStorageMeta)item.getItemMeta();
            if (EnchantMeta != null) {
                final Map<Enchantment, Integer> isEnch = (Map<Enchantment, Integer>)EnchantMeta.getStoredEnchants();
                if (isEnch.size() > 0) {
                    for (final Map.Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
                        rtrn = rtrn + ":z@" + ench.getKey().getName() + "@" + ench.getValue();
                    }
                }
            }
        }
        return rtrn;
    }
    
    public static ItemStack deserializeEnchantedBook(ItemStack item, final String[] itemAttribute) {
        if (item == null && itemAttribute == null) {
            return new ItemStack(Material.ENCHANTED_BOOK);
        }
        if (item == null) {
            item = new ItemStack(Material.ENCHANTED_BOOK);
        }
        if (itemAttribute == null) {
            return item;
        }
        if (item.getType() == Material.ENCHANTED_BOOK) {
            final EnchantmentStorageMeta EnchantMeta = (EnchantmentStorageMeta)(item.hasItemMeta() ? item.getItemMeta() : Bukkit.getServer().getItemFactory().getItemMeta(item.getType()));
            if (itemAttribute[1] != null && itemAttribute[2] != null) {
                EnchantMeta.addStoredEnchant(Enchantment.getByName(itemAttribute[1]), (int)Integer.valueOf(itemAttribute[2]), true);
            }
            item.setItemMeta((ItemMeta)EnchantMeta);
        }
        return item;
    }
    
    public static ItemStack deserializeBook(ItemStack item, final String[] itemAttribute) {
        if (item == null && itemAttribute == null) {
            return new ItemStack(Material.BOOK_AND_QUILL);
        }
        if (item == null) {
            item = new ItemStack(Material.WRITTEN_BOOK);
        }
        if (itemAttribute == null) {
            return item;
        }
        if (item.getType() == Material.BOOK_AND_QUILL || item.getType() == Material.WRITTEN_BOOK) {
            final BookMeta meta = (BookMeta)(item.hasItemMeta() ? item.getItemMeta() : Bukkit.getServer().getItemFactory().getItemMeta(item.getType()));
            final String author = itemAttribute[1];
            final String title = itemAttribute[2];
            if (author == null) {
                item.setItemMeta((ItemMeta)meta);
                return item;
            }
            meta.setAuthor(author.equals("<NO-AUTHOR>") ? null : convertSymbolsForLoading(author));
            if (title == null) {
                item.setItemMeta((ItemMeta)meta);
                return item;
            }
            meta.setTitle(title.equals("<NO-TITLE>") ? null : convertSymbolsForLoading(title));
            for (int i = 3; i < itemAttribute.length; ++i) {
                if (itemAttribute[i] != null) {
                    meta.addPage(new String[] { convertSymbolsForLoading(itemAttribute[i]) });
                }
            }
            item.setItemMeta((ItemMeta)meta);
        }
        return item;
    }
    
    public static Inventory setTitle(final String str, final Inventory inv) {
        final Inventory newInv = Bukkit.getServer().createInventory(inv.getHolder(), inv.getSize(), str);
        newInv.setContents(inv.getContents());
        newInv.setMaxStackSize(inv.getMaxStackSize());
        return newInv;
    }
    
    public static String serializeExperience(final int level, final float exp) {
        String rtrn = "x@";
        rtrn = rtrn + level + "@" + exp;
        return rtrn.equals("x@") ? "" : (rtrn.equals("x@@") ? "" : rtrn);
    }
    
    public static String serializeExperience(final Player player) {
        return serializeExperience(player.getLevel(), player.getExp());
    }
    
    public static String[] deserializeExperience(final String serializedExp) {
        return serializedExp.replace("x@", "").split("@");
    }
    
    public static int deserializeLevel(final String serializedExp) {
        int rtrn = 0;
        int num = 0;
        for (final String curNum : deserializeExperience(serializedExp)) {
            ++num;
            boolean isInt = true;
            if (num == 1) {
                try {
                    Integer.parseInt(curNum);
                }
                catch (NumberFormatException e) {
                    isInt = false;
                }
                if (isInt) {
                    rtrn = Integer.parseInt(curNum);
                }
            }
        }
        return rtrn;
    }
    
    public static float deserializeExp(final String serializedExp) {
        float rtrn = 0.0f;
        int num = 0;
        for (final String curNum : deserializeExperience(serializedExp)) {
            ++num;
            boolean isFloat = true;
            if (num == 2) {
                try {
                    Float.parseFloat(curNum);
                }
                catch (NumberFormatException e) {
                    isFloat = false;
                }
                if (isFloat) {
                    rtrn = Float.parseFloat(curNum);
                }
            }
        }
        return rtrn;
    }
    
    public static double deserializeHealth(final String HealthString, final Player player) {
        if (HealthString == null) {
            return 20.0;
        }
        double rtrn = 20.0;
        rtrn = Double.valueOf(HealthString.replace(convertSymbolsForSaving(player.getName()), "").replace("#", "").replace(";", ""));
        return rtrn;
    }
    
    public static String serializeHealth(final Player player) {
        return convertSymbolsForSaving(player.getName()) + "#" + player.getHealth() + ";";
    }
    
    public static String[] deserializeHunger(final String HungerString, final Player player) {
        if (HungerString == null) {
            return null;
        }
        return HungerString.replace(player.getName() + "#f", "").replace(";", "").split("@e");
    }
    
    public static String serializeHunger(final Player player) {
        return player.getName() + "#f" + player.getFoodLevel() + "@e" + player.getExhaustion() + ";";
    }
    
    public static Collection<PotionEffect> deserializePotionEffects(final String potionEffectString, final Player player) {
        return deserializePotionEffects(potionEffectString, player.getName());
    }
    
    public static Collection<PotionEffect> deserializePotionEffects(String potionEffectString, final String playerName) {
        if (potionEffectString == null) {
            return null;
        }
        final Collection<PotionEffect> rtrn = new ArrayList<PotionEffect>();
        potionEffectString = potionEffectString.replace(convertSymbolsForSaving(playerName) + ";", "").replace(";", "");
        for (final String serializedEffect : potionEffectString.split("e@")) {
            final String effectName = serializedEffect.split(":d@")[0];
            String effectDuration = "";
            String effectAmplifier = "";
            for (final String serializedEffectSplit : serializedEffect.split(":d@")) {
                if (!serializedEffectSplit.equals(effectName)) {
                    final String[] getDurAndAmp = serializedEffectSplit.split(":a@");
                    for (int i = 0; i < getDurAndAmp.length; ++i) {
                        if (i == 0 && !getDurAndAmp[i].equals("")) {
                            effectDuration = getDurAndAmp[i];
                        }
                        if (i == 1 && !getDurAndAmp[i].equals("")) {
                            effectAmplifier = getDurAndAmp[i];
                        }
                    }
                }
            }
            if (!effectName.equals("") && !effectDuration.equals("") && !effectAmplifier.equals("")) {
                rtrn.add(new PotionEffect(PotionEffectType.getByName(effectName), (int)Integer.valueOf(effectDuration), (int)Integer.valueOf(effectAmplifier)));
            }
        }
        return rtrn;
    }
    
    public static String serializePotionEffects(final Player player) {
        final Collection<PotionEffect> effects = (Collection<PotionEffect>)player.getActivePotionEffects();
        String rtrn = convertSymbolsForSaving(player.getName()) + ";";
        for (final PotionEffect effect : effects) {
            final String effectName = effect.getType().getName();
            final int duration = effect.getDuration();
            final int amplify = effect.getAmplifier();
            rtrn = rtrn + "e@" + effectName + ":d@" + duration + ":a@" + amplify;
        }
        rtrn += ";";
        return rtrn;
    }
    
    public static void playerSetInventory(final Player player, final Inventory invToSet) {
        player.getInventory().clear();
        for (int i = 0; i < invToSet.getSize(); ++i) {
            player.getInventory().setItem(i, invToSet.getItem(i));
        }
    }
    
    public static void playerSetEnderchest(final Player player, final Inventory invToSet) {
        player.getEnderChest().clear();
        for (int i = 0; i < invToSet.getSize(); ++i) {
            player.getEnderChest().setItem(i, invToSet.getItem(i));
        }
    }
    
    public static void playerSetArmor(final Player player, final Inventory invToSet) {
        if (invToSet == null) {
            return;
        }
        final ItemStack[] armor = new ItemStack[4];
        for (int i = 0; i < 4; ++i) {
            ItemStack item = invToSet.getItem(i);
            if (item == null) {
                item = new ItemStack(Material.AIR);
            }
            armor[i] = item;
        }
        player.getInventory().setArmorContents(armor);
    }
    
    public static void setPlayerHunger(final Player player, final String[] hunger) {
        if (hunger == null) {
            player.setFoodLevel(20);
            return;
        }
        player.setFoodLevel((int)Integer.valueOf(hunger[0]));
        player.setExhaustion((float)Float.valueOf(hunger[1]));
    }
}
