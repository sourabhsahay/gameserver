package com.junglee.task.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by vishwasourabh.sahay on 16/02/17.
 */
@Component
public class LoginChannelInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private TCPUpStreamHandler TCPUpStreamHandler;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("HttpServerCodec",new HttpServerCodec());
        pipeline.addLast("HttpObjectAggregator",new HttpObjectAggregator(65536));
        pipeline.addLast("ChunkedWriteHandler",new ChunkedWriteHandler());
        pipeline.addLast("WebSocketServerProtocolHandler",new WebSocketServerProtocolHandler("/login"));
        pipeline.addLast("TCPUpStreamHandler", TCPUpStreamHandler);


    }
}