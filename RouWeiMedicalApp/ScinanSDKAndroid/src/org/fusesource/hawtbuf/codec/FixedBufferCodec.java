/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */
package org.fusesource.hawtbuf.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.fusesource.hawtbuf.Buffer;

/**
 * Implementation of a Marshaller for Buffer objects
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class FixedBufferCodec implements Codec<Buffer> {
    
    private final int size;

    public FixedBufferCodec(int size) {
        this.size = size;
    }

    public void encode(Buffer value, DataOutput dataOut) throws IOException {
        dataOut.write(value.data, value.offset, size);
    }

    public Buffer decode(DataInput dataIn) throws IOException {
        byte[] data = new byte[size];
        dataIn.readFully(data);
        return new Buffer(data);
    }

    public int getFixedSize() {
        return size;
    }

    public Buffer deepCopy(Buffer source) {
        return source.deepCopy();
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }
    public int estimatedSize(Buffer object) {
        return size;
    }
    
}
