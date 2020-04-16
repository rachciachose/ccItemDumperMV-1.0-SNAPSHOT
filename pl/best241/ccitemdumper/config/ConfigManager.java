// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper.config;

import java.io.File;
import pl.best241.ccitemdumper.CcItemDumper;

public final class ConfigManager
{
    private final CcItemDumper plugin;
    public static String host;
    
    public ConfigManager(final CcItemDumper plugin) {
        this.plugin = plugin;
        final File fileConfig = new File(plugin.getDataFolder(), "config.yml");
        if (!fileConfig.exists()) {
            System.out.println("Nie znaleziono config.yml! Generowanie!");
            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
        }
        plugin.getConfig();
        this.load();
    }
    
    public void load() {
        ConfigManager.host = this.plugin.getConfig().getString("host");
    }
}
