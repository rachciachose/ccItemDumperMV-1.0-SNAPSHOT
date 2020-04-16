// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.gridfs;

import java.util.Arrays;
import com.mongodb.MongoClient;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import com.mongodb.MongoGridFSException;
import java.util.Iterator;
import java.util.Set;
import java.util.Date;
import org.bson.BsonValue;
import java.util.HashSet;
import java.util.Collection;
import com.mongodb.Block;
import com.mongodb.client.MongoIterable;
import com.mongodb.Function;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.MongoCursor;
import java.util.concurrent.TimeUnit;
import org.bson.conversions.Bson;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import org.bson.codecs.configuration.CodecRegistry;

class GridFSFindIterableImpl implements GridFSFindIterable
{
    private static final CodecRegistry DEFAULT_CODEC_REGISTRY;
    private final FindIterable<Document> underlying;
    private static final List<String> VALID_FIELDS;
    
    public GridFSFindIterableImpl(final FindIterable<Document> underlying) {
        this.underlying = underlying;
    }
    
    @Override
    public GridFSFindIterable sort(final Bson sort) {
        this.underlying.sort(sort);
        return this;
    }
    
    @Override
    public GridFSFindIterable skip(final int skip) {
        this.underlying.skip(skip);
        return this;
    }
    
    @Override
    public GridFSFindIterable limit(final int limit) {
        this.underlying.limit(limit);
        return this;
    }
    
    @Override
    public GridFSFindIterable filter(final Bson filter) {
        this.underlying.filter(filter);
        return this;
    }
    
    @Override
    public GridFSFindIterable maxTime(final long maxTime, final TimeUnit timeUnit) {
        this.underlying.maxTime(maxTime, timeUnit);
        return this;
    }
    
    @Override
    public GridFSFindIterable batchSize(final int batchSize) {
        this.underlying.batchSize(batchSize);
        return this;
    }
    
    @Override
    public GridFSFindIterable noCursorTimeout(final boolean noCursorTimeout) {
        this.underlying.noCursorTimeout(noCursorTimeout);
        return this;
    }
    
    @Override
    public MongoCursor<GridFSFile> iterator() {
        return this.toGridFSFileIterable().iterator();
    }
    
    @Override
    public GridFSFile first() {
        return this.toGridFSFileIterable().first();
    }
    
    @Override
    public <U> MongoIterable<U> map(final Function<GridFSFile, U> mapper) {
        return this.toGridFSFileIterable().map(mapper);
    }
    
    @Override
    public void forEach(final Block<? super GridFSFile> block) {
        this.toGridFSFileIterable().forEach(block);
    }
    
    @Override
    public <A extends Collection<? super GridFSFile>> A into(final A target) {
        return this.toGridFSFileIterable().into(target);
    }
    
    private MongoIterable<GridFSFile> toGridFSFileIterable() {
        return this.underlying.map((Function<Document, GridFSFile>)new Function<Document, GridFSFile>() {
            @Override
            public GridFSFile apply(final Document document) {
                final BsonValue id = GridFSFindIterableImpl.this.getId(document);
                final String filename = document.getString("filename");
                final long length = GridFSFindIterableImpl.this.getAndValidateNumber("length", document).longValue();
                final int chunkSize = GridFSFindIterableImpl.this.getAndValidateNumber("chunkSize", document).intValue();
                final Date uploadDate = document.getDate("uploadDate");
                final String md5 = document.getString("md5");
                final Document metadata = document.get("metadata", Document.class);
                final Set<String> extraElementKeys = new HashSet<String>(document.keySet());
                extraElementKeys.removeAll(GridFSFindIterableImpl.VALID_FIELDS);
                if (extraElementKeys.size() > 0) {
                    final Document extraElements = new Document();
                    for (final String key : extraElementKeys) {
                        extraElements.append(key, document.get(key));
                    }
                    return new GridFSFile(id, filename, length, chunkSize, uploadDate, md5, metadata, extraElements);
                }
                return new GridFSFile(id, filename, length, chunkSize, uploadDate, md5, metadata);
            }
        });
    }
    
    private Number getAndValidateNumber(final String fieldName, final Document document) {
        final Number value = document.get(fieldName, Number.class);
        if (value.floatValue() % 1.0f != 0.0f) {
            throw new MongoGridFSException(String.format("Invalid number format for %s", fieldName));
        }
        return value;
    }
    
    private BsonValue getId(final Document document) {
        final Object rawId = document.get("_id");
        if (rawId instanceof ObjectId) {
            return new BsonObjectId((ObjectId)rawId);
        }
        return new Document("_id", document.get("_id")).toBsonDocument(BsonDocument.class, GridFSFindIterableImpl.DEFAULT_CODEC_REGISTRY).get("_id");
    }
    
    static {
        DEFAULT_CODEC_REGISTRY = MongoClient.getDefaultCodecRegistry();
        VALID_FIELDS = Arrays.asList("_id", "filename", "length", "chunkSize", "uploadDate", "md5", "metadata");
    }
}
