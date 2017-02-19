package com.junglee.task.event.impl;

import com.junglee.task.event.Events;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
public class ChatEvent extends  DefaultNetworkEvent {
    public ChatEvent()
    {
        setType(Events.CHAT_OUT);
    }
}
