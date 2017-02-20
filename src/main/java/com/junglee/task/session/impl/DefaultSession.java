package com.junglee.task.session.impl;

import com.junglee.task.communication.MessageSender;
import com.junglee.task.session.id.UniqueIDService;
import com.junglee.task.event.Event;
import com.junglee.task.event.EventDispatcher;
import com.junglee.task.event.EventHandler;
import com.junglee.task.event.impl.ExecutorEventDispatcher;
import com.junglee.task.session.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public class DefaultSession implements Session
{
    /**
     * session id
     */
    protected final Object id;
    /**
     * event dispatcher
     */
    protected EventDispatcher eventDispatcher;

    /**
     * session parameters
     */
    protected final Map<String, Object> sessionAttributes;

    protected final long creationTime;

    protected long lastReadWriteTime;

    protected Status status;

    protected boolean isWriteable;

    /**
     * Life cycle variable to check if the session is shutting down. If it is, then no
     * more incoming events will be accepted.
     */
    protected volatile boolean isShuttingDown;

    protected boolean isUDPEnabled;

    protected MessageSender tcpSender = null;

    protected MessageSender udpSender = null;

    protected DefaultSession(SessionBuilder sessionBuilder)
    {
        sessionBuilder.validateAndSetValues();
        this.id = sessionBuilder.id;
        this.eventDispatcher = sessionBuilder.eventDispatcher;
        this.sessionAttributes = sessionBuilder.sessionAttributes;
        this.creationTime = sessionBuilder.creationTime;
        this.status = sessionBuilder.status;
        this.lastReadWriteTime = sessionBuilder.lastReadWriteTime;
        this.isWriteable = sessionBuilder.isWriteable;
        this.isShuttingDown = sessionBuilder.isShuttingDown;
        this.isUDPEnabled = sessionBuilder.isUDPEnabled;
    }

    public static class SessionBuilder
    {
        public SessionBuilder(UniqueIDService uniqueIDService) {
            this.ID_GENERATOR_SERVICE = uniqueIDService;
        }

        /**
         * Used to set a unique id on the incoming sessions to this room.
         */

        protected Object id = null;
        protected EventDispatcher eventDispatcher = null;
        protected UniqueIDService ID_GENERATOR_SERVICE;
        protected Map<String, Object> sessionAttributes = null;
        protected long creationTime = 0L;
        protected long lastReadWriteTime = 0L;
        protected Status status = Status.NOT_CONNECTED;
        protected boolean isWriteable = true;
        protected volatile boolean isShuttingDown = false;
        protected boolean isUDPEnabled = false;// By default UDP is not enabled.

        public Session build()
        {
            return new DefaultSession(this);
        }

        /**
         * This method is used to validate and set the variables to default
         * values if they are not already set before calling build. This method
         * is invoked by the constructor of SessionBuilder. <b>Important!</b>
         * Builder child classes which override this method need to call
         * super.validateAndSetValues(), otherwise you could get runtime NPE's.
         */
        protected void validateAndSetValues(){
            if (null == id)
            {
                id = String.valueOf(ID_GENERATOR_SERVICE.generateFor(DefaultSession.class));
            }
            if (null == eventDispatcher)
            {
                eventDispatcher = new ExecutorEventDispatcher();
            }
            if(null == sessionAttributes)
            {
                sessionAttributes = new HashMap<String, Object>();
            }
            creationTime = System.currentTimeMillis();
        }

        public Object getId()
        {
            return id;
        }
        public SessionBuilder id(final String id)
        {
            this.id = id;
            return this;
        }
        public SessionBuilder eventDispatcher(final EventDispatcher eventDispatcher)
        {
            this.eventDispatcher = eventDispatcher;
            return this;
        }
        public SessionBuilder sessionAttributes(final Map<String, Object> sessionAttributes)
        {
            this.sessionAttributes = sessionAttributes;
            return this;
        }
        public SessionBuilder creationTime(long creationTime)
        {
            this.creationTime = creationTime;
            return this;
        }
        public SessionBuilder lastReadWriteTime(long lastReadWriteTime)
        {
            this.lastReadWriteTime = lastReadWriteTime;
            return this;
        }
        public SessionBuilder status(Status status)
        {
            this.status = status;
            return this;
        }
        public SessionBuilder isWriteable(boolean isWriteable)
        {
            this.isWriteable = isWriteable;
            return this;
        }
        public SessionBuilder isShuttingDown(boolean isShuttingDown)
        {
            this.isShuttingDown = isShuttingDown;
            return this;
        }
        public SessionBuilder isUDPEnabled(boolean isUDPEnabled)
        {
            this.isUDPEnabled = isUDPEnabled;
            return this;
        }
    }

    public void onEvent(Event event)
    {
        if(!isShuttingDown){
            eventDispatcher.fireEvent(event);
        }
    }

    public Object getId()
    {
        return id;
    }

    public void setId(Object id)
    {
        throw new IllegalArgumentException("id cannot be set in this implementation, since it is final");
    }

    public EventDispatcher getEventDispatcher()
    {
        return eventDispatcher;
    }

    public void addHandler(EventHandler eventHandler)
    {
        eventDispatcher.addHandler(eventHandler);
    }

    public void removeHandler(EventHandler eventHandler)
    {
        eventDispatcher.removeHandler(eventHandler);
    }

    public List<EventHandler> getEventHandlers(int eventType)
    {
        return eventDispatcher.getHandlers(eventType);
    }

    public Object getAttribute(String key)
    {
        return sessionAttributes.get(key);
    }

    public void removeAttribute(String key)
    {
        sessionAttributes.remove(key);
    }

    public void setAttribute(String key, Object value)
    {
        sessionAttributes.put(key, value);
    }

    public long getCreationTime()
    {
        return creationTime;
    }

    public long getLastReadWriteTime()
    {
        return lastReadWriteTime;
    }

    public void setLastReadWriteTime(long lastReadWriteTime)
    {
        this.lastReadWriteTime = lastReadWriteTime;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;

    }

    public boolean isConnected()
    {
        return this.status == Status.CONNECTED;
    }

    public boolean isWriteable()
    {
        return isWriteable;
    }

    public void setWriteable(boolean isWriteable)
    {
        this.isWriteable = isWriteable;
    }

    public boolean isUDPEnabled()
    {
        return isUDPEnabled;
    }

    public void setUDPEnabled(boolean isEnabled)
    {
        this.isUDPEnabled = isEnabled;
    }

    public synchronized void close()
    {
        isShuttingDown = true;
        eventDispatcher.close();
        if(null != tcpSender){
            tcpSender.close();
            tcpSender = null;
        }
        if(null != udpSender){
            udpSender.close();
            udpSender = null;
        }
        this.status = Status.CLOSED;
    }

    public boolean isShuttingDown()
    {
        return isShuttingDown;
    }

    public Map<String, Object> getSessionAttributes()
    {
        return sessionAttributes;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultSession other = (DefaultSession) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }

    public MessageSender getTcpSender()
    {
        return tcpSender;
    }

    public void setTcpSender(MessageSender tcpSender)
    {
        this.tcpSender = tcpSender;
    }

    public MessageSender getUdpSender()
    {
        return udpSender;
    }

    public void setUdpSender(MessageSender udpSender)
    {
        this.udpSender = udpSender;
    }
}

