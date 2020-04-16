// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper.tickers;

import java.util.Iterator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.MongoClient;
import pl.best241.ccitemdumper.config.ConfigManager;
import pl.best241.ccitemdumper.data.DataStore;
import org.bukkit.plugin.Plugin;
import pl.best241.ccitemdumper.Config;
import pl.best241.ccitemdumper.CcItemDumper;
import org.bukkit.Bukkit;

public class DataInsertTicker
{
    public static void run() {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)CcItemDumper.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                DataInsertTicker.insert();
            }
        }, Config.insertTimeUnitInTicks, Config.insertTimeUnitInTicks);
    }
    
    public static void insert() {
        if (DataStore.dataToInsert.isEmpty()) {
            return;
        }
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(ConfigManager.host, 27017);
            final MongoDatabase database = mongoClient.getDatabase("craftcore");
            final MongoCollection<Document> collection = database.getCollection("ccItemDeposit:playerInventories");
            collection.listIndexes();
            for (final Document documentToInsert : DataStore.dataToInsert.keySet()) {
                final Long fetchTime = DataStore.dataToInsert.remove(documentToInsert);
                collection.insertOne(documentToInsert);
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }
}
