/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package org.fusesource.mqtt.client;

import com.scinan.sdk.api.v2.network.base.VendorSSLSocketFactory;
import com.scinan.sdk.push.HeartBeatMonitor;
import com.scinan.sdk.push.HeartBeatWakeLock;
import com.scinan.sdk.push.SslTransport;
import com.scinan.sdk.push.TcpTransport;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;

import static org.fusesource.hawtbuf.Buffer.utf8;
import static org.fusesource.hawtdispatch.Dispatch.createQueue;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.HexSupport;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.hawtdispatch.DispatchQueue;
import org.fusesource.hawtdispatch.Task;
import org.fusesource.hawtdispatch.transport.DefaultTransportListener;
import org.fusesource.hawtdispatch.transport.Transport;
import org.fusesource.mqtt.codec.CONNACK;
import org.fusesource.mqtt.codec.DISCONNECT;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.fusesource.mqtt.codec.MQTTProtocolCodec;
import org.fusesource.mqtt.codec.MessageSupport.Acked;
import org.fusesource.mqtt.codec.PINGREQ;
import org.fusesource.mqtt.codec.PINGRESP;
import org.fusesource.mqtt.codec.PUBACK;
import org.fusesource.mqtt.codec.PUBCOMP;
import org.fusesource.mqtt.codec.PUBLISH;
import org.fusesource.mqtt.codec.PUBREC;
import org.fusesource.mqtt.codec.PUBREL;
import org.fusesource.mqtt.codec.SUBACK;
import org.fusesource.mqtt.codec.SUBSCRIBE;
import org.fusesource.mqtt.codec.UNSUBACK;
import org.fusesource.mqtt.codec.UNSUBSCRIBE;


/**
 * <p>
 * A callback based non/blocking Connection interface to MQTT.
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class CallbackConnection implements Serializable {
    
    private static class Request {
        final MQTTFrame frame;
        private final short id;
        final Callback cb;

        Request(int id, MQTTFrame frame, Callback cb) {
            this.id = (short) id;
            this.cb = cb;
            this.frame = frame;
        }
    }

    public void reset() {
        if (mqtt != null)
            mqtt.setCleanSession(true);
        disconnected = false;
        transport = null;
        reconnects = 0;
        suspendCount = new AtomicInteger(0);
        suspendChanges = new AtomicInteger(0);
        activeSubs.clear();
        if (heartBeatMonitor != null) {
            heartBeatMonitor.cancelAlarm();
            heartBeatMonitor.setOnKeepAlive(NOOP);
        }
    }

    public void sendHeartBeat() {
        LogUtil.d("============" + heartBeatMonitor);
        try {
            if (heartBeatMonitor != null)
                heartBeatMonitor.runTask();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final Listener DEFAULT_LISTENER = new Listener(){
        public void onConnected() {
        }
        public void onDisconnected() {
        }
        public void onPublish(UTF8Buffer utf8Buffer, Buffer buffer, Runnable runnable) {
            this.onFailure(createListenerNotSetError());
        }
        public void onFailure(Throwable value) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), value);
        }

        @Override
        public void onReconnect() {
        }
    };

    private final DispatchQueue queue;
    private final MQTT mqtt;
    private Transport transport;
    private Listener listener = DEFAULT_LISTENER;
    private Runnable refiller;
    private Map<Short, Request> requests = new ConcurrentHashMap<Short, Request>();
    private LinkedList<Request> overflow = new LinkedList<Request>();
    private HashSet<Short> processed = new HashSet<Short>();
    private Throwable failure;
    private boolean disconnected = false;
    private HeartBeatMonitor heartBeatMonitor;
    private volatile long pingedAt;
    private volatile long pingedLast;
    private long reconnects = 0;
    private AtomicInteger suspendCount = new AtomicInteger(0);
    private AtomicInteger suspendChanges = new AtomicInteger(0);

    private HashMap<UTF8Buffer, QoS> activeSubs = new HashMap<UTF8Buffer, QoS>();


    public CallbackConnection(MQTT mqtt) {
        this.mqtt = mqtt;
        if(this.mqtt.dispatchQueue == null) {
            this.queue = createQueue("mqtt client");
        } else {
            this.queue = this.mqtt.dispatchQueue;
        }
    }

    public void connect(final Callback<Void> cb) {
        assert cb !=null : "Callback should not be null.";
        if( transport!=null ) {
            cb.onFailure(new IllegalStateException("Already connected"));
            return;
        }
        try {
            createTransport(new LoginHandler(cb, true));
        } catch (Throwable e) {
            // This error happens when the MQTT config is invalid, reattempting
            // wont fix this case.
            cb.onFailure(e);
        }
    }

    void reconnect() {
        try {
            pingedAt = 0;
            // And reconnect.
            createTransport(new LoginHandler(new Callback<Void>() {
                public void onSuccess(Void value) {

                    mqtt.tracer.debug("Restoring MQTT connection state");
                    // Setup a new overflow so that the replay can be sent out before the original overflow list.
                    LinkedList<Request> originalOverflow = overflow;
                    Map<Short, Request> originalRequests = requests;
                    overflow = new LinkedList<Request>();
                    requests = new ConcurrentHashMap<Short, Request>();

                    // Restore any active subscriptions.
                    if (!activeSubs.isEmpty()) {
                        ArrayList<Topic> topics = new ArrayList<Topic>(activeSubs.size());
                        for (Map.Entry<UTF8Buffer, QoS> entry : activeSubs.entrySet()) {
                            topics.add(new Topic(entry.getKey(), entry.getValue()));
                        }
                        send(new SUBSCRIBE().topics(topics.toArray(new Topic[topics.size()])), null);
                    }

                    // Replay any un-acked requests..
                    for (Map.Entry<Short, Request> entry : originalRequests.entrySet()) {
                        MQTTFrame frame = entry.getValue().frame;
                        frame.dup(true); // set the dup flag as these frames were previously transmitted.
                        send(entry.getValue());
                    }

                    // Replay the original overflow
                    for (Request request : originalOverflow) {
                        // Stuff in the overflow never got sent out.. so no need to set the dup flag
                        send(request);
                    }

                }

                public void onFailure(Throwable value) {
                    handleFatalFailure(value);
                }
            }, false));
        } catch (Throwable e) {
            handleFatalFailure(e);
        }
    }
    void handleSessionFailure(Throwable error) {
        error.printStackTrace();
        // Socket failure, should we try to reconnect?
        if( !disconnected && (mqtt.reconnectAttemptsMax<0 || reconnects < mqtt.reconnectAttemptsMax ) ) {

            mqtt.tracer.debug("Reconnecting transport");
            // Cleanup the previous transport.
            if(heartBeatMonitor!=null) {
                heartBeatMonitor.stop();
                heartBeatMonitor = null;
            }
            final Transport t = transport;
            transport = null;

            if(t!=null) {
                t.stop(new Task() {
                    @Override
                    public void run() {
                        listener.onDisconnected();
                        reconnect();
                    }
                });
            } else {
                reconnect();
            }

        } else {
            // nope.
            handleFatalFailure(error);
        }
    }

    void reconnect(final Callback<Transport> onConnect) {
        long reconnectDelay = mqtt.reconnectDelay;
        if( reconnectDelay> 0 && mqtt.reconnectBackOffMultiplier > 1.0 ) {
            reconnectDelay = (long) Math.pow(mqtt.reconnectDelay*reconnects, mqtt.reconnectBackOffMultiplier);
        }
        reconnectDelay = Math.min(reconnectDelay, mqtt.reconnectDelayMax);
        reconnects += 1;
        queue.executeAfter(reconnectDelay, TimeUnit.MILLISECONDS, new Task() {
            @Override
            public void run() {
                if(disconnected) {
                    onConnect.onFailure(createDisconnectedError());
                } else {
                    try {
                        createTransport(onConnect);
                    } catch (Exception e) {
                        onConnect.onFailure(e);
                    }
                }
            }
        });
    }

    /**
     * Creates and start a transport to the MQTT server.  Passes it to the onConnect
     * once the transport is connected.
     *
     * @param onConnect
     * @throws Exception
     */
    void createTransport(final Callback<Transport> onConnect) throws Exception {
        mqtt.tracer.debug("Connecting");
        String scheme = mqtt.host.getScheme();
        final TcpTransport transport;
        if( "tcp".equals(scheme) ) {
            transport = new TcpTransport(mqtt.getContext());
        }  else if( SslTransport.protocol(scheme)!=null ) {
            SslTransport ssl = new SslTransport(mqtt.getContext());
            if( mqtt.sslContext == null ) {
                mqtt.sslContext = SSLContext.getDefault();
            }
            ssl.setSSLContext(mqtt.sslContext);
            transport = ssl;
        } else {
            throw new Exception("Unsupported URI scheme '"+scheme+"'");
        }

        if( mqtt.blockingExecutor == null ) {
            mqtt.blockingExecutor = MQTT.getBlockingThreadPool();
        }
        transport.setBlockingExecutor(mqtt.blockingExecutor);
        transport.setDispatchQueue(queue);
        transport.setProtocolCodec(new MQTTProtocolCodec());

        if( transport instanceof TcpTransport ) {
            TcpTransport tcp = (TcpTransport)transport;
            tcp.setMaxReadRate(mqtt.maxReadRate);
            tcp.setMaxWriteRate(mqtt.maxWriteRate);
            tcp.setReceiveBufferSize(mqtt.receiveBufferSize);
            tcp.setSendBufferSize(mqtt.sendBufferSize);
            tcp.setTrafficClass(mqtt.trafficClass);
            tcp.setUseLocalHost(mqtt.useLocalHost);
            tcp.connecting(mqtt.host, mqtt.localAddress);
        }

        if (transport instanceof SslTransport) {
            SslTransport tcp = (SslTransport)transport;
            tcp.setMaxReadRate(mqtt.maxReadRate);
            tcp.setMaxWriteRate(mqtt.maxWriteRate);
            tcp.setReceiveBufferSize(mqtt.receiveBufferSize);
            tcp.setSendBufferSize(mqtt.sendBufferSize);
            tcp.setTrafficClass(mqtt.trafficClass);
            tcp.setUseLocalHost(mqtt.useLocalHost);
            tcp.connecting(mqtt.host, mqtt.localAddress);
        }

        transport.setTransportListener(new DefaultTransportListener(){
            @Override
            public void onTransportConnected() {
                mqtt.tracer.debug("Transport connected");
                if(disconnected) {
                    onFailure(createDisconnectedError());
                } else {
                    onConnect.onSuccess(transport);
                }
            }

            @Override
            public void onTransportFailure(final IOException error) {
                mqtt.tracer.debug("Transport failure: %s", error);
                onFailure(error);
            }

            private void onFailure(final Throwable error) {
                if(!transport.isClosed()) {
                    transport.stop(new Task() {
                        @Override
                        public void run() {
                            onConnect.onFailure(error);
                        }
                    });
                }
            }
        });
        transport.start(NOOP);
    }

    class LoginHandler implements Callback<Transport> {
        final Callback<Void> cb;
        private final boolean initialConnect;

        LoginHandler(Callback<Void> cb, boolean initialConnect) {
            this.cb = cb;
            this.initialConnect = initialConnect;
        }

        public void onSuccess(final Transport transport) {
            transport.setTransportListener(new DefaultTransportListener() {
                @Override
                public void onTransportFailure(IOException error) {
                    mqtt.tracer.debug("Transport failure: %s", error);
                    transport.stop(NOOP);
                    onFailure(error);
                }

                @Override
                public void onTransportCommand(Object command) {
                    MQTTFrame response = (MQTTFrame) command;
                    mqtt.tracer.onReceive(response);
                    try {
                        switch (response.messageType()) {
                            case CONNACK.TYPE:
                                CONNACK connack = new CONNACK().decode(response);
                                switch (connack.code()) {
                                    case CONNECTION_ACCEPTED:
                                        mqtt.tracer.debug("MQTT login accepted");
                                        onSessionEstablished(transport);
                                        cb.onSuccess(null);
                                        listener.onConnected();
                                        queue.execute(new Task() {
                                            @Override
                                            public void run() {
                                                drainOverflow();
                                            }
                                        });
                                        break;
                                    default:
                                        mqtt.tracer.debug("MQTT login rejected");
                                        // Bad creds or something. No point in reconnecting.
                                        transport.stop(NOOP);
                                        cb.onFailure(new MQTTException("Could not connect: " + connack.code(), connack));
                                }
                                break;
                            default:
                                mqtt.tracer.debug("Received unexpected MQTT frame: %d", response.messageType());
                                // Naughty MQTT server? No point in reconnecting.
                                transport.stop(NOOP);
                                cb.onFailure(new IOException("Could not connect. Received unexpected command: " + response.messageType()));

                        }
                    } catch (ProtocolException e) {
                        mqtt.tracer.debug("Protocol error: %s", e);
                        transport.stop(NOOP);
                        cb.onFailure(e);
                    }
                }
            });
            transport.resumeRead();
            if( mqtt.connect.clientId() == null ) {
                String id = hex(transport.getLocalAddress())+Long.toHexString(System.currentTimeMillis()/1000);
                if(id.length() > 23) {
                    id = id.substring(0,23);
                }
                mqtt.connect.clientId(utf8(id));
            }
            MQTTFrame encoded = mqtt.connect.encode();
            boolean accepted = transport.offer(encoded);
            mqtt.tracer.onSend(encoded);
            mqtt.tracer.debug("Logging in");
            assert accepted: "First frame should always be accepted by the transport";
        }
        
        private boolean tryReconnect() {
            if(initialConnect) {
                return mqtt.connectAttemptsMax<0 || reconnects < mqtt.connectAttemptsMax;
            }
            
            return mqtt.reconnectAttemptsMax<0 || reconnects < mqtt.reconnectAttemptsMax;
        }

        public void onFailure(Throwable value) {
            // Socket failure, should we try to reconnect?
            if( !disconnected && tryReconnect() ) {
                reconnect(this);
            } else {
                // nope.
                cb.onFailure(value);
            }
        }
    }

    boolean onRefillCalled =false;
    public void onSessionEstablished(Transport transport) {
        this.transport = transport;
        if( suspendCount.get() > 0 ) {
            this.transport.suspendRead();
        }
        this.transport.setTransportListener(new DefaultTransportListener() {
            @Override
            public void onTransportCommand(Object command) {
                MQTTFrame frame = (MQTTFrame) command;
                mqtt.tracer.onReceive(frame);
                processFrame(frame);
            }
            @Override
            public void onRefill() {
                onRefillCalled =true;
                drainOverflow();
            }

            @Override
            public void onTransportFailure(IOException error) {
                handleSessionFailure(error);
            }
        });
        pingedAt = 0;
        if(mqtt.getKeepAlive()>0) {
            heartBeatMonitor = new HeartBeatMonitor(mqtt.getContext());
            heartBeatMonitor.setWriteInterval((mqtt.getKeepAlive() * 1000) / 2);
            heartBeatMonitor.setTransport(this.transport);
            heartBeatMonitor.suspendRead(); // to match the suspended state of the transport.
            heartBeatMonitor.setOnKeepAlive(new Task() {
                @Override
                public void run() {
                    HeartBeatWakeLock.acquireWakeLock(mqtt.getContext());
                    // Don't care if the offer is rejected, just means we have data outbound.
                    if (!disconnected && pingedAt == 0) {
                        MQTTFrame encoded = new PINGREQ().encode();
                        if (CallbackConnection.this.transport.offer(encoded)) {
                            mqtt.tracer.onSend(encoded);
                            long current = System.currentTimeMillis();

                            //两次发送心跳的时间间隔必须大于5秒
                            if (current - pingedLast < 5000) {
                                LogUtil.t("send heartbeat fail4 because send to fast delta is " + (current - pingedLast));
                                return;
                            }
                            pingedAt = current;
                            pingedLast = current;
                            LogUtil.t("send the ping request ! " + AndroidUtil.getGMT8String(pingedAt));
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    //心跳返回时间检查，大于三秒将重新连接
                                    if (pingedAt != 0) {
                                        try {
                                            LogUtil.t("send heartbeat fail0 to reconnect push service " + (System.currentTimeMillis() - pingedAt));
                                            listener.onReconnect();
                                        } catch (Exception e) {
                                        }

                                    } else {
                                        LogUtil.t("receive heartbeat success in timeout check");
                                    }
                                }
                            }, 4500);
                        } else {
                            LogUtil.t("send heartbeat fail1 to reconnect push service");
                            try {
                                listener.onReconnect();
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        if (disconnected) {
                            LogUtil.t("send heartbeat fail2 to reconnect push service");
                            try {
                                listener.onReconnect();
                            } catch (Exception e) {
                            }
                        } else {
                            LogUtil.t("send heartbeat fail3 why send heartbeat so fast " + (System.currentTimeMillis() - pingedAt));
                        }
                    }

                }
            });
            heartBeatMonitor.setPingResp(new Task() {
                @Override
                public void run() {
                    HeartBeatWakeLock.acquireWakeLock(mqtt.getContext());
                    if (!disconnected && pingedAt == 0) {
                        MQTTFrame encoded = new PINGRESP().encode();
                        if (CallbackConnection.this.transport.offer(encoded)) {
                            mqtt.tracer.onSend(encoded);
                            HeartBeatWakeLock.acquireWakeLock(mqtt.getContext());
                        }
                    }
                }
            });
            heartBeatMonitor.start();
        }
    }

    public Transport transport() {
        return transport;
    }

    public DispatchQueue getDispatchQueue() {
        return queue;
    }

    public void resume() {
        suspendChanges.incrementAndGet();
        if( suspendCount.decrementAndGet() == 0 && this.transport!=null ) {
            this.transport.resumeRead();
            if(this.heartBeatMonitor!=null){
                this.heartBeatMonitor.resumeRead();
            }
        }
    }

    public void suspend() {
        suspendChanges.incrementAndGet();
        if( suspendCount.incrementAndGet() == 1 && this.transport!=null ) {
            this.transport.suspendRead();
            if(this.heartBeatMonitor!=null){
                this.heartBeatMonitor.suspendRead();
            }
        }
    }

    public CallbackConnection refiller(Runnable refiller) {
        queue.assertExecuting();
        this.refiller = refiller;
        return this;
    }

    public CallbackConnection listener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public boolean full() {
        queue.assertExecuting();
        return this.transport.full();
    }

    public Throwable failure() {
        queue.assertExecuting();
        return failure;
    }

    public void disconnect(final Callback<Void> onComplete) {
        if( disconnected ) {
            if(onComplete!=null){
                onComplete.onSuccess(null);
            }
            return;
        }
        disconnected = true;
        final short requestId = getNextMessageId();
        final Runnable stop = new Runnable() {
            boolean executed = false;
            public void run() {
                if(!executed) {
                    executed = true;
                    requests.remove(requestId);

                    if(heartBeatMonitor!=null) {
                        heartBeatMonitor.cancelAlarm();
                        heartBeatMonitor.setOnKeepAlive(NOOP);
                        heartBeatMonitor.stop();
                        heartBeatMonitor = null;
                    }
                    transport.stop(new Task() {
                        @Override
                        public void run() {
                            ((TcpTransport) transport).disconnect();
                            listener.onDisconnected();
                            if (onComplete != null) {
                                onComplete.onSuccess(null);
                            }
                        }
                    });
                }
            }
        };
        
        Callback<Void> cb = new Callback<Void>() {
            public void onSuccess(Void v) {
                // To make sure DISCONNECT has been flushed out to the socket
                onRefillCalled = false;
                refiller = new Runnable() {
                    public void run() {
                        if(onRefillCalled) {
                            stop.run();
                        }
                    }
                };
                if(transport != null){
                    transport.flush();
                }
            }
            public void onFailure(Throwable value) {
                stop.run();
            }
        };
        
        // Pop the frame into a request so it we get notified
        // of any failures so we continue to stop the transport.
        if(transport!=null) {
            MQTTFrame frame = new DISCONNECT().encode();
            send(new Request(getNextMessageId(), frame, cb));
        } else {
            cb.onSuccess(null);
        }
    }

    /**
     * Kills the connection without a graceful disconnect.
     * @param onComplete
     */
    public void kill(final Callback<Void> onComplete) {
        if( disconnected ) {
            if(onComplete!=null){
                onComplete.onSuccess(null);
            }
            return;
        }
        disconnected = true;
        if(heartBeatMonitor!=null) {
            heartBeatMonitor.stop();
            heartBeatMonitor = null;
        }
        if (transport != null)
            transport.stop(new Task() {
            @Override
            public void run() {
                listener.onDisconnected();
                if (onComplete != null) {
                    onComplete.onSuccess(null);
                }
            }
        });
    }

    public void publish(String topic, byte[] payload, QoS qos, boolean retain, Callback<Void> cb) {
        publish(utf8(topic), new Buffer(payload), qos, retain, cb);
    }

    public void publish(UTF8Buffer topic, Buffer payload, QoS qos, boolean retain, Callback<Void> cb) {
        queue.assertExecuting();
        if( disconnected ) {
            cb.onFailure(createDisconnectedError());
            return;
        }
        PUBLISH command = new PUBLISH().qos(qos).retain(retain);
        command.topicName(topic).payload(payload);
        send(command, cb);
    }

    public void subscribe(final Topic[] topics, Callback<byte[]> cb) {
        if(topics==null) {
            throw new IllegalArgumentException("topics must not be null");
        }
        queue.assertExecuting();
        if( disconnected ) {
            cb.onFailure(createDisconnectedError());
            return;
        }
        if( listener == DEFAULT_LISTENER ) {
            cb.onFailure(createListenerNotSetError());
        } else {
            send(new SUBSCRIBE().topics(topics), new ProxyCallback<byte[]>(cb) {
                @Override
                public void onSuccess(byte[] value) {
                    for (Topic topic : topics) {
                        activeSubs.put(topic.name(), topic.qos());
                    }
                    if (next != null) {
                        next.onSuccess(value);
                    }
                }
            });
        }
    }

    public void unsubscribe(final UTF8Buffer[] topics, Callback<Void> cb) {
        queue.assertExecuting();
        if( disconnected ) {
            cb.onFailure(createDisconnectedError());
            return;
        }
        send(new UNSUBSCRIBE().topics(topics), new ProxyCallback(cb){
            @Override
            public void onSuccess(Object value) {
                for (UTF8Buffer topic : topics) {
                    activeSubs.remove(topic);
                }
                if(next!=null) {
                    next.onSuccess(value);
                }
            }
        });
    }

    private void send(Acked command, Callback cb) {
        short id = 0;
        if(command.qos() != QoS.AT_MOST_ONCE) {
            id = getNextMessageId();
            command.messageId(id);
        }
        send(new Request(id, command.encode(), cb));
    }

    private void send(Request request) {
        if( failure !=null ) {
            if( request.cb!=null ) {
                request.cb.onFailure(failure);
            }
        } else {
            // Put the request in the map before sending it over the wire. 
            if(request.id!=0) {
                this.requests.put(request.id, request);
            }

            if( overflow.isEmpty() && transport!=null && transport.offer(request.frame) ) {
                mqtt.tracer.onSend(request.frame);
                if(request.id==0) {
                    if( request.cb!=null ) {
                        ((Callback<Void>)request.cb).onSuccess(null);
                    }
                    
                }
            } else {
                // Remove it from the request.
                this.requests.remove(request.id);
                overflow.addLast(request);
            }
        }
    }

    short nextMessageId = 1;
    private short getNextMessageId() {
        short rc = nextMessageId;
        nextMessageId++;
        if(nextMessageId==0) {
            nextMessageId=1;
        }
        return rc;
    }

    private void drainOverflow() {
        queue.assertExecuting();
        if( overflow.isEmpty() || transport==null ){
            return;
        }
        Request request;
        while((request=overflow.peek())!=null) {
            if( this.transport.offer(request.frame) ) {
                mqtt.tracer.onSend(request.frame);
                overflow.removeFirst();
                if(request.id==0) {
                    if( request.cb!=null ) {
                        ((Callback<Void>)request.cb).onSuccess(null);
                    }
                } else {
                    this.requests.put(request.id, request);
                }
            } else {
                break;
            }
        }
        if( overflow.isEmpty() ) {
            if( refiller!=null ) {
                try {
                    refiller.run();
                } catch (Throwable e) {
                    Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                }
            }
        }
    }


    private void completeRequest(short id, byte originalType, Object arg) {
        Request request = requests.remove(id);
        if( request!=null ) {
            assert originalType==request.frame.messageType();
            if(request.cb!=null) {
                if( arg==null ) {
                    ((Callback<Void>)request.cb).onSuccess(null);
                } else {
                    ((Callback<Object>)request.cb).onSuccess(arg);
                }
            }
        } else {
            handleFatalFailure(new ProtocolException("Command from server contained an invalid message id: " + id));
        }
    }

    private void processFrame(MQTTFrame frame) {
        try {
            switch(frame.messageType()) {
                case PUBLISH.TYPE: {
                    PUBLISH publish = new PUBLISH().decode(frame);
                    toReceiver(publish);
                    break;
                }
                case PUBREL.TYPE:{
                    PUBREL ack = new PUBREL().decode(frame);
                    processed.remove(ack.messageId());
                    PUBCOMP response = new PUBCOMP();
                    response.messageId(ack.messageId());
                    send(new Request(0, response.encode(), null));
                    break;
                }
                case PUBACK.TYPE:{
                    PUBACK ack = new PUBACK().decode(frame);
                    completeRequest(ack.messageId(), PUBLISH.TYPE, null);
                    break;
                }
                case PUBREC.TYPE:{
                    PUBREC ack = new PUBREC().decode(frame);
                    PUBREL response = new PUBREL();
                    response.messageId(ack.messageId());
                    send(new Request(0, response.encode(), null));
                    break;
                }
                case PUBCOMP.TYPE:{
                    PUBCOMP ack = new PUBCOMP().decode(frame);
                    completeRequest(ack.messageId(), PUBLISH.TYPE, null);
                    break;
                }
                case SUBACK.TYPE: {
                    SUBACK ack = new SUBACK().decode(frame);
                    completeRequest(ack.messageId(), SUBSCRIBE.TYPE, ack.grantedQos());
                    break;
                }
                case UNSUBACK.TYPE: {
                    UNSUBACK ack = new UNSUBACK().decode(frame);
                    completeRequest(ack.messageId(), UNSUBSCRIBE.TYPE, null);
                    break;
                }
                case PINGRESP.TYPE: {
                    LogUtil.t("received the ping response! " + (System.currentTimeMillis() - pingedAt));
                    pingedAt = 0;
                    break;
                }
                case PINGREQ.TYPE: {
                    heartBeatMonitor.runPingRespTask();
                    break;
                }
                default:
                    throw new ProtocolException("Unexpected MQTT command type: "+frame.messageType());
            }
        } catch (Throwable e) {
            handleFatalFailure(e);
        }
    }

    static public final Task NOOP = Dispatch.NOOP;

    private void toReceiver(final PUBLISH publish) {
        if( listener !=null ) {
            try {
                Runnable cb = NOOP;
                switch( publish.qos() ) {
                    case AT_LEAST_ONCE:
                        cb = new Runnable() {
                            public void run() {
                                PUBACK response = new PUBACK();
                                response.messageId(publish.messageId());
                                send(new Request(0, response.encode(), null));
                            }
                        };
                        break;
                    case EXACTLY_ONCE:
                        cb = new Runnable() {
                            public void run() {
                                PUBREC response = new PUBREC();
                                response.messageId(publish.messageId());
                                processed.add(publish.messageId());
                                send(new Request(0, response.encode(), null));
                            }
                        };
                        // It might be a dup.
                        if( processed.contains(publish.messageId()) ) {
                            cb.run();
                            return;
                        }
                        break;
                    case AT_MOST_ONCE:
                }
                listener.onPublish(publish.topicName(), publish.payload(), cb);
            } catch (Throwable e) {
                handleFatalFailure(e);
            }
        }
    }

    private void handleFatalFailure(Throwable error) {
        if( failure == null ) {
            failure = error;
            
            mqtt.tracer.debug("Fatal connection failure: %s", error);
            // Fail incomplete requests.
            ArrayList<Request> values = new ArrayList(requests.values());
            requests.clear();
            for (Request value : values) {
                if( value.cb!= null ) {
                    value.cb.onFailure(failure);
                }
            }

            ArrayList<Request> overflowEntries = new ArrayList<Request>(overflow);
            overflow.clear();
            for (Request entry : overflowEntries) {
                if( entry.cb !=null ) {
                    entry.cb.onFailure(failure);
                }
            }
            
            if( listener !=null && !disconnected ) {
                try {
                    listener.onFailure(failure);
                } catch (Exception e) {
                    Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                }
            }
        }
    }

    private static IllegalStateException createListenerNotSetError() {
        return (IllegalStateException) new IllegalStateException("No connection listener set to handle message received from the server.").fillInStackTrace();
    }

    private static IllegalStateException createDisconnectedError() {
        return (IllegalStateException) new IllegalStateException("Disconnected").fillInStackTrace();
    }

    static private  String hex(SocketAddress address) {
        if( address instanceof InetSocketAddress ) {
            InetSocketAddress isa = (InetSocketAddress)address;
            return HexSupport.toHexFromBuffer(new Buffer(isa.getAddress().getAddress()))+Integer.toHexString(isa.getPort());
        }
        return "";
    }

}
