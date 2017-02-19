package com.junglee.task.event;

import com.junglee.task.session.Session;

import java.util.List;

/**
 * Created by vishwasourabh.sahay on 16/02/17.
 */
public interface EventDispatcher
{
    public void addHandler( EventHandler eventHandler );

    public List<EventHandler> getHandlers(int eventType);

    public void removeHandler(EventHandler eventHandler);


    public void removeHandlersForEvent(int eventType);

    boolean removeHandlersForSession(Session session);

    void clear();

    public void fireEvent( Event event );
    public void close();

}