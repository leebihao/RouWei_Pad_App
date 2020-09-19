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
 * Implementation of a Marshaller for Strings
 * 
 */
public class StringCodec implements Codec<String> {
    
    public static final StringCodec INSTANCE = new StringCodec();
    
    /**
     * Write the payload of this entry to the RawContainer
     * 
     * @param object
     * @param dataOut
     * @throws IOException
     */
    public void encode(String object, DataOutput dataOut) throws IOException {
        dataOut.writeUTF(object);
    }

    /**
     * Read the entry from the RawContainer
     * 
     * @param dataIn
     * @return unmarshalled object
     * @throws IOException
     */
    public String decode(DataInput dataIn) throws IOException {
        return dataIn.readUTF();
    }


    public int getFixedSize() {
        return -1;
    }

    public String deepCopy(String source) {
        return source;
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(String object) {
        return object.length()+2;
    }
}
