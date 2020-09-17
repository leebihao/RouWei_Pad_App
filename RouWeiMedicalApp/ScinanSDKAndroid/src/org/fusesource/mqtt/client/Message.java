/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.client;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.DispatchQueue;
import org.fusesource.hawtdispatch.TaskWrapper;

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Message {

    private UTF8Buffer topic;
    private Buffer payload;
    private Runnable onComplete;
    private DispatchQueue queue;

    public Message(DispatchQueue queue, UTF8Buffer topic, Buffer payload, Runnable onComplete) {
        this.queue = queue;
        this.payload = payload;
        this.topic = topic;
        this.onComplete = onComplete;
    }

    public byte[] getPayload() {
        return payload.toByteArray();
    }

    /**
     * Using getPayloadBuffer() is lower overhead version of getPayload()
     * since it avoids a byte array copy.
     * @return
     */
    public Buffer getPayloadBuffer() {
        return payload;
    }

    public String getTopic() {
        return topic.toString();
    }

    /**
     * Using getTopicBuffer is lower overhead version of getTopic()
     * since it avoid doing UTF-8 decode.
     * @return
     */
    public UTF8Buffer getTopicBuffer() {
        return topic;
    }

    public void ack() {
        if(onComplete!=null) {
            queue.execute(new TaskWrapper(onComplete));
            onComplete = null;
        }
    }

}
