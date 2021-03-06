/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.codec;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import org.fusesource.hawtbuf.UTF8Buffer;
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
public class PUBLISH extends HeaderBase implements Message, Acked {

    public static final byte TYPE = 3;

    private UTF8Buffer topicName;
    private short messageId;
    private Buffer payload;

    public PUBLISH() {
        qos(QoS.AT_LEAST_ONCE);
    }

    public byte messageType() {
        return TYPE;
    }

    public PUBLISH decode(MQTTFrame frame) throws ProtocolException {
        assert(frame.buffers.length == 1);
        header(frame.header());

        DataByteArrayInputStream is = new DataByteArrayInputStream(frame.buffers[0]);
        topicName = MessageSupport.readUTF(is);
        
        QoS qos = qos();
        if(qos != QoS.AT_MOST_ONCE) {
            messageId = is.readShort();
        }
        payload = is.readBuffer(is.available());
        if( payload == null ) {
            payload = new Buffer(0);
        }
        return this;
    }
    
    public MQTTFrame encode() {
        try {
            DataByteArrayOutputStream os = new DataByteArrayOutputStream();
            MessageSupport.writeUTF(os, topicName);
            QoS qos = qos();
            if(qos != QoS.AT_MOST_ONCE) {
                os.writeShort(messageId);
            }
            MQTTFrame frame = new MQTTFrame();
            frame.header(header());
            frame.commandType(TYPE);
            if(payload!=null && payload.length!=0) {
                os.write(payload);
            }
            frame.buffer(os.toBuffer());
            return frame;
        } catch (IOException e) {
            throw new RuntimeException("The impossible happened");
        }
    }

    @Override
    public boolean dup() {
        return super.dup();
    }

    @Override
    public PUBLISH dup(boolean dup) {
        return (PUBLISH) super.dup(dup);
    }

    @Override
    public QoS qos() {
        return super.qos();
    }

    @Override
    public PUBLISH qos(QoS qos) {
        return (PUBLISH) super.qos(qos);
    }

    @Override
    public boolean retain() {
        return super.retain();
    }

    @Override
    public PUBLISH retain(boolean retain) {
        return (PUBLISH) super.retain(retain);
    }

    public short messageId() {
        return messageId;
    }

    public PUBLISH messageId(short messageId) {
        this.messageId = messageId;
        return this;
    }

    public Buffer payload() {
        return payload;
    }

    public PUBLISH payload(Buffer payload) {
        this.payload = payload;
        return this;
    }

    public UTF8Buffer topicName() {
        return topicName;
    }

    public PUBLISH topicName(UTF8Buffer topicName) {
        this.topicName = topicName;
        return this;
    }

    @Override
    public String toString() {
        return "PUBLISH{" +
                "dup=" + dup() +
                ", qos=" + qos() +
                ", retain=" + retain() +
                ", messageId=" + messageId +
                ", topicName=" + topicName +
                ", payload=" + payload +
                '}';
    }
}
