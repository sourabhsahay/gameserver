package com.junglee.task.event.impl;

import com.junglee.task.communication.MessageSender;
import com.junglee.task.event.Events;
import com.junglee.task.event.SessionEventHandler;
import com.junglee.task.session.PlayerSession;
import com.junglee.task.session.Session;
import com.junglee.task.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public class DefaultSessionEventHandler implements SessionEventHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionEventHandler.class);

    private final Session session;

    public DefaultSessionEventHandler(Session session)
    {
        this.session = session;
    }

    public int getEventType()
    {
        return Events.ANY;
    }

    public void onEvent(Event event)
    {
        doEventHandlerMethodLookup(event);
    }

    protected void doEventHandlerMethodLookup(Event event)
    {
        switch (event.getType())
        {
            case Events.SESSION_MESSAGE:
                onDataIn(event);
                break;
            case Events.CHAT_IN:
                onDataIn(event);
                break;
            case Events.NETWORK_MESSAGE:
                onNetworkMessage((DefaultNetworkEvent)event);
                break;
            case Events.GAME_CHANNEL_JOIN:
                onNetworkMessage((DefaultNetworkEvent)event);
                break;
            case Events.LOG_IN_SUCCESS:
                onLoginSuccess(event);
                break;
            case Events.LOG_IN_FAILURE:
                onLoginFailure(event);
                break;
            case Events.START:
                onStart(event);
                break;
            case Events.STOP:
                onStart(event);
                break;
            case Events.EXCEPTION:
                onException(event);
                break;
            case Events.LOG_OUT:
                onLogout(event);
                break;
            default:
                onCustomEvent(event);
                break;
        }
    }

    protected void onDataIn(Event event)
    {
        if (null != getSession())
        {
            PlayerSession pSession = (PlayerSession) getSession();
            DefaultNetworkEvent networkEvent = new DefaultNetworkEvent(event);
            pSession.getGameChannel().sendBroadcast(networkEvent);
        }
    }




    protected void onNetworkMessage(DefaultNetworkEvent event)
    {
        Session session = getSession();
        if (!session.isWriteable())
            return;
            MessageSender udpSender = session.getUdpSender();
            if (null != udpSender)
            {
                udpSender.sendMessage(event);
            }
            else
            {
                LOG.trace(
                        "Going to discard event: {} since udpSender is null in session: {}",
                        event, session);
            }
    }


    protected void onLoginSuccess(Event event)
    {
        getSession().getTcpSender().sendMessage(event);
    }

    protected void onLoginFailure(Event event)
    {
        getSession().getTcpSender().sendMessage(event);
    }



    protected void onStart(Event event)
    {
        getSession().getTcpSender().sendMessage(event);
    }

    protected void onStop(Event event)
    {
        getSession().getTcpSender().sendMessage(event);
    }


    protected void onDisconnect(Event event)
    {
        LOG.debug("Received disconnect event in session. ");
        onException(event);
    }


    @SuppressWarnings("unchecked")
    protected void onException(Event event)
    {

            LOG.debug("Received exception/disconnect event in session. "
                    + "Going to close session");
            onClose(event);
    }

    protected void onLogout(Event event)
    {
        onClose(event);
    }

    protected void onClose(Event event)
    {
        getSession().close();
    }

    protected void onCustomEvent(Event event)
    {

    }

    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        throw new UnsupportedOperationException("Session is a final variable and cannot be reset.");
    }


}
