package com.junglee.task.server.impl;

import com.junglee.task.server.Server;
import com.junglee.task.server.handler.LoginChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */

@Component
public class TCPServer extends AbstractServer
{
    private static final Logger LOG = LoggerFactory.getLogger(TCPServer.class);
    private final EventLoopGroup group	= new NioEventLoopGroup();


    @Autowired
    LoginChannelInitializer loginChannelInitializer;

    private String[] args;
    private ServerBootstrap serverBootstrap;



    public TCPServer()
    {

    }

    public void startServer(int port) throws Exception
    {
        portNumber = port;
        startServer(args);
    }

    public void startServer() throws Exception
    {
        startServer(args);
    }

    public void startServer(String[] args) throws Exception
    {
        int portNumber = getPortNumber(args);
        InetSocketAddress socketAddress = new InetSocketAddress(portNumber);
        startServer(socketAddress);
    }

    public ServerBootstrap createServerBootstrap()
    {
        // TODO The thread pools should be injected from spring.
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(group).channel(NioServerSocketChannel.class).childHandler(new LoginChannelInitializer());
        ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(portNumber));
        future.syncUninterruptibly();
        return serverBootstrap;
    }

    public Server.TRANSMISSION_PROTOCOL getTransmissionProtocol()
    {
        return Server.TRANSMISSION_PROTOCOL.TCP;
    }

    public void startServer(InetSocketAddress socketAddress) throws Exception
    {
        this.socketAddress = socketAddress;
        createServerBootstrap();
        try
        {
            ((ServerBootstrap) serverBootstrap).bind(socketAddress);
        }
        catch (ChannelException e)
        {
            LOG.error("Unable to start TCP server due to error {}",e);
            throw e;
        }
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
        super.stopServer();
    }

    public String[] getArgs()
    {
        return args;
    }

    public void setArgs(String[] args)
    {
        this.args = args;
    }

    @Override
    public String toString()
    {
        return "TCPServer [args=" + Arrays.toString(args)
                + ", socketAddress=" + socketAddress + ", portNumber=" + portNumber
                + "]";
    }

}

