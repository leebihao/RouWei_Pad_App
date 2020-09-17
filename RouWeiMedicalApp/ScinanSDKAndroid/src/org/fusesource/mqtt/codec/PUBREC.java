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
public class PUBREC extends AckBase implements Message {

    public static final byte TYPE = 5;

    public byte messageType() {
        return TYPE;
    }

    @Override
    public PUBREC decode(MQTTFrame frame) throws ProtocolException {
        return (PUBREC) super.decode(frame);
    }

    @Override
    public PUBREC messageId(short messageId) {
        return (PUBREC) super.messageId(messageId);
    }

}
