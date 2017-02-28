package com.junglee.task.session;

import com.junglee.task.communication.MessageSender;
import com.junglee.task.event.EventDispatcher;
import com.junglee.task.event.EventHandler;
import com.junglee.task.event.Event;

import java.util.List;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */

public interface Session
{
    /**
     * session status types
     */
    enum Status
    {
        NOT_CONNECTED, CONNECTING, CONNECTED, CLOSED
    }

    Object getId();

    void setId(Object id);

    void setAttribute(String key, Object value);

    Object getAttribute(String key);

    void removeAttribute(String key);

    void onEvent(Event event);

    EventDispatcher getEventDispatcher();


    boolean isWriteable();

    void setWriteable(boolean writeable);

    boolean isUDPEnabled();

    void setUDPEnabled(boolean isEnabled);

    boolean isShuttingDown();

    long getCreationTime();

    long getLastReadWriteTime();

    void setStatus(Status status);

    Status getStatus();

    boolean isConnected();

    void addHandler(EventHandler eventHandler);

    void removeHandler(EventHandler eventHandler);

    List<EventHandler> getEventHandlers(int eventType);

    void close();

    public abstract void setUdpSender(MessageSender udpSender);

    public abstract MessageSender getUdpSender();

    public abstract void setTcpSender(MessageSender tcpSender);

    public abstract MessageSender getTcpSender();
}
