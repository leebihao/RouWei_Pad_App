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
import org.fusesource.mqtt.client.Topic;

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
public class SUBSCRIBE extends HeaderBase implements Message, Acked {

    public static final byte TYPE = 8;
    public static final Topic[] NO_TOPICS = new Topic[0];

    private short messageId;
    private Topic topics[] = NO_TOPICS;

    public SUBSCRIBE() {
        qos(QoS.AT_LEAST_ONCE);
    }

    public byte messageType() {
        return TYPE;
    }

    public SUBSCRIBE decode(MQTTFrame frame) throws ProtocolException {
        assert(frame.buffers.length == 1);
        header(frame.header());

        DataByteArrayInputStream is = new DataByteArrayInputStream(frame.buffers[0]);
        QoS qos = qos();
        if(qos != QoS.AT_MOST_ONCE) {
            messageId = is.readShort();
        }
        ArrayList<Topic> list = new ArrayList<Topic>();
        while(is.available() > 0) {
            Topic topic = new Topic(MessageSupport.readUTF(is), QoS.values()[is.readByte()]);
            list.add(topic);
        }
        topics = list.toArray(new Topic[list.size()]);
        return this;
    }
    
    public MQTTFrame encode() {
        try {
            DataByteArrayOutputStream os = new DataByteArrayOutputStream();
            QoS qos = qos();
            if(qos != QoS.AT_MOST_ONCE) {
                os.writeShort(messageId);
            }
            for(Topic topic: topics) {
                MessageSupport.writeUTF(os, topic.name());
                os.writeByte(topic.qos().ordinal());
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
    public SUBSCRIBE dup(boolean dup) {
        return (SUBSCRIBE) super.dup(dup);
    }

    @Override
    public QoS qos() {
        return super.qos();
    }

    public short messageId() {
        return messageId;
    }

    public SUBSCRIBE messageId(short messageId) {
        this.messageId = messageId;
        return this;
    }

    public Topic[] topics() {
        return topics;
    }

    public SUBSCRIBE topics(Topic[] topics) {
        this.topics = topics;
        return this;
    }

    @Override
    public String toString() {
        return "SUBSCRIBE{" +
                "dup=" + dup() +
                ", qos=" + qos() +
                ", messageId=" + messageId +
                ", topics=" + (topics == null ? null : Arrays.asList(topics)) +
                '}';
    }
}
