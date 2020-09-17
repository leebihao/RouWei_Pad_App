/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Promise<T> implements Callback<T>, Future<T> {

    private final CountDownLatch latch = new CountDownLatch(1);
    Callback<T> next;
    Throwable error;
    T value;

    public void onFailure(Throwable value) {
        Callback<T> callback = null;
        synchronized(this)  {
            error = value;
            latch.countDown();
            callback = next;
        }
        if( callback!=null ) {
            callback.onFailure(value);
        }
    }

    public void onSuccess(T value) {
        Callback<T> callback = null;
        synchronized(this)  {
            this.value = value;
            latch.countDown();
            callback = next;
        }
        if( callback!=null ) {
            callback.onSuccess(value);
        }
    }

    public void then(Callback<T> callback) {
        boolean fire = false;
        synchronized(this)  {
            next = callback;
            if( latch.getCount() == 0 ) {
                fire = true;
            }
        }
        if( fire ) {
            if( error!=null ) {
                callback.onFailure(error);
            } else {
                callback.onSuccess(value);
            }
        }
    }

    public T await(long amount, TimeUnit unit) throws Exception {
        if( latch.await(amount, unit) ) {
            return get();
        } else {
            throw new TimeoutException();
        }
    }

    public T await() throws Exception {
        latch.await();
        return get();
    }

    private T get() throws Exception {
        Throwable e = error;
        if( e !=null ) {
            if( e instanceof RuntimeException ) {
                throw (RuntimeException) e;
            } else if( e instanceof Exception) {
                throw (Exception) e;
            } else if( e instanceof Error) {
                throw (Error) e;
            } else {
                // don't expect to hit this case.
                throw new RuntimeException(e);
            }
        }
        return value;
    }

}
