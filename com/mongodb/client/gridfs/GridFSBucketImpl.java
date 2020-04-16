// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.gridfs;

import com.mongodb.client.gridfs.model.GridFSFile;
import java.util.Iterator;
import java.util.ArrayList;
import com.mongodb.client.model.IndexOptions;
import org.bson.codecs.configuration.CodecRegistries;
import com.mongodb.MongoClient;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import com.mongodb.client.gridfs.model.GridFSDownloadByNameOptions;
import java.io.OutputStream;
import org.bson.BsonValue;
import org.bson.BsonObjectId;
import java.io.IOException;
import com.mongodb.MongoGridFSException;
import java.io.InputStream;
import org.bson.types.ObjectId;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;

final class GridFSBucketImpl implements GridFSBucket
{
    private final MongoDatabase database;
    private final String bucketName;
    private final int chunkSizeBytes;
    private final WriteConcern writeConcern;
    private final ReadPreference readPreference;
    private final MongoCollection<Document> filesCollection;
    private final MongoCollection<Document> chunksCollection;
    private final CodecRegistry codecRegistry;
    private volatile boolean checkedIndexes;
    
    GridFSBucketImpl(final MongoDatabase database) {
        this(database, "fs");
    }
    
    GridFSBucketImpl(final MongoDatabase database, final String bucketName) {
        this.database = Assertions.notNull("database", database);
        this.bucketName = Assertions.notNull("bucketName", bucketName);
        this.chunkSizeBytes = 261120;
        this.writeConcern = database.getWriteConcern();
        this.readPreference = database.getReadPreference();
        this.codecRegistry = this.getCodecRegistry();
        this.filesCollection = this.getFilesCollection();
        this.chunksCollection = this.getChunksCollection();
    }
    
    GridFSBucketImpl(final MongoDatabase database, final String bucketName, final int chunkSizeBytes, final CodecRegistry codecRegistry, final ReadPreference readPreference, final WriteConcern writeConcern, final MongoCollection<Document> filesCollection, final MongoCollection<Document> chunksCollection, final boolean checkedIndexes) {
        this.database = Assertions.notNull("database", database);
        this.bucketName = Assertions.notNull("bucketName", bucketName);
        this.chunkSizeBytes = chunkSizeBytes;
        this.codecRegistry = Assertions.notNull("codecRegistry", codecRegistry);
        this.readPreference = Assertions.notNull("readPreference", readPreference);
        this.writeConcern = Assertions.notNull("writeConcern", writeConcern);
        this.checkedIndexes = checkedIndexes;
        this.filesCollection = Assertions.notNull("filesCollection", filesCollection);
        this.chunksCollection = Assertions.notNull("chunksCollection", chunksCollection);
    }
    
    @Override
    public String getBucketName() {
        return this.bucketName;
    }
    
    @Override
    public int getChunkSizeBytes() {
        return this.chunkSizeBytes;
    }
    
    @Override
    public ReadPreference getReadPreference() {
        return this.readPreference;
    }
    
    @Override
    public WriteConcern getWriteConcern() {
        return this.writeConcern;
    }
    
    @Override
    public GridFSBucket withChunkSizeBytes(final int chunkSizeBytes) {
        return new GridFSBucketImpl(this.database, this.bucketName, chunkSizeBytes, this.codecRegistry, this.readPreference, this.writeConcern, this.filesCollection, this.chunksCollection, this.checkedIndexes);
    }
    
    @Override
    public GridFSBucket withReadPreference(final ReadPreference readPreference) {
        return new GridFSBucketImpl(this.database, this.bucketName, this.chunkSizeBytes, this.codecRegistry, readPreference, this.writeConcern, this.filesCollection, this.chunksCollection, this.checkedIndexes);
    }
    
    @Override
    public GridFSBucket withWriteConcern(final WriteConcern writeConcern) {
        return new GridFSBucketImpl(this.database, this.bucketName, this.chunkSizeBytes, this.codecRegistry, this.readPreference, writeConcern, this.filesCollection, this.chunksCollection, this.checkedIndexes);
    }
    
    @Override
    public GridFSUploadStream openUploadStream(final String filename) {
        return this.openUploadStream(filename, new GridFSUploadOptions());
    }
    
    @Override
    public GridFSUploadStream openUploadStream(final String filename, final GridFSUploadOptions options) {
        final int chunkSize = (options.getChunkSizeBytes() == null) ? this.chunkSizeBytes : options.getChunkSizeBytes();
        this.checkCreateIndex();
        return new GridFSUploadStreamImpl(this.filesCollection, this.chunksCollection, new ObjectId(), filename, chunkSize, options.getMetadata());
    }
    
    @Override
    public ObjectId uploadFromStream(final String filename, final InputStream source) {
        return this.uploadFromStream(filename, source, new GridFSUploadOptions());
    }
    
    @Override
    public ObjectId uploadFromStream(final String filename, final InputStream source, final GridFSUploadOptions options) {
        final GridFSUploadStream uploadStream = this.openUploadStream(filename, options);
        final int chunkSize = (options.getChunkSizeBytes() == null) ? this.chunkSizeBytes : options.getChunkSizeBytes();
        final byte[] buffer = new byte[chunkSize];
        try {
            int len;
            while ((len = source.read(buffer)) != -1) {
                uploadStream.write(buffer, 0, len);
            }
            uploadStream.close();
        }
        catch (IOException e) {
            uploadStream.abort();
            throw new MongoGridFSException("IOException when reading from the InputStream", e);
        }
        return uploadStream.getFileId();
    }
    
    @Override
    public GridFSDownloadStream openDownloadStream(final ObjectId id) {
        return this.findTheFileInfoAndOpenDownloadStream(new BsonObjectId(id));
    }
    
    @Override
    public void downloadToStream(final ObjectId id, final OutputStream destination) {
        this.downloadToStream(this.findTheFileInfoAndOpenDownloadStream(new BsonObjectId(id)), destination);
    }
    
    @Override
    public void downloadToStream(final BsonValue id, final OutputStream destination) {
        this.downloadToStream(this.findTheFileInfoAndOpenDownloadStream(id), destination);
    }
    
    @Override
    public GridFSDownloadStream openDownloadStream(final BsonValue id) {
        return this.findTheFileInfoAndOpenDownloadStream(id);
    }
    
    @Override
    public GridFSDownloadStream openDownloadStreamByName(final String filename) {
        return this.openDownloadStreamByName(filename, new GridFSDownloadByNameOptions());
    }
    
    @Override
    public GridFSDownloadStream openDownloadStreamByName(final String filename, final GridFSDownloadByNameOptions options) {
        return new GridFSDownloadStreamImpl(this.getFileByName(filename, options), this.chunksCollection);
    }
    
    @Override
    public void downloadToStreamByName(final String filename, final OutputStream destination) {
        this.downloadToStreamByName(filename, destination, new GridFSDownloadByNameOptions());
    }
    
    @Override
    public void downloadToStreamByName(final String filename, final OutputStream destination, final GridFSDownloadByNameOptions options) {
        this.downloadToStream(this.openDownloadStreamByName(filename, options), destination);
    }
    
    @Override
    public GridFSFindIterable find() {
        return new GridFSFindIterableImpl(this.filesCollection.find());
    }
    
    @Override
    public GridFSFindIterable find(final Bson filter) {
        return this.find().filter(filter);
    }
    
    @Override
    public void delete(final ObjectId id) {
        final DeleteResult result = this.filesCollection.deleteOne(new BsonDocument("_id", new BsonObjectId(id)));
        this.chunksCollection.deleteMany(new BsonDocument("files_id", new BsonObjectId(id)));
        if (result.wasAcknowledged() && result.getDeletedCount() == 0L) {
            throw new MongoGridFSException(String.format("No file found with the ObjectId: %s", id));
        }
    }
    
    @Override
    public void rename(final ObjectId id, final String newFilename) {
        final UpdateResult updateResult = this.filesCollection.updateOne(new Document("_id", id), new Document("$set", new Document("filename", newFilename)));
        if (updateResult.wasAcknowledged() && updateResult.getMatchedCount() == 0L) {
            throw new MongoGridFSException(String.format("No file found with the ObjectId: %s", id));
        }
    }
    
    @Override
    public void drop() {
        this.filesCollection.drop();
        this.chunksCollection.drop();
    }
    
    private CodecRegistry getCodecRegistry() {
        return CodecRegistries.fromRegistries(this.database.getCodecRegistry(), MongoClient.getDefaultCodecRegistry());
    }
    
    private MongoCollection<Document> getFilesCollection() {
        return this.database.getCollection(this.bucketName + ".files").withCodecRegistry(this.codecRegistry).withReadPreference(this.readPreference).withWriteConcern(this.writeConcern);
    }
    
    private MongoCollection<Document> getChunksCollection() {
        return this.database.getCollection(this.bucketName + ".chunks").withCodecRegistry(MongoClient.getDefaultCodecRegistry()).withReadPreference(this.readPreference).withWriteConcern(this.writeConcern);
    }
    
    private void checkCreateIndex() {
        if (!this.checkedIndexes) {
            if (this.filesCollection.withReadPreference(ReadPreference.primary()).find().projection(new Document("_id", 1)).first() == null) {
                final Document filesIndex = new Document("filename", 1).append("uploadDate", 1);
                if (!this.hasIndex(this.filesCollection.withReadPreference(ReadPreference.primary()), filesIndex)) {
                    this.filesCollection.createIndex(filesIndex);
                }
                final Document chunksIndex = new Document("files_id", 1).append("n", 1);
                if (!this.hasIndex(this.chunksCollection.withReadPreference(ReadPreference.primary()), chunksIndex)) {
                    this.chunksCollection.createIndex(chunksIndex, new IndexOptions().unique(true));
                }
            }
            this.checkedIndexes = true;
        }
    }
    
    private boolean hasIndex(final MongoCollection<Document> collection, final Document index) {
        boolean hasIndex = false;
        final ArrayList<Document> indexes = collection.listIndexes().into(new ArrayList<Document>());
        for (final Document indexDoc : indexes) {
            if (indexDoc.get("key", Document.class).equals(index)) {
                hasIndex = true;
                break;
            }
        }
        return hasIndex;
    }
    
    private GridFSFile getFileByName(final String filename, final GridFSDownloadByNameOptions options) {
        final int revision = options.getRevision();
        int skip;
        int sort;
        if (revision >= 0) {
            skip = revision;
            sort = 1;
        }
        else {
            skip = -revision - 1;
            sort = -1;
        }
        final GridFSFile fileInfo = this.find(new Document("filename", filename)).skip(skip).sort(new Document("uploadDate", sort)).first();
        if (fileInfo == null) {
            throw new MongoGridFSException(String.format("No file found with the filename: %s and revision: %s", filename, revision));
        }
        return fileInfo;
    }
    
    private GridFSDownloadStream findTheFileInfoAndOpenDownloadStream(final BsonValue id) {
        final GridFSFile fileInfo = this.find(new Document("_id", id)).first();
        if (fileInfo == null) {
            throw new MongoGridFSException(String.format("No file found with the id: %s", id));
        }
        return new GridFSDownloadStreamImpl(fileInfo, this.chunksCollection);
    }
    
    private void downloadToStream(final GridFSDownloadStream downloadStream, final OutputStream destination) {
        final byte[] buffer = new byte[downloadStream.getGridFSFile().getChunkSize()];
        MongoGridFSException savedThrowable = null;
        try {
            int len;
            while ((len = downloadStream.read(buffer)) != -1) {
                destination.write(buffer, 0, len);
            }
            try {
                downloadStream.close();
            }
            catch (Exception ex) {}
            if (savedThrowable != null) {
                throw savedThrowable;
            }
        }
        catch (IOException e) {
            savedThrowable = new MongoGridFSException("IOException when reading from the OutputStream", e);
        }
        catch (Exception e2) {
            savedThrowable = new MongoGridFSException("Unexpected Exception when reading GridFS and writing to the Stream", e2);
        }
        finally {
            try {
                downloadStream.close();
            }
            catch (Exception ex2) {}
            if (savedThrowable != null) {
                throw savedThrowable;
            }
        }
    }
}
