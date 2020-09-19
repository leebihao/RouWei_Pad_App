/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.client;

/**
 * <p>
 * Function Result that carries one value.
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class ProxyCallback<T> implements Callback<T> {

    public final Callback<T> next;

    public ProxyCallback(Callback<T> next) {
        this.next = next;
    }

    public void onSuccess(T value) {
        if( next!=null ) {
            next.onSuccess(value);
        }
    }

    public void onFailure(Throwable value) {
        if( next!=null ) {
            next.onFailure(value);
        }
    }
}
