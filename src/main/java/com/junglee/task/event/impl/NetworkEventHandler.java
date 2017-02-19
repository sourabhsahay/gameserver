package com.junglee.task.event.impl;

import com.junglee.task.event.Events;
import com.junglee.task.event.SessionEventHandler;
import com.junglee.task.session.Session;
import com.junglee.task.event.Event;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public class NetworkEventHandler implements SessionEventHandler
{

    private static final int EVENT_TYPE = Events.NETWORK_MESSAGE;
    private final Session session;

    public NetworkEventHandler(Session session)
    {
        this.session = session;
    }

    public void onEvent(Event event)
    {
        session.onEvent(event);
    }

    public int getEventType()
    {
        return EVENT_TYPE;
    }

    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        throw new UnsupportedOperationException(
                "Session is a final field in this class. "
                        + "It cannot be reset");
    }

}