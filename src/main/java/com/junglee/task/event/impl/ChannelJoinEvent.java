package com.junglee.task.event.impl;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
public class ChannelJoinEvent extends DefaultNetworkEvent {

    public ChannelJoinEvent(int type)
    {
        super.setType(type);
    }
}
