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
 * Implementation of a Codec for Buffer objects
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
abstract public class AbstractBufferCodec<T extends Buffer> extends VariableCodec<T> {

    public void encode(T value, DataOutput dataOut) throws IOException {
        dataOut.writeInt(value.length);
        dataOut.write(value.data, value.offset, value.length);
    }

    public T decode(DataInput dataIn) throws IOException {
        int size = dataIn.readInt();
        byte[] data = new byte[size];
        dataIn.readFully(data);
        return createBuffer(data);
    }

    abstract protected T createBuffer(byte [] data);
    
    public T deepCopy(T source) {
        return createBuffer(source.deepCopy().data);
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    @Override
    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(T object) {
        return object.length+4;
    }

}
