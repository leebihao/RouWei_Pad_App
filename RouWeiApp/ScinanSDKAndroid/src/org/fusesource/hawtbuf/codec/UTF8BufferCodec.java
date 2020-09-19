/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */
package org.fusesource.hawtbuf.codec;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.UTF8Buffer;

/**
 * Implementation of a Codec for UTF8Buffer objects.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 *
 */
public class UTF8BufferCodec extends AbstractBufferCodec<UTF8Buffer> {
    public static final UTF8BufferCodec INSTANCE = new UTF8BufferCodec();

    @Override
    protected UTF8Buffer createBuffer(byte[] data) {
        return new UTF8Buffer(data);
    }
    
}
