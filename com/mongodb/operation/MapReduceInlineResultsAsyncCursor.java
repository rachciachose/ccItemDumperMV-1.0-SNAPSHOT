// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.codecs.Decoder;
import com.mongodb.connection.QueryResult;

class MapReduceInlineResultsAsyncCursor<T> extends AsyncQueryBatchCursor<T> implements MapReduceAsyncBatchCursor<T>
{
    private final MapReduceStatistics statistics;
    
    MapReduceInlineResultsAsyncCursor(final QueryResult<T> queryResult, final Decoder<T> decoder, final MapReduceStatistics statistics) {
        super(queryResult, 0, 0, decoder);
        this.statistics = statistics;
    }
    
    @Override
    public MapReduceStatistics getStatistics() {
        return this.statistics;
    }
}
