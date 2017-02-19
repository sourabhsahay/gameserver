package com.junglee.task.event;

import com.junglee.task.communication.MessageSender;
import com.junglee.task.event.impl.ConnectEvent;
import com.junglee.task.event.impl.DefaultEvent;
import com.junglee.task.event.impl.DefaultEventContext;
import com.junglee.task.session.Session;

/**
 * Created by vishwasourabh.sahay on 16/02/17.
 */
public class Events
{
    public static final int PROTCOL_VERSION=1;
    /**
     * Events should <b>NEVER</b> have this type. But event handlers can choose
     * to have this type to signify that they will handle any type of incoming
     * event. For e.g. {@link DefaultSessionEventHandler}
     */
    public final static int ANY = 1;

    // Lifecycle events.
    //
    public final static int CONNECT = 2;
    //Reconnect to be used if a player connects after disconnect with valid ChannelId
    public static final int RECONNECT = 3;
    public final static int CONNECT_FAILED = 4;

    public static final int DISCONNECT = 5;

    public static final int LOG_IN = 6;
    public static final int LOG_OUT = 7;
    public static final int LOG_IN_SUCCESS = 8;
    public static final int LOG_IN_FAILURE = 9;
    public static final int LOG_OUT_SUCCESS = 10;
    public static final int LOG_OUT_FAILURE = 11;

    // Game Metadata events
    public static final int GAME_LIST = 12;
    public static final int ROOM_LIST = 13;
    public static final byte GAME_CHANNEL_JOIN = 14;
    public static final byte GAME_CHANNEL_LEAVE = 15;
    public static final byte GAME_CHANNEL_JOIN_SUCCESS = 16;
    public static final byte GAME_CHANNEL_JOIN_FAILURE = 17;

    public static final byte START = 18;

    public static final byte CHANGE_ATTRIBUTE = 20;


    /**
     * Event sent from server to client to stop messages from being sent to server.
     */
    public static final byte STOP = 21;
    /**
     * Incoming data from another machine/JVM to this JVM (server or client)
     */
    public static final byte SESSION_MESSAGE = 22;

    /**
     * This event is used to send data from the current machine to remote
     * machines using TCP or UDP transports. It is an out-going event.
     */
    public static final byte NETWORK_MESSAGE = 23;

    /*
     *Chat from one player to the whole table
     */
    public static final byte CHAT_IN = 24;

    /*
    *Chat from server or broadcasted chat from incoming player
    */
    public static final byte CHAT_OUT = 25;

    /*
     * chat from Admin to the whole table
     */

    public static final byte EXCEPTION = 40;

    public static Event event(Object source, int eventType)
    {
        return event(source,eventType,(Session)null);
    }

    public static Event event(Object source, int eventType,Session session)
    {
        EventContext context = null;
        if(null != session)
        {
            context = new DefaultEventContext();
        }
        return event(source,eventType,context);
    }

    public static Event event(Object source, int eventType, EventContext context)
    {
        DefaultEvent event = new DefaultEvent();
        event.setSource(source);
        event.setType(eventType);
        event.setEventContext(context);
        event.setTimeStamp(System.currentTimeMillis());
        return event;
    }

    public static Event connectEvent(MessageSender messageSender, boolean isUDPEnabled){
        Event event = null;
        if(isUDPEnabled)
        {
            event = new ConnectEvent(null, messageSender);
        }
        else
        {
           event = new ConnectEvent(messageSender, null);
        }
        event.setTimeStamp(System.currentTimeMillis());
        return event;
    }
}
