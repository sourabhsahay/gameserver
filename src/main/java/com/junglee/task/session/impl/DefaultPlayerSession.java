package com.junglee.task.session.impl;

import com.junglee.task.entity.GameChannel;
import com.junglee.task.entity.Player;
import com.junglee.task.event.Event;
import com.junglee.task.event.EventDispatcher;
import com.junglee.task.event.impl.ExecutorEventDispatcher;
import com.junglee.task.session.PlayerSession;
import com.junglee.task.session.id.UniqueIDService;

import java.util.Calendar;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public class DefaultPlayerSession extends DefaultSession implements
        PlayerSession
{

    /**
     * Each session belongs to a Player. This variable holds the reference.
     */
    final protected Player player;

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public long getSessionEndTime() {
        return sessionEndTime;
    }

    /**
     * Each incoming connection is made to a game room. This reference holds the
     * association to the game room.
     */
    protected GameChannel parentGameChannel;

    public void setSessionStartTime(long sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public void setSessionEndTime(long sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    /**
     * This variable holds information about the type of binary communication
     * protocol to be used with this session.
     */



    private long sessionStartTime;

    private long sessionEndTime;

    protected DefaultPlayerSession(PlayerSessionBuilder playerSessionBuilder)
    {
        super(playerSessionBuilder);
        this.player = playerSessionBuilder.player;
        this.parentGameChannel = playerSessionBuilder.parentGameChannel;
        this.sessionStartTime = Calendar.getInstance().getTimeInMillis();
    }

    public static class PlayerSessionBuilder extends SessionBuilder
    {
        protected Player player = null;
        protected GameChannel parentGameChannel;

        public PlayerSessionBuilder(EventDispatcher eventDispatcher, UniqueIDService uniqueIDService) {
            super(eventDispatcher, uniqueIDService);
        }

        public PlayerSession build()
        {
            return new DefaultPlayerSession(this);
        }

        public PlayerSessionBuilder player(Player player)
        {
            this.player = player;
            return this;
        }

        public PlayerSessionBuilder parentGameRoom(GameChannel parentGameChannel)
        {
            if (null == parentGameChannel)
            {
                throw new IllegalArgumentException(
                        "GameChannel instance is null, session will not be constructed");
            }
            this.parentGameChannel = parentGameChannel;
            return this;
        }

        @Override
        protected void validateAndSetValues()
        {
            if (null == eventDispatcher)
            {
                eventDispatcher = new ExecutorEventDispatcher();
            }
            super.validateAndSetValues();
        }

    }

    public Player getPlayer()
    {
        return player;
    }

    public GameChannel getGameChannel()
    {
        return parentGameChannel;
    }

    public void setGameRoom(GameChannel gameChannel)
    {
        this.parentGameChannel = gameChannel;
    }


    @Override
    public synchronized void close()
    {
        if (!isShuttingDown)
        {
            super.close();
            parentGameChannel.disconnectSession(this);
        }
    }

    public void sendToGameChannel(Event event) {
        parentGameChannel.send(event);
    }

    @Override
    public String toString()
    {
        return "PlayerSession [id=" + id + "player=" + player
                + ", parentGameChannel=" + parentGameChannel + ", protocol="
                + ", isShuttingDown=" + isShuttingDown + "]";
    }
}