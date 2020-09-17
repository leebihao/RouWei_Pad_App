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
 * Implementation of a variable length Codec for a signed Long
 *
 */
public class VarSignedLongCodec extends VarLongCodec {

    public static final VarSignedLongCodec INSTANCE = new VarSignedLongCodec();


    public void encode(Long value, DataOutput dataOut) throws IOException {
        super.encode(encodeZigZag(value), dataOut);
    }

    public Long decode(DataInput dataIn) throws IOException {
        return decodeZigZag(super.decode(dataIn));
    }

    private static long decodeZigZag(long n) {
        return (n >>> 1) ^ -(n & 1);
    }

    private static long encodeZigZag(long n) {
        return (n << 1) ^ (n >> 63);
    }

    public int estimatedSize(Long value) {
        return super.estimatedSize(encodeZigZag(value));
    }
}