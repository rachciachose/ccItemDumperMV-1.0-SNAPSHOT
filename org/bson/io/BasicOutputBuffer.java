// 
// Decompiled by Procyon v0.5.30
// 

package org.bson.io;

import java.util.Arrays;
import org.bson.ByteBufNIO;
import java.nio.ByteBuffer;
import org.bson.ByteBuf;
import java.util.List;
import java.io.IOException;
import java.io.OutputStream;

public class BasicOutputBuffer extends OutputBuffer
{
    private byte[] buffer;
    private int position;
    
    public BasicOutputBuffer() {
        this(1024);
    }
    
    public BasicOutputBuffer(final int initialSize) {
        this.buffer = new byte[1024];
        this.buffer = new byte[initialSize];
    }
    
    @Override
    public void write(final byte[] b) {
        this.ensureOpen();
        this.write(b, 0, b.length);
    }
    
    @Override
    public void writeBytes(final byte[] bytes, final int offset, final int length) {
        this.ensureOpen();
        this.ensure(length);
        System.arraycopy(bytes, offset, this.buffer, this.position, length);
        this.position += length;
    }
    
    @Override
    public void writeByte(final int value) {
        this.ensureOpen();
        this.ensure(1);
        this.buffer[this.position++] = (byte)(0xFF & value);
    }
    
    @Override
    protected void write(final int absolutePosition, final int value) {
        this.ensureOpen();
        if (absolutePosition < 0) {
            throw new IllegalArgumentException(String.format("position must be >= 0 but was %d", absolutePosition));
        }
        if (absolutePosition > this.position - 1) {
            throw new IllegalArgumentException(String.format("position must be <= %d but was %d", this.position - 1, absolutePosition));
        }
        this.buffer[absolutePosition] = (byte)(0xFF & value);
    }
    
    @Override
    public int getPosition() {
        this.ensureOpen();
        return this.position;
    }
    
    @Override
    public int getSize() {
        this.ensureOpen();
        return this.position;
    }
    
    @Override
    public int pipe(final OutputStream out) throws IOException {
        this.ensureOpen();
        out.write(this.buffer, 0, this.position);
        return this.position;
    }
    
    @Override
    public void truncateToPosition(final int newPosition) {
        this.ensureOpen();
        if (newPosition > this.position || newPosition < 0) {
            throw new IllegalArgumentException();
        }
        this.position = newPosition;
    }
    
    @Override
    public List<ByteBuf> getByteBuffers() {
        this.ensureOpen();
        return Arrays.asList(new ByteBufNIO(ByteBuffer.wrap(this.buffer, 0, this.position).duplicate()));
    }
    
    @Override
    public void close() {
        this.buffer = null;
    }
    
    private void ensureOpen() {
        if (this.buffer == null) {
            throw new IllegalStateException("The output is closed");
        }
    }
    
    private void ensure(final int more) {
        final int need = this.position + more;
        if (need <= this.buffer.length) {
            return;
        }
        int newSize = this.buffer.length * 2;
        if (newSize < need) {
            newSize = need + 128;
        }
        final byte[] n = new byte[newSize];
        System.arraycopy(this.buffer, 0, n, 0, this.position);
        this.buffer = n;
    }
}
