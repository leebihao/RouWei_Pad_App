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
 */
public interface Codec<T> {
    
    /**
     * Write the payload of the object to the DataOutput stream.
     * 
     * @param object 
     * @param dataOut
     * @throws IOException
     */
    void encode(T object, DataOutput dataOut) throws IOException;
    
    
    /**
     * Read the payload of the object from the DataInput stream.
     * 
     * @param dataIn 
     * @return unmarshalled object
     * @throws IOException
     */
    T decode(DataInput dataIn) throws IOException;

    /** 
     * @return -1 if the object do not always marshall to a fixed size, otherwise return that fixed size.
     */
    int getFixedSize();
    
    /**
     *
     * @return true if the {@link #estimatedSize(Object)} operation is supported.
     */
    boolean isEstimatedSizeSupported();

    /**
     * @param object
     * @return the estimated marshaled size of the object.
     */
    int estimatedSize(T object);
    
    /**
     * 
     * @return true if the {@link #deepCopy(Object)} operations is supported.
     */
    boolean isDeepCopySupported();

    /**
     * @return a deep copy of the source object.  If the source is immutable
     * the same source should be returned.
     */
    T deepCopy(T source);
   
}
