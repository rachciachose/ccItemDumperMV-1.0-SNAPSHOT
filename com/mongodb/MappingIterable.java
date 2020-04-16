// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import java.util.Iterator;
import java.util.Collection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

class MappingIterable<U, V> implements MongoIterable<V>
{
    private final MongoIterable<U> iterable;
    private final Function<U, V> mapper;
    
    public MappingIterable(final MongoIterable<U> iterable, final Function<U, V> mapper) {
        this.iterable = iterable;
        this.mapper = mapper;
    }
    
    @Override
    public MongoCursor<V> iterator() {
        return new MongoMappingCursor<Object, V>(this.iterable.iterator(), this.mapper);
    }
    
    @Override
    public V first() {
        final MongoCursor<V> iterator = this.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        return iterator.next();
    }
    
    @Override
    public void forEach(final Block<? super V> block) {
        this.iterable.forEach(new Block<U>() {
            @Override
            public void apply(final U document) {
                block.apply(MappingIterable.this.mapper.apply(document));
            }
        });
    }
    
    @Override
    public <A extends Collection<? super V>> A into(final A target) {
        this.forEach(new Block<V>() {
            @Override
            public void apply(final V v) {
                ((Collection<V>)target).add(v);
            }
        });
        return target;
    }
    
    @Override
    public MappingIterable<U, V> batchSize(final int batchSize) {
        this.iterable.batchSize(batchSize);
        return this;
    }
    
    @Override
    public <W> MongoIterable<W> map(final Function<V, W> newMap) {
        return new MappingIterable<Object, W>(this, newMap);
    }
}
