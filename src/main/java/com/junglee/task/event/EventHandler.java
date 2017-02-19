package com.junglee.task.event;

/**
 * Created by vishwasourabh.sahay on 16/02/17.
 */
public interface EventHandler
{
    /**
     * On event
     *
     * @param event
     */
    public void onEvent(Event event);

    public int getEventType();

}

