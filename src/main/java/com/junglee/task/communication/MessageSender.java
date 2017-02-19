package com.junglee.task.communication;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface MessageSender
{
    public Object sendMessage(Object message);

    public void close();


}