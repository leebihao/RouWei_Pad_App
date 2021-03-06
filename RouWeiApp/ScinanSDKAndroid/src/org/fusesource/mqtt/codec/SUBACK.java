/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.codec;

import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.Arrays;
import static org.fusesource.mqtt.codec.MessageSupport.*;

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class SUBACK implements Message {

    public static final byte[] NO_GRANTED_QOS = new byte[0];
    public static final byte TYPE = 9;

    private short messageId;
    private byte grantedQos[] = NO_GRANTED_QOS;

    public byte messageType() {
        return TYPE;
    }

    public SUBACK decode(MQTTFrame frame) throws ProtocolException {
        assert(frame.buffers.length == 1);
        DataByteArrayInputStream is = new DataByteArrayInputStream(frame.buffers[0]);
        messageId = is.readShort();
        grantedQos = is.readBuffer(is.available()).toByteArray();
        return this;
    }
    
    public MQTTFrame encode() {
        try {
            DataByteArrayOutputStream os = new DataByteArrayOutputStream(2+grantedQos.length);
            os.writeShort(messageId);
            os.write(grantedQos);

            MQTTFrame frame = new MQTTFrame();
            frame.commandType(TYPE);
            return frame.buffer(os.toBuffer());
        } catch (IOException e) {
            throw new RuntimeException("The impossible happened");
        }
    }

    public byte[] grantedQos() {
        return grantedQos;
    }

    public SUBACK grantedQos(byte[] grantedQos) {
        this.grantedQos = grantedQos;
        return this;
    }

    public short messageId() {
        return messageId;
    }

    public SUBACK messageId(short messageId) {
        this.messageId = messageId;
        return this;
    }

    @Override
    public String toString() {
        return "SUBACK{" +
                "grantedQos=" + Arrays.toString(grantedQos) +
                ", messageId=" +messageId +
                '}';
    }
}
