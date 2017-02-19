package com.junglee.task.event;

import com.junglee.task.session.Session;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface SessionEventHandler extends EventHandler
{
    public Session getSession();

    public void setSession(Session session) ;
}
