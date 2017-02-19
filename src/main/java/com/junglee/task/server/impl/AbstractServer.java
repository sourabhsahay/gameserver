package com.junglee.task.server.impl;

import com.junglee.task.server.Server;
import com.junglee.task.session.Session;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public abstract class AbstractServer implements Server
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractServer.class);
    public static final ChannelGroup ALL_CHANNELS = new DefaultChannelGroup(new DefaultEventExecutor());
    protected Session session;
    protected InetSocketAddress socketAddress;
    protected int portNumber = 2000;
    protected Bootstrap serverBootstrap;

    public AbstractServer()
    {
        super();
    }

    public void stopServer() throws Exception
    {
        LOG.debug("In stopServer method of class: {}",
                this.getClass().getName());
        ChannelGroupFuture future = ALL_CHANNELS.close();
        try {
            future.await();
        } catch (InterruptedException e) {
            LOG.error("Execption occurred while waiting for channels to close: {}",e);
        }
    }


    public int getPortNumber(String[] args)
    {
        if (null == args || args.length < 1)
        {
            return portNumber;
        }

        try
        {
            return Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e)
        {
            LOG.error("Exception occurred while "
                    + "trying to parse the port number: {}", args[0]);
            LOG.error("NumberFormatException: {}",e);
            throw e;
        }
    }

    public Bootstrap getServerBootstrap()
    {
        return serverBootstrap;
    }


    public int getPortNumber()
    {
        return portNumber;
    }

    public void setPortNumber(int portNumber)
    {
        this.portNumber = portNumber;
    }



    public InetSocketAddress getSocketAddress()
    {
        return socketAddress;
    }

    public void setInetAddress(InetSocketAddress inetAddress)
    {
        this.socketAddress = inetAddress;
    }

    @Override
    public String toString()
    {
        return "NettyServer [socketAddress=" + socketAddress + ", portNumber="
                + portNumber + "]";
    }

    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

}
