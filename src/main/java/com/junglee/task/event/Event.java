package com.junglee.task.event;


/**
 * Created by vishwasourabh.sahay on 16/02/17.
 */
public interface Event
{
    int getType();

    void setType(int type);

    Object getSource();

    void setSource(Object source);

    EventContext getEventContext();

    void setEventContext(EventContext context);

    long getTimeStamp();

    void setTimeStamp(long timeStamp);
}