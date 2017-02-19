package com.junglee.task.server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.Random;

/**
 * Created by vishwasourabh.sahay on 12/02/17.
 */

public class GameServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Random random = new Random();

    private static final String[] quotes = {
            "Where there is love there is life.",
            "First they ignore you, then they laugh at you, then they fight you, then you win.",
            "Be the change you want to see in the world.",
            "The weak can never forgive. Forgiveness is the attribute of the strong.",
    };

    private static String nextQuote() {
        int quoteId;
        synchronized (random) {
            quoteId = random.nextInt(quotes.length);
        }
        return quotes[quoteId];
    }


    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.err.println(datagramPacket.content().toString(CharsetUtil.UTF_8));
        if ("Quote".equals(datagramPacket.content().toString(CharsetUtil.UTF_8))) {
            channelHandlerContext.write(new DatagramPacket(Unpooled.copiedBuffer("Quote" + nextQuote(), CharsetUtil.UTF_8), datagramPacket.sender()));

        }
    }
}