/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.client;

import java.util.concurrent.TimeUnit;

/**
 * <p>A simplified Future function results interface.</p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public interface Future<T> {
    T await() throws Exception;
    T await(long amount, TimeUnit unit) throws Exception;
    void then(Callback<T> callback);

}
