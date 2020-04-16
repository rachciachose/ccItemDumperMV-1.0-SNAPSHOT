// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccitemdumper.managers;

import com.mongodb.client.FindIterable;
import java.util.ArrayList;
import java.util.UUID;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.bson.Document;
import com.mongodb.MongoClient;
import pl.best241.ccitemdumper.config.ConfigManager;

public class MongoDBManager
{
    public static final int port = 27017;
    public static final String databaseName = "craftcore";
    public static final String collectionName = "ccItemDeposit:playerInventories";
    
    public static void createIndexes() {
        try (final MongoClient mongoClient = new MongoClient(ConfigManager.host, 27017)) {
            final MongoDatabase database = mongoClient.getDatabase("craftcore");
            final MongoCollection<Document> collection = database.getCollection("ccItemDeposit:playerInventories");
            final ListIndexesIterable<Document> listIndexes = collection.listIndexes();
            for (final Document index : listIndexes) {
                System.out.println("Index: " + index);
            }
            collection.createIndex(new Document("uuid", 1));
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public static long getPlayerInventoriesSize() {
        long count = -1L;
        try (final MongoClient mongoClient = new MongoClient(ConfigManager.host, 27017)) {
            final MongoDatabase database = mongoClient.getDatabase("craftcore");
            final MongoCollection<Document> collection = database.getCollection("ccItemDeposit:playerInventories");
            count = collection.count();
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return count;
    }
    
    public static ArrayList<Long> getTimesOfBackups(final UUID uuid) {
        final ArrayList<Long> times = new ArrayList<Long>();
        try (final MongoClient mongoClient = new MongoClient(ConfigManager.host, 27017)) {
            final MongoDatabase database = mongoClient.getDatabase("craftcore");
            final MongoCollection<Document> collection = database.getCollection("ccItemDeposit:playerInventories");
            final FindIterable<Document> find = collection.find(new Document("uuid", uuid.toString()));
            for (final Document backup : find) {
                times.add(backup.getLong("time"));
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return times;
    }
    
    public static ArrayList<String> getParsedInventories(final UUID uuid) {
        final ArrayList<String> invetories = new ArrayList<String>();
        try (final MongoClient mongoClient = new MongoClient(ConfigManager.host, 27017)) {
            final MongoDatabase database = mongoClient.getDatabase("craftcore");
            final MongoCollection<Document> collection = database.getCollection("ccItemDeposit:playerInventories");
            final FindIterable<Document> find = collection.find(new Document("uuid", uuid.toString()));
            for (final Document backup : find) {
                invetories.add(backup.getString("inventory"));
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return invetories;
    }
    
    public static ArrayList<String> getParsedEnders(final UUID uuid) {
        final ArrayList<String> invetories = new ArrayList<String>();
        try (final MongoClient mongoClient = new MongoClient(ConfigManager.host, 27017)) {
            final MongoDatabase database = mongoClient.getDatabase("craftcore");
            final MongoCollection<Document> collection = database.getCollection("ccItemDeposit:playerInventories");
            final FindIterable<Document> find = collection.find(new Document("uuid", uuid.toString()));
            for (final Document backup : find) {
                invetories.add(backup.getString("enderchest"));
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return invetories;
    }
    
    public static ArrayList<Document> getDocuments(final UUID uuid) {
        final ArrayList<Document> invetories = new ArrayList<Document>();
        try (final MongoClient mongoClient = new MongoClient(ConfigManager.host, 27017)) {
            final MongoDatabase database = mongoClient.getDatabase("craftcore");
            final MongoCollection<Document> collection = database.getCollection("ccItemDeposit:playerInventories");
            final FindIterable<Document> find = collection.find(new Document("uuid", uuid.toString()));
            for (final Document backup : find) {
                invetories.add(backup);
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return invetories;
    }
}
