// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.io;

import org.bson.types.ObjectId;
import org.bson.BsonSerializationException;
import java.nio.ByteOrder;
import org.bson.ByteBuf;
import java.nio.charset.Charset;

public class ByteBufferBsonInput implements BsonInput
{
    private static final Charset UTF8_CHARSET;
    private ByteBuf buffer;
    private int mark;
    
    public ByteBufferBsonInput(final ByteBuf buffer) {
        this.mark = -1;
        if (buffer == null) {
            throw new IllegalArgumentException("buffer can not be null");
        }
        (this.buffer = buffer).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    @Override
    public int getPosition() {
        this.ensureOpen();
        return this.buffer.position();
    }
    
    @Override
    public byte readByte() {
        this.ensureOpen();
        this.ensureAvailable(1);
        return this.buffer.get();
    }
    
    @Override
    public void readBytes(final byte[] bytes) {
        this.ensureOpen();
        this.ensureAvailable(bytes.length);
        this.buffer.get(bytes);
    }
    
    @Override
    public void readBytes(final byte[] bytes, final int offset, final int length) {
        this.ensureOpen();
        this.ensureAvailable(length);
        this.buffer.get(bytes, offset, length);
    }
    
    @Override
    public long readInt64() {
        this.ensureOpen();
        this.ensureAvailable(8);
        return this.buffer.getLong();
    }
    
    @Override
    public double readDouble() {
        this.ensureOpen();
        this.ensureAvailable(8);
        return this.buffer.getDouble();
    }
    
    @Override
    public int readInt32() {
        this.ensureOpen();
        this.ensureAvailable(4);
        return this.buffer.getInt();
    }
    
    @Override
    public String readString() {
        this.ensureOpen();
        final int size = this.readInt32();
        if (size <= 0) {
            throw new BsonSerializationException(String.format("While decoding a BSON string found a size that is not a positive number: %d", size));
        }
        final byte[] bytes = new byte[size];
        this.readBytes(bytes);
        if (bytes[size - 1] != 0) {
            throw new BsonSerializationException("Found a BSON string that is not null-terminated");
        }
        return new String(bytes, 0, size - 1, ByteBufferBsonInput.UTF8_CHARSET);
    }
    
    @Override
    public ObjectId readObjectId() {
        this.ensureOpen();
        final byte[] bytes = new byte[12];
        this.readBytes(bytes);
        return new ObjectId(bytes);
    }
    
    @Override
    public String readCString() {
        this.ensureOpen();
        final int mark = this.buffer.position();
        this.readUntilNullByte();
        final int size = this.buffer.position() - mark - 1;
        this.buffer.position(mark);
        final byte[] bytes = new byte[size];
        this.readBytes(bytes);
        this.readByte();
        return new String(bytes, ByteBufferBsonInput.UTF8_CHARSET);
    }
    
    private void readUntilNullByte() {
        while (this.readByte() != 0) {}
    }
    
    @Override
    public void skipCString() {
        this.ensureOpen();
        this.readUntilNullByte();
    }
    
    @Override
    public void skip(final int numBytes) {
        this.ensureOpen();
        this.buffer.position(this.buffer.position() + numBytes);
    }
    
    @Override
    public void mark(final int readLimit) {
        this.ensureOpen();
        this.mark = this.buffer.position();
    }
    
    @Override
    public void reset() {
        this.ensureOpen();
        if (this.mark == -1) {
            throw new IllegalStateException("Mark not set");
        }
        this.buffer.position(this.mark);
    }
    
    @Override
    public boolean hasRemaining() {
        this.ensureOpen();
        return this.buffer.hasRemaining();
    }
    
    @Override
    public void close() {
        this.buffer.release();
        this.buffer = null;
    }
    
    private void ensureOpen() {
        if (this.buffer == null) {
            throw new IllegalStateException("Stream is closed");
        }
    }
    
    private void ensureAvailable(final int bytesNeeded) {
        if (this.buffer.remaining() < bytesNeeded) {
            throw new BsonSerializationException(String.format("While decoding a BSON document %d bytes were required, but only %d remain", bytesNeeded, this.buffer.remaining()));
        }
    }
    
    static {
        UTF8_CHARSET = Charset.forName("UTF-8");
    }
}
