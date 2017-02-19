package com.junglee.task.event.impl;

import com.junglee.task.event.EventContext;
import com.junglee.task.event.Event;

import java.io.Serializable;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public class DefaultEvent implements Event, Serializable
{
    private static final long serialVersionUID = 8188757584720622237L;

    protected EventContext eventContext;
    protected int type;
    protected Object source;
    protected long timeStamp;

    public EventContext getEventContext()
    {
        return eventContext;
    }

    public int getType()
    {
        return type;
    }

    public Object getSource()
    {
        return source;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setEventContext(EventContext context)
    {
        this.eventContext = context;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public void setSource(Object source)
    {
        this.source = source;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;

    }

    @Override
    public String toString() {
        return "Event [type=" + type + ", source=" + source + ", timeStamp="
                + timeStamp + "]";
    }

}
