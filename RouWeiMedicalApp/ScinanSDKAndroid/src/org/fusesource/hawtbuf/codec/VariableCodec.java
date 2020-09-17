/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */
package org.fusesource.hawtbuf.codec;

/**
 * Convenience base class for Marshaller implementations which do not deepCopy and
 * which use variable size encodings.
 * 
 * @author chirino
 * @param <T>
 */
abstract public class VariableCodec<T> implements Codec<T> {

    public int getFixedSize() {
        return -1;
    }

    public boolean isDeepCopySupported() {
        return false;
    }

    public T deepCopy(T source) {
        throw new UnsupportedOperationException();
    }

    public boolean isEstimatedSizeSupported() {
        return false;
    }

    public int estimatedSize(T object) {
        throw new UnsupportedOperationException();
    }
}
