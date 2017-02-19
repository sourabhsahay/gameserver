package com.junglee.task.communication.impl;

import com.junglee.task.communication.MessageSender;
import com.junglee.task.event.Event;
import com.junglee.task.event.Events;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public class TCPMessageSender implements MessageSender {
        private final Channel channel;
        private static final Logger LOG = LoggerFactory
                .getLogger(TCPMessageSender.class);

        public TCPMessageSender(Channel channel)
        {
            super();
            this.channel = channel;
        }

        public Object sendMessage(Object message)
        {
            return channel.write(message);
        }


        public Channel getChannel()
        {
            return channel;
        }

        public void close()
        {
            LOG.debug("Going to close tcp connection in class: {}", this
                    .getClass().getName());
            Event event = Events.event(null, Events.DISCONNECT);
            if (channel.isOpen())
            {
                channel.write(event).addListener(ChannelFutureListener.CLOSE);
            }
            else
            {
                channel.close();
                LOG.trace("Unable to write the Event {} with type {} to socket",
                        event, event.getType());
            }
        }

        @Override
        public String toString()
        {
            String channelId = "TCP channel with Id: ";
            if (null != channel)
            {
                channelId += channel.id().toString();
            }
            else
            {
                channelId += "0";
            }
            String sender = "Netty " + channelId;
            return sender;
        }
    }

