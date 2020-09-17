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
 * Implementation of a Marshaller for a Integer
 * 
 */
public class IntegerCodec implements Codec<Integer> {
    
    public static final IntegerCodec INSTANCE = new IntegerCodec();
    
    public void encode(Integer object, DataOutput dataOut) throws IOException {
        dataOut.writeInt(object);
    }

    public Integer decode(DataInput dataIn) throws IOException {
        return dataIn.readInt();
    }

    public int getFixedSize() {
        return 4;
    }

    
    /** 
     * @return the source object since integers are immutable. 
     */
    public Integer deepCopy(Integer source) {
        return source;
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }
    public int estimatedSize(Integer object) {
        return 4;
    }
}
