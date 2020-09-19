/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.client;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Task;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.fusesource.hawtbuf.Buffer.utf8;

/**
 * <p>
 * A blocking Connection interface to MQTT.
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class BlockingConnection {

    private final FutureConnection next;

    public BlockingConnection(FutureConnection next) {
        this.next = next;
    }

    public boolean isConnected() {
        return next.isConnected();
    }

    public void connect() throws Exception {
        this.next.connect().await();
    }

    public void disconnect() throws Exception {
        this.next.disconnect().await();
    }

    public void kill() throws Exception {
        this.next.kill().await();
    }

    public byte[] subscribe(final Topic[] topics) throws Exception {
        return this.next.subscribe(topics).await();
    }

    public void unsubscribe(final String[] topics) throws Exception {
        this.next.unsubscribe(topics).await();
    }

    public void unsubscribe(final UTF8Buffer[] topics) throws Exception {
        this.next.unsubscribe(topics).await();
    }

    public void publish(final UTF8Buffer topic, final Buffer payload, final QoS qos, final boolean retain) throws Exception {
        this.next.publish(topic, payload, qos, retain).await();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void publish(final String topic, final byte[] payload, final QoS qos, final boolean retain) throws Exception {
        publish(utf8(topic), new Buffer(payload), qos, retain);
    }

    public Message receive() throws Exception {
        return this.next.receive().await();
    }

    /**
     * @return null if the receive times out.
     */
    public Message receive(long amount, TimeUnit unit) throws Exception {
        Future<Message> receive = this.next.receive();
        try {
            return receive.await(amount, unit);
        } catch (TimeoutException e) {
            // Put it back on the queue..
            receive.then(new Callback<Message>() {
                public void onSuccess(final Message value) {
                    next.putBackMessage(value);
                }
                public void onFailure(Throwable value) {
                }
            });
            return null;
        }
    }

    public void resume() {
        next.resume();
    }

    public void suspend() {
        next.suspend();
    }
}
