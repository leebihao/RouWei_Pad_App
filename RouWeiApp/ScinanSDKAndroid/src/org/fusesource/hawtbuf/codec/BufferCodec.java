/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */
package org.fusesource.hawtbuf.codec;

import org.fusesource.hawtbuf.Buffer;

/**
 * Implementation of a Marshaller for Buffer objects.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 *
 */
public class BufferCodec extends AbstractBufferCodec<Buffer> {
    
    public static final BufferCodec INSTANCE = new BufferCodec();

    @Override
    protected Buffer createBuffer(byte[] data) {
        return new Buffer(data);
    }
}
