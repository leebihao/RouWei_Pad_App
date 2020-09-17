/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.client;

import org.fusesource.hawtbuf.UTF8Buffer;

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Topic {

    private final UTF8Buffer name;
    private final QoS qos;

    public Topic(String name, QoS qos) {
        this(new UTF8Buffer(name), qos);
    }

    public Topic(UTF8Buffer name, QoS qos) {
        this.name = name;
        this.qos = qos;
    }

    public UTF8Buffer name() {
        return name;
    }

    public QoS qos() {
        return qos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;

        Topic topic = (Topic) o;

        if (name != null ? !name.equals(topic.name) : topic.name != null) return false;
        if (qos != topic.qos) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (qos != null ? qos.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{ name=" + name +
                ", qos=" + qos +
                " }";
    }
}
