package com.junglee.task.event.impl;

import com.junglee.task.event.Events;
import com.junglee.task.event.Event;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public class DefaultNetworkEvent extends DefaultEvent
{
    private static final long serialVersionUID = 6486454029499527617L;

    /**
     * {@link Events#NETWORK_MESSAGE}.
     */
    public DefaultNetworkEvent()
    {
        super.setType(Events.NETWORK_MESSAGE);
    }


    public DefaultNetworkEvent(Event event)
    {
        this.setSource(event.getSource());
        this.setEventContext(event.getEventContext());
        this.setTimeStamp(event.getTimeStamp());
        super.setType(Events.NETWORK_MESSAGE);
    }


    @Override
    public void setType(int type)
    {
        throw new IllegalArgumentException(
                "Event type of this class is already set to NETWORK_MESSAGE. "
                        + "It should not be reset.");
    }
}