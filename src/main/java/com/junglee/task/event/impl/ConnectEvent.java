package com.junglee.task.event.impl;

import com.junglee.task.communication.MessageSender;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public class ConnectEvent extends DefaultEvent
{
    private static final long serialVersionUID = 1L;

    protected MessageSender tcpSender;
    protected MessageSender udpSender;

    public ConnectEvent(MessageSender tcpSender, MessageSender udpSender) {
        this.tcpSender = tcpSender;
        this.udpSender = udpSender;
    }

    @Override
    public MessageSender getSource()
    {
        return tcpSender;
    }

    @Override
    public void setSource(Object source)
    {
        this.tcpSender = (MessageSender) source;
    }

    public MessageSender getTcpSender()
    {
        return tcpSender;
    }

    public void setTcpSender(MessageSender tcpSender)
    {
        this.tcpSender = tcpSender;
    }

    public MessageSender getUdpSender()
    {
        return udpSender;
    }

    public void setUdpSender(MessageSender udpSender)
    {
        this.udpSender = udpSender;
    }


}