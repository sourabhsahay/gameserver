package com.junglee.task.entity;

import com.junglee.task.event.Event;
import com.junglee.task.event.impl.DefaultNetworkEvent;
import com.junglee.task.session.PlayerSession;

import java.util.Set;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface GameChannel
{

    public PlayerSession createPlayerSession(Player player);

    public void onLogin(PlayerSession playerSession);

    public void afterSessionConnect(PlayerSession playerSession);

    public abstract boolean disconnectSession(PlayerSession session);

    public abstract Set<PlayerSession> getSessions();

    public abstract String getGameChannelName();

    public abstract void setGameChannelName(String gameRoomName);

    public abstract Game getParentGame();

    public abstract void setParentGame(Game parentGame);

    public abstract void setSessions(Set<PlayerSession> sessions);

    public abstract void send(Event event);

    public abstract void sendBroadcast(DefaultNetworkEvent networkEvent);

    public abstract void close();

}
