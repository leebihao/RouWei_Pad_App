/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.codec;

import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import org.fusesource.mqtt.client.QoS;

import java.io.IOException;
import java.net.ProtocolException;
import static org.fusesource.mqtt.codec.MessageSupport.*;

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class PUBREL extends HeaderBase implements Message, Acked {

    public static final byte TYPE = 6;

    private short messageId;

    public byte messageType() {
        return TYPE;
    }
    
    public PUBREL() {
        qos(QoS.AT_LEAST_ONCE);
    }

    public PUBREL decode(MQTTFrame frame) throws ProtocolException {
        assert(frame.buffers.length == 1);
        header(frame.header());
        DataByteArrayInputStream is = new DataByteArrayInputStream(frame.buffers[0]);
        messageId = is.readShort();
        return this;
    }
    
    public MQTTFrame encode() {
        try {
            DataByteArrayOutputStream os = new DataByteArrayOutputStream(2);
            os.writeShort(messageId);

            MQTTFrame frame = new MQTTFrame();
            frame.header(header());
            frame.commandType(TYPE);
            return frame.buffer(os.toBuffer());
        } catch (IOException e) {
            throw new RuntimeException("The impossible happened");
        }
    }


    @Override
    public boolean dup() {
        return super.dup();
    }

    @Override
    public PUBREL dup(boolean dup) {
        return (PUBREL) super.dup(dup);
    }

    @Override
    public QoS qos() {
        return super.qos();
    }

    public short messageId() {
        return messageId;
    }

    public PUBREL messageId(short messageId) {
        this.messageId = messageId;
        return this;
    }

    @Override
    public String toString() {
        return "PUBREL{" +
                "dup=" + dup() +
                ", qos=" + qos() +
                ", messageId=" + messageId +
                '}';
    }
}
