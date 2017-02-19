package com.junglee.task.event.impl;

import com.junglee.task.event.EventContext;
import com.junglee.task.session.Session;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public class DefaultEventContext implements EventContext
{

    private Object attachement;
    private Session session;

    public Object getAttachment()
    {
        return attachement;
    }

    public Session getSession()
    {
        return session;
    }

    public void setAttachment(Object attachement)
    {
        this.attachement = attachement;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

}
