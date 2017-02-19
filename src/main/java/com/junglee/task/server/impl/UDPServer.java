package com.junglee.task.server.impl;

import com.junglee.task.server.handler.UDPUpstreamHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
@Component
public class UDPServer extends AbstractServer
{
    private static final Logger LOG = LoggerFactory.getLogger(UDPServer.class);
    private String[] args;


    @Autowired
    private UDPUpstreamHandler upstreamHandler;
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    /**
     * The connected channel for this server. This reference can be used to
     * shutdown this server.
     */
    private Channel channel;

    public UDPServer()
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

    public void startServer(String[] args)  throws Exception
    {
        int portNumber = getPortNumber(args);
        InetSocketAddress socketAddress = new InetSocketAddress(portNumber);
        startServer(socketAddress);
    }

    private Bootstrap createServerBootstrap() throws Exception
    {
        EventLoopGroup udpGroup = new NioEventLoopGroup();
        try {
            serverBootstrap = new Bootstrap();
            serverBootstrap.group(udpGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            pipeline.addLast(DECODER);
                            pipeline.addLast(ENCODER);

                            pipeline.addLast(upstreamHandler);
                        }
                    });
            serverBootstrap.bind(portNumber).sync().channel().closeFuture().sync();
        } finally {
            udpGroup.shutdownGracefully();
        }
        return serverBootstrap;
    }

    @Override
    public void stopServer() throws Exception
    {
        if(null != channel)
        {
            channel.close();
        }
        super.stopServer();
    }


    public TRANSMISSION_PROTOCOL getTransmissionProtocol()
    {
        return TRANSMISSION_PROTOCOL.UDP;
    }

    public void startServer(InetSocketAddress socketAddress) throws Exception
    {
        this.socketAddress = socketAddress;
        createServerBootstrap();

        try
        {
            channel =  serverBootstrap
                    .bind(socketAddress).channel();
        }
        catch (ChannelException e)
        {
            LOG.error("Unable to start UDP server due to error {}",e);
            throw e;
        }

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
        return "UDPServer [args=" + Arrays.toString(args)
                + ", socketAddress=" + socketAddress + ", portNumber=" + portNumber
                + "]";
    }

}
