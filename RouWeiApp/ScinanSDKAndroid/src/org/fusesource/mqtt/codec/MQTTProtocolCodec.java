/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.codec;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtdispatch.transport.AbstractProtocolCodec;
import org.fusesource.hawtdispatch.util.BufferPools;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class MQTTProtocolCodec extends AbstractProtocolCodec {

    private static final BufferPools BUFFER_POOLS = new BufferPools();

    private int maxMessageLength = 1024*1024*100;

    public MQTTProtocolCodec() {
        this.bufferPools = BUFFER_POOLS;
    }

    public int getMaxMessageLength() {
        return maxMessageLength;
    }

    public void setMaxMessageLength(int maxMessageLength) {
        this.maxMessageLength = maxMessageLength;
    }

    @Override
    protected void encode(Object value) throws IOException {
        MQTTFrame frame = (MQTTFrame) value;
        nextWriteBuffer.write(frame.header());

        int remaining = 0;
        for(Buffer buffer : frame.buffers) {
            remaining += buffer.length;
        }
        do {
            byte digit = (byte) (remaining & 0x7F);
            remaining >>>= 7;
            if (remaining > 0) {
                digit |= 0x80;
            }
            nextWriteBuffer.write(digit);
        } while (remaining > 0);
        for(Buffer buffer : frame.buffers) {
            nextWriteBuffer.write(buffer.data, buffer.offset, buffer.length);
        }
    }

    @Override
    protected Action initialDecodeAction() {
        return readHeader;
    }

    private final Action readHeader = new Action() {
        public MQTTFrame apply() throws IOException {
            int length = readLength();
            if( length >= 0 ) {
                if( length > maxMessageLength) {
                    throw new IOException("The maximum message length was exceeded");
                }
                byte header = readBuffer.get(readStart);
                readStart = readEnd;
                if( length > 0 ) {
                    nextDecodeAction = readBody(header, length);
                } else {
                    return new MQTTFrame().header(header);
                }
            }
            return null;
        }
    };

    private int readLength() throws IOException {
        readEnd = readStart+2; // Header is at least 2 bytes..
        int limit = readBuffer.position();
        int length = 0;
        int multiplier = 1;
        byte digit;

        while (readEnd-1 < limit) {
            // last byte is part of the encoded length..
            digit = readBuffer.get(readEnd-1);
            length += (digit & 0x7F) * multiplier;
            if( (digit & 0x80) == 0 ) {
                return length;
            }

            // length extends out one more byte..
            multiplier <<= 7;
            readEnd++;
        }
        return -1;
    }

    Action readBody(final byte header, final int length) {
        return new Action() {
            public MQTTFrame apply() throws IOException {
                int limit = readBuffer.position();
                if ((limit - readStart) < length) {
                    readEnd = limit;
                    return null;
                } else {
                    Buffer body = new Buffer(readBuffer.array(), readStart, length);
                    readEnd = readStart = readStart + length;
                    nextDecodeAction = readHeader;
                    return new MQTTFrame(body).header(header);
                }
            }
        };
    }
}
