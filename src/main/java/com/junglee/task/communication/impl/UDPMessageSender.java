package com.junglee.task.communication.impl;

import com.junglee.task.communication.MessageSender;
import com.junglee.task.session.Session;
import com.junglee.task.session.SessionManagerService;
import io.netty.channel.socket.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public class UDPMessageSender implements MessageSender
{
    private static final Logger LOG = LoggerFactory
            .getLogger(UDPMessageSender.class);
    private final SocketAddress remoteAddress;
    private final DatagramChannel channel;
    private final SessionManagerService<SocketAddress> sessionManagerService;


    public UDPMessageSender(DatagramChannel channel,
                                 SessionManagerService<SocketAddress> sessionManagerService)
    {
        this.remoteAddress = channel.remoteAddress();
        this.channel = channel;
        this.sessionManagerService = sessionManagerService;
    }

    public Object sendMessage(Object message)
    {
        return channel.write(message);
    }

    public void close()
    {
        Session session = sessionManagerService.getSession(remoteAddress);
        if (sessionManagerService.removeSession(remoteAddress))
        {
            LOG.debug("Successfully removed session: {}", session);
        }
        else
        {
            LOG.trace("No  session found for address: {}", remoteAddress);
        }

    }

    public SocketAddress getRemoteAddress()
    {
        return remoteAddress;
    }

    public DatagramChannel getChannel()
    {
        return channel;
    }

    @Override
    public String toString()
    {
        String channelId = "UDP Channel with id: ";
        if (null != channel)
        {
            channelId += channel.id();
        }
        else
        {
            channelId += "0";
        }
        String sender = "Netty " + channelId + " RemoteAddress: "
                + remoteAddress;
        return sender;
    }

    protected SessionManagerService<SocketAddress> getSessionManagerService()
    {
        return sessionManagerService;
    }
}
