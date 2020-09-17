/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */
package org.fusesource.hawtbuf.codec;

import org.fusesource.hawtbuf.AsciiBuffer;

/**
 * Implementation of a Codec for AsciiBuffer objects.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 *
 */
public class AsciiBufferCodec extends AbstractBufferCodec<AsciiBuffer> {
    public static final AsciiBufferCodec INSTANCE = new AsciiBufferCodec();

    @Override
    protected AsciiBuffer createBuffer(byte[] data) {
        return new AsciiBuffer(data);
    }
    
}
