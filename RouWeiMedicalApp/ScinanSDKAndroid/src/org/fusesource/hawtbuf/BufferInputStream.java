/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */
package org.fusesource.hawtbuf;

import java.io.IOException;
import java.io.InputStream;


/**
 * Very similar to the java.io.ByteArrayInputStream but this version is not
 * thread safe.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
final public class BufferInputStream extends InputStream {

    byte buffer[];
    int limit;
    int pos;
    int mark;

    public BufferInputStream(byte data[]) {
        this(data, 0, data.length);
    }

    public BufferInputStream(Buffer sequence) {
        this(sequence.getData(), sequence.getOffset(), sequence.getLength());
    }

    public BufferInputStream(byte data[], int offset, int size) {
        this.buffer = data;
        this.mark = offset;
        this.pos = offset;
        this.limit = offset + size;
    }

    public int read() throws IOException {
        if (pos < limit) {
            return buffer[pos++] & 0xff;
        } else {
            return -1;
        }
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) {
        if (pos < limit) {
            len = Math.min(len, limit - pos);
            System.arraycopy(buffer, pos, b, off, len);
            pos += len;
            return len;
        } else {
            return -1;
        }
    }
    
    public Buffer readBuffer(int len) {
        Buffer rc=null;
        if (pos < limit) {
            len = Math.min(len, limit - pos);
            rc = new Buffer(buffer, pos, len);
            pos += len;
        }
        return rc;
    }

    public long skip(long len) throws IOException {
        if (pos < limit) {
            len = Math.min(len, limit - pos);
            if (len > 0) {
                pos += len;
            }
            return len;
        } else {
            return -1;
        }
    }

    public int available() {
        return limit - pos;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int markpos) {
        mark = pos;
    }

    public void reset() {
        pos = mark;
    }

}
