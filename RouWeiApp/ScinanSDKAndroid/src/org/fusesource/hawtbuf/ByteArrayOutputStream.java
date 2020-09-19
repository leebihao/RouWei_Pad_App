/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */
package org.fusesource.hawtbuf;

import java.io.OutputStream;



/**
 * Very similar to the java.io.ByteArrayOutputStream but this version 
 * is not thread safe and the resulting data is returned in a Buffer
 * to avoid an extra byte[] allocation.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class ByteArrayOutputStream extends OutputStream {

    byte buffer[];
    int size;

    public ByteArrayOutputStream() {
        this(1028);
    }
    public ByteArrayOutputStream(int capacity) {
        buffer = new byte[capacity];
    }

    public void write(int b) {
        int newsize = size + 1;
        checkCapacity(newsize);
        buffer[size] = (byte) b;
        size = newsize;
    }

    public void write(byte b[], int off, int len) {
        int newsize = size + len;
        checkCapacity(newsize);
        System.arraycopy(b, off, buffer, size, len);
        size = newsize;
    }

    public void write(Buffer b) {
        write(b.data, b.offset, b.length);
    }

    /**
     * Ensures the the buffer has at least the minimumCapacity specified. 
     * @param minimumCapacity
     */
    private void checkCapacity(int minimumCapacity) {
        if (minimumCapacity > buffer.length) {
            byte b[] = new byte[Math.max(buffer.length << 1, minimumCapacity)];
            System.arraycopy(buffer, 0, b, 0, size);
            buffer = b;
        }
    }

    public void reset() {
        size = 0;
    }

    public Buffer toBuffer() {
        return new Buffer(buffer, 0, size);
    }
    
    public byte[] toByteArray() {
        return toBuffer().toByteArray();
    }
    
    public int size() {
        return size;
    }
}
