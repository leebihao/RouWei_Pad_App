/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.codec;

import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.QoS;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Arrays;
import static org.fusesource.mqtt.codec.MessageSupport.*;

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class UNSUBSCRIBE extends HeaderBase implements Message, Acked {

    public static final byte TYPE = 10;
    public static final UTF8Buffer[] NO_TOPICS = new UTF8Buffer[0];

    private short messageId;
    private UTF8Buffer topics[] = NO_TOPICS;

    public UNSUBSCRIBE() {
        qos(QoS.AT_LEAST_ONCE);
    }

    public byte messageType() {
        return TYPE;
    }

    public UNSUBSCRIBE decode(MQTTFrame frame) throws ProtocolException {
        assert(frame.buffers.length == 1);
        header(frame.header());

        DataByteArrayInputStream is = new DataByteArrayInputStream(frame.buffers[0]);

        messageId = is.readShort();
        ArrayList<UTF8Buffer> list = new ArrayList<UTF8Buffer>();
        while(is.available() > 0) {
            list.add(MessageSupport.readUTF(is));
        }
        topics = list.toArray(new UTF8Buffer[list.size()]);
        return this;
    }
    
    public MQTTFrame encode() {
        try {
            DataByteArrayOutputStream os = new DataByteArrayOutputStream();
            QoS qos = qos();
            if(qos != QoS.AT_MOST_ONCE) {
                os.writeShort(messageId);
            }
            for(UTF8Buffer topic: topics) {
                MessageSupport.writeUTF(os, topic);
            }

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
    public UNSUBSCRIBE dup(boolean dup) {
        return (UNSUBSCRIBE) super.dup(dup);
    }

    @Override
    public QoS qos() {
        return super.qos();
    }

    public short messageId() {
        return messageId;
    }

    public UNSUBSCRIBE messageId(short messageId) {
        this.messageId = messageId;
        return this;
    }

    public UTF8Buffer[] topics() {
        return topics;
    }

    public UNSUBSCRIBE topics(UTF8Buffer[] topics) {
        this.topics = topics;
        return this;
    }

    @Override
    public String toString() {
        return "UNSUBSCRIBE{" +
                "dup=" + dup() +
                ", qos=" + qos() +
                ", messageId=" + messageId +
                ", topics=" + (topics == null ? null : Arrays.asList(topics)) +
                '}';
    }
}
