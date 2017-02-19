package com.junglee.task.event.impl;

import com.junglee.task.event.*;
import com.junglee.task.session.Session;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */

@Component
public class ExecutorEventDispatcher implements EventDispatcher
{
    private static final ExecutorService EXECUTOR = Executors
            .newFixedThreadPool(10);
    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                EXECUTOR.shutdown();
            }
        });
    }

    private Map<Integer, List<EventHandler>> handlersByEventType;
    private List<EventHandler> genericHandlers;
    private boolean isShuttingDown;

    public ExecutorEventDispatcher()
    {
        this(new HashMap<Integer, List<EventHandler>>(2),
                new CopyOnWriteArrayList<EventHandler>());
    }

    public ExecutorEventDispatcher(
            Map<Integer, List<EventHandler>> handlersByEventType,
            List<EventHandler> genericHandlers)
    {
        this.handlersByEventType = handlersByEventType;
        this.genericHandlers = genericHandlers;
        this.isShuttingDown = false;
    }

    public void addHandler(EventHandler eventHandler)
    {
        int eventType = eventHandler.getEventType();
        synchronized (this)
        {
            if (eventType == Events.ANY)
            {
                genericHandlers.add(eventHandler);
            }
            else
            {
                List<EventHandler> handlers = this.handlersByEventType
                        .get(eventType);
                if (handlers == null)
                {
                    handlers = new CopyOnWriteArrayList<EventHandler>();
                    this.handlersByEventType.put(eventType, handlers);
                }

                handlers.add(eventHandler);
            }
        }
    }

    public List<EventHandler> getHandlers(int eventType)
    {
        return handlersByEventType.get(eventType);
    }

    public void removeHandler(EventHandler eventHandler)
    {
        int eventType = eventHandler.getEventType();
        synchronized (this)
        {
            if (eventType == Events.ANY)
            {
                genericHandlers.remove(eventHandler);
            }
            else
            {
                List<EventHandler> handlers = this.handlersByEventType
                        .get(eventType);
                if (null != handlers)
                {
                    handlers.remove(eventHandler);
                    // Remove the reference if there are no listeners left.
                    if (handlers.size() == 0)
                    {
                        handlersByEventType.put(eventType, null);
                    }
                }
            }
        }

    }

    public void removeHandlersForEvent(int eventType)
    {
        synchronized (this)
        {
            List<EventHandler> handlers = this.handlersByEventType
                    .get(eventType);
            if (null != handlers)
            {
                handlers.clear();
            }
        }
    }


    public boolean removeHandlersForSession(Session session)
    {
        List<EventHandler> removeList = new ArrayList<EventHandler>();
        Collection<List<EventHandler>> eventHandlersList = handlersByEventType
                .values();
        for (List<EventHandler> handlerList : eventHandlersList)
        {
            if (null != handlerList)
            {
                for (EventHandler handler : handlerList)
                {
                    if (handler instanceof SessionEventHandler)
                    {
                        SessionEventHandler sessionHandler = (SessionEventHandler) handler;
                        if (sessionHandler.getSession().equals(session))
                        {
                            removeList.add(handler);
                        }
                    }
                }
            }
        }
        for (EventHandler handler : removeList)
        {
            removeHandler(handler);
        }
        return (removeList.size() > 0);
    }

    public synchronized void clear()
    {
        if(null != handlersByEventType)
        {
            handlersByEventType.clear();
        }
        if(null != genericHandlers)
        {
            genericHandlers.clear();
        }
    }

    public void fireEvent(final Event event)
    {
        boolean isShuttingDown = false;
        synchronized (this)
        {
            isShuttingDown = this.isShuttingDown;
        }
        if (!isShuttingDown)
        {
            EXECUTOR.submit(new Runnable()
            {

                public void run()
                {
                    for (EventHandler handler : genericHandlers)
                    {
                        handler.onEvent(event);
                    }

                    List<EventHandler> handlers = handlersByEventType
                            .get(event.getType());
                    if (null != handlers)
                    {
                        for (EventHandler handler : handlers)
                        {
                            handler.onEvent(event);
                        }
                    }

                }
            });

        }
        else
        {
            System.err.println("Discarding event: " + event
                    + " as dispatcher is shutting down");
        }

    }

    public void close()
    {
        synchronized (this)
        {
            isShuttingDown = true;
            genericHandlers.clear();
            handlersByEventType.clear();
        }

    }

}
