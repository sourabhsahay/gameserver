package com.junglee.task.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junglee.task.entity.GameChannel;
import com.junglee.task.session.id.UniqueIDService;
import com.junglee.task.communication.impl.TCPMessageSender;
import com.junglee.task.entity.Player;
import com.junglee.task.event.Event;
import com.junglee.task.event.EventDispatcher;
import com.junglee.task.event.Events;
import com.junglee.task.event.impl.DefaultEvent;
import com.junglee.task.service.DBLookupService;
import com.junglee.task.session.PlayerSession;
import com.junglee.task.session.SessionManagerService;
import com.junglee.task.util.PlayerCredentials;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;

/**
 * Created by vishwasourabh.sahay on 16/02/17.
 */
@Component
public class TCPUpStreamHandler extends SimpleChannelInboundHandler
{

    private static final Logger LOG = LoggerFactory
            .getLogger(TCPUpStreamHandler.class);

    private DBLookupService DBLookupService;
    protected UniqueIDService uniqueIDService;
    private ObjectMapper objectMapper;
    private EventDispatcher eventDispatcher;
    private SessionManagerService<SocketAddress> sessionManagerService;


    @Autowired
    TCPUpStreamHandler(DBLookupService DBLookupService, UniqueIDService uniqueIDService, ObjectMapper mapper,
                       EventDispatcher eventDispatcher, SessionManagerService sessionManagerService)
    {
        this.DBLookupService = DBLookupService;
        this.objectMapper = objectMapper;
        this.uniqueIDService = uniqueIDService;
        this.eventDispatcher = eventDispatcher;
        this.sessionManagerService = sessionManagerService;


    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {

        Channel channel = channelHandlerContext.channel();
        if (object instanceof TextWebSocketFrame){

            TextWebSocketFrame frame = (TextWebSocketFrame) object;
            String message = frame.text();
            DefaultEvent event = objectMapper.readValue(message,DefaultEvent.class);

            if (Events.LOG_IN == event.getType())
            {
                LOG.trace("Login attempt from {}", channel.remoteAddress());
                PlayerCredentials playerCredentials = (PlayerCredentials)event.getSource();

                Player player = lookupPlayer(playerCredentials);
                loginPlayer(player, channel);
                joinGameRoom(player, channel, playerCredentials.getPassword());
            }
            else if (Events.CHAT_IN ==event.getType())
            {
                //Chat should be consistent, hence TCP protocol should be used for chats, not UDP.
                sessionManagerService.getSession(channelHandlerContext.channel().remoteAddress()).onEvent(event);
            }
            else
            {
                LOG.error(
                        "Invalid event {} sent from remote address {}. "
                                + "Going to close channel {}",
                        new Object[] { event.getType(),
                                channel.remoteAddress(), channel.id() });
                closeChannelWithLoginFailure(channel);
            }


        }
        else
        {
            closeChannelWithLoginFailure(channel);
        }


    }

    protected TextWebSocketFrame eventToFrame(int opcode, Object payload) throws Exception
    {
        Event event = Events.event(payload, opcode);
        return new TextWebSocketFrame(objectMapper.writeValueAsString(event));
    }

    public Player lookupPlayer(PlayerCredentials credentials)
    {
        Player player = DBLookupService.playerLookup(credentials);
        if (null == player)
        {
            LOG.error("Invalid credentials provided by user: {}", credentials);
        }
        return player;
    }

    public void loginPlayer(Player player, Channel channel) throws Exception
    {
        if (null != player)
        {
            channel.write(eventToFrame(Events.LOG_IN_SUCCESS, null));
        }
        else
        {
            closeChannelWithLoginFailure(channel);
        }
    }

    public void joinGameRoom(Player player, Channel channel, String refKey) throws Exception
    {
        GameChannel gameChannel = DBLookupService.gameChannelLookup(refKey);
        if (null != gameChannel)
        {
            PlayerSession playerSession = gameChannel.createPlayerSession(player);
            gameChannel.onLogin(playerSession);
            String reconnectKey = (String)uniqueIDService
                    .generateFor(playerSession.getClass());
            LOG.trace("Sending GAME_CHANNEL_JOIN_SUCCESS to channel {}",
                    channel.id());
            //Make use of reconnect-key for subsequent login.
            ChannelFuture future = channel.write(eventToFrame(
                    Events.GAME_CHANNEL_JOIN_SUCCESS, reconnectKey));
            connectToGameRoom(gameChannel, playerSession, future);
        }
        else
        {
            // Write failure and close channel.
            ChannelFuture future = channel.write(eventToFrame(
                    Events.GAME_CHANNEL_JOIN_FAILURE, null));
            future.addListener(ChannelFutureListener.CLOSE);
            LOG.error(
                    "Invalid ref key provided by client: {}. Channel {} will be closed",
                    refKey, channel.id());
        }
    }

    public void connectToGameRoom(final GameChannel gameChannel,
                                  final PlayerSession playerSession, ChannelFuture future)
    {
        future.addListener(new ChannelFutureListener()
        {
            public void operationComplete(ChannelFuture future)
                    throws Exception
            {
                Channel channel = future.channel();
                LOG.trace(
                        "Sending GAME_CHANNEL_JOIN_SUCCESS to channel {} completed",
                        channel.id());
                if (future.isSuccess())
                {
                   TCPMessageSender sender = new TCPMessageSender(
                            channel);
                    playerSession.setTcpSender(sender);
                    // Send the connect event so that it will in turn send the
                    // START event.
                    playerSession.onEvent(Events.connectEvent(sender,false));
                }
                else
                {
                    LOG.error("Sending GAME_CHANNEL_JOIN_SUCCESS message to client was failure, channel will be closed");
                    channel.close();
                }
            }
        });
    }


    protected void closeChannelWithLoginFailure(Channel channel) throws Exception
    {
        channel.write(eventToFrame(Events.LOG_IN_FAILURE, null)).addListener(
                ChannelFutureListener.CLOSE);
    }



}
