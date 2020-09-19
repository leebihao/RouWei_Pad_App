/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */
package org.fusesource.mqtt.client;

import com.scinan.sdk.util.LogUtil;

import org.fusesource.mqtt.codec.MQTTFrame;

/**
 * A subclass of this can be configured on an MQTT connection to
 * get more insight into what it's doing.
 */
public class Tracer {

    /**
     * Override to log/capture debug level messages
     * @param message
     * @param args
     */
    public void debug(String message, Object...args) {
        LogUtil.d("^^^^^^^" + message);
    }

    /**
     * Called when a MQTTFrame sent to the remote peer.
     * @param frame
     */
    public void onSend(MQTTFrame frame) {
        LogUtil.d("^^onSend^^^^^" + frame.toString());
    }

    /**
     * Called when a MQTTFrame is received from the remote peer.
     * @param frame
     */
    public void onReceive(MQTTFrame frame) {
        LogUtil.d("^^^onReceive^^^^" + frame.toString());
    }

}
