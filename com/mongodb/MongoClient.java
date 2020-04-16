// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import org.bson.codecs.configuration.CodecRegistries;
import java.util.Arrays;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.Transformer;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.ListDatabasesIterable;
import org.bson.BsonDocument;
import com.mongodb.client.MongoIterable;
import java.util.List;
import org.bson.codecs.configuration.CodecRegistry;
import java.io.Closeable;

public class MongoClient extends Mongo implements Closeable
{
    private static final CodecRegistry DEFAULT_CODEC_REGISTRY;
    
    public static CodecRegistry getDefaultCodecRegistry() {
        return MongoClient.DEFAULT_CODEC_REGISTRY;
    }
    
    public MongoClient() {
        this(new ServerAddress());
    }
    
    public MongoClient(final String host) {
        this(new ServerAddress(host));
    }
    
    public MongoClient(final String host, final MongoClientOptions options) {
        this(new ServerAddress(host), options);
    }
    
    public MongoClient(final String host, final int port) {
        this(new ServerAddress(host, port));
    }
    
    public MongoClient(final ServerAddress addr) {
        this(addr, new MongoClientOptions.Builder().build());
    }
    
    public MongoClient(final ServerAddress addr, final List<MongoCredential> credentialsList) {
        this(addr, credentialsList, new MongoClientOptions.Builder().build());
    }
    
    public MongoClient(final ServerAddress addr, final MongoClientOptions options) {
        super(addr, options);
    }
    
    public MongoClient(final ServerAddress addr, final List<MongoCredential> credentialsList, final MongoClientOptions options) {
        super(addr, credentialsList, options);
    }
    
    public MongoClient(final List<ServerAddress> seeds) {
        this(seeds, new MongoClientOptions.Builder().build());
    }
    
    public MongoClient(final List<ServerAddress> seeds, final List<MongoCredential> credentialsList) {
        this(seeds, credentialsList, new MongoClientOptions.Builder().build());
    }
    
    public MongoClient(final List<ServerAddress> seeds, final MongoClientOptions options) {
        super(seeds, options);
    }
    
    public MongoClient(final List<ServerAddress> seeds, final List<MongoCredential> credentialsList, final MongoClientOptions options) {
        super(seeds, credentialsList, options);
    }
    
    public MongoClient(final MongoClientURI uri) {
        super(uri);
    }
    
    public MongoClientOptions getMongoClientOptions() {
        return super.getMongoClientOptions();
    }
    
    public List<MongoCredential> getCredentialsList() {
        return super.getCredentialsList();
    }
    
    public MongoIterable<String> listDatabaseNames() {
        return new ListDatabasesIterableImpl<BsonDocument>(BsonDocument.class, getDefaultCodecRegistry(), ReadPreference.primary(), this.createOperationExecutor()).map((Function<BsonDocument, String>)new Function<BsonDocument, String>() {
            @Override
            public String apply(final BsonDocument result) {
                return result.getString("name").getValue();
            }
        });
    }
    
    public ListDatabasesIterable<Document> listDatabases() {
        return this.listDatabases(Document.class);
    }
    
    public <T> ListDatabasesIterable<T> listDatabases(final Class<T> clazz) {
        return new ListDatabasesIterableImpl<T>(clazz, this.getMongoClientOptions().getCodecRegistry(), ReadPreference.primary(), this.createOperationExecutor());
    }
    
    public MongoDatabase getDatabase(final String databaseName) {
        final MongoClientOptions clientOptions = this.getMongoClientOptions();
        return new MongoDatabaseImpl(databaseName, clientOptions.getCodecRegistry(), clientOptions.getReadPreference(), clientOptions.getWriteConcern(), this.createOperationExecutor());
    }
    
    static DBObjectCodec getCommandCodec() {
        return new DBObjectCodec(getDefaultCodecRegistry());
    }
    
    static {
        DEFAULT_CODEC_REGISTRY = CodecRegistries.fromProviders(Arrays.asList(new ValueCodecProvider(), new DBRefCodecProvider(), new DocumentCodecProvider(new DocumentToDBRefTransformer()), new DBObjectCodecProvider(), new BsonValueCodecProvider(), new GeoJsonCodecProvider()));
    }
}
