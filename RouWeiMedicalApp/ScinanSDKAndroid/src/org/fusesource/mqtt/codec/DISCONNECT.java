/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.codec;

import java.net.ProtocolException;

import static org.fusesource.mqtt.codec.MessageSupport.*;

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class DISCONNECT extends EmptyBase implements Message {

    public static final byte TYPE = 14;

    public byte messageType() {
        return TYPE;
    }

    @Override
    public DISCONNECT decode(MQTTFrame frame) throws ProtocolException {
        return (DISCONNECT) super.decode(frame);
    }

    @Override
    public String toString() {
        return "DISCONNECT";
    }

}
