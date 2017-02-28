package com.junglee.task.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junglee.task.communication.MessageSender;
import com.junglee.task.communication.impl.UDPMessageSender;
import com.junglee.task.event.Event;
import com.junglee.task.event.Events;
import com.junglee.task.event.impl.DefaultEvent;
import com.junglee.task.session.Session;
import com.junglee.task.session.SessionManagerService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */

@Component
public class UDPUpstreamHandler extends SimpleChannelInboundHandler<String>
{
    private static final Logger LOG = LoggerFactory.getLogger(UDPUpstreamHandler.class);

    @Autowired
    private SessionManagerService<SocketAddress> udpSessionManager;

    @Autowired
    private ObjectMapper objectMapper;
    public UDPUpstreamHandler()
    {
        super();
    }




    public SessionManagerService<SocketAddress> getUdpSessionManager()
    {
        return udpSessionManager;
    }

    public void setUdpSessionManager(
            SessionManagerService<SocketAddress> udpSessionManager)
    {
        this.udpSessionManager = udpSessionManager;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
       SocketAddress remoteAddress = channelHandlerContext.channel().remoteAddress();
        Session session = udpSessionManager.getSession(remoteAddress);

        if(null != session)
        {
            //TODO improve event deserialization
            DefaultEvent event = objectMapper.readValue(message,DefaultEvent.class);

            if (!session.isUDPEnabled())
            {
                Event event1 = getUDPConnectEvent(event, remoteAddress,
                        (DatagramChannel) channelHandlerContext.channel());
                session.onEvent(event1);
            }
            session.onEvent(event);
        }
    }

    public Event getUDPConnectEvent(Event event, SocketAddress remoteAddress,
                                    DatagramChannel udpChannel)
    {
        LOG.debug("Incoming udp connection remote address : {}",
                remoteAddress);

        if (event.getType() != Events.CONNECT)
        {
            LOG.warn("Going to discard UDP Message Event with type {} "
                            + "It will get converted to a CONNECT event since "
                            + "the UDP MessageSender is not initialized till now",
                    event.getType());
        }
        MessageSender messageSender = new UDPMessageSender(udpChannel, udpSessionManager);
        Event connectEvent = Events.connectEvent(messageSender, true);

        return connectEvent;
    }


}

