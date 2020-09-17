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

/**
 * Implementation of a Codec for a Long
 * 
 */
public class LongCodec implements Codec<Long> {
    
    public static final LongCodec INSTANCE = new LongCodec();
    
    public void encode(Long object, DataOutput dataOut) throws IOException {
        dataOut.writeLong(object);
    }

    public Long decode(DataInput dataIn) throws IOException {
        return dataIn.readLong();
    }

    public int getFixedSize() {
        return 8;
    }

    public Long deepCopy(Long source) {
        return source;
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(Long object) {
        return 8;
    }
}
