package com.junglee.task.entity.impl;

import com.junglee.task.entity.Game;
import com.junglee.task.entity.GameChannel;
import com.junglee.task.event.EventDispatcher;
import com.junglee.task.event.EventHandler;
import com.junglee.task.session.PlayerSession;
import com.junglee.task.session.SessionFactory;
import com.junglee.task.session.id.UniqueIDService;
import com.junglee.task.session.impl.DefaultSession;
import com.junglee.task.entity.GameStateManagerService;
import com.junglee.task.entity.Player;
import com.junglee.task.event.Event;
import com.junglee.task.event.impl.DefaultNetworkEvent;
import com.junglee.task.event.impl.NetworkEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public abstract class GameChannelSession extends DefaultSession implements GameChannel
{
    private static final Logger LOG = LoggerFactory.getLogger(GameChannelSession.class);

    protected String gameRoomName;
    /**
     */
    protected Game parentGame;

    protected GameStateManagerService stateManager;

    protected Set<PlayerSession> sessions;

    SessionFactory sessionFactory;

    EventDispatcher eventDispatcher;


    protected GameChannelSession(GameChannelSessionBuilder gameRoomSessionBuilder, EventDispatcher eventDispatcher,
                                 SessionFactory sessionFactory)
    {
        super(gameRoomSessionBuilder);
        this.sessions = gameRoomSessionBuilder.sessions;
        this.parentGame = gameRoomSessionBuilder.parentGame;
        this.gameRoomName = gameRoomSessionBuilder.gameRoomName;
        this.eventDispatcher = eventDispatcher;
        this.sessionFactory = sessionFactory;
    }

    public static class GameChannelSessionBuilder extends SessionBuilder
    {
        protected Set<PlayerSession> sessions;
        protected Game parentGame;
        protected String gameRoomName;

        public GameChannelSessionBuilder(EventDispatcher eventDispatcher, UniqueIDService uniqueIDService) {
            super(eventDispatcher, uniqueIDService);
        }

        @Override
        protected void validateAndSetValues()
        {
            if (null == id)
            {
                id = String.valueOf(ID_GENERATOR_SERVICE.generateFor(GameChannelSession.class));
            }
            if(null == sessionAttributes)
            {
                sessionAttributes = new HashMap<String, Object>();
            }
            if (null == sessions)
            {
                sessions = new HashSet<PlayerSession>();
            }
            creationTime = System.currentTimeMillis();
        }

        public GameChannelSessionBuilder sessions(Set<PlayerSession> sessions)
        {
            this.sessions = sessions;
            return this;
        }

        public GameChannelSessionBuilder parentGame(Game parentGame)
        {
            this.parentGame = parentGame;
            return this;
        }

        public GameChannelSessionBuilder gameRoomName(String gameRoomName)
        {
            this.gameRoomName = gameRoomName;
            return this;
        }


    }

    public PlayerSession createPlayerSession(Player player)
    {
        PlayerSession playerSession = getSessionInstance(player);
        return playerSession;
    }

    public abstract void onLogin(PlayerSession playerSession);


    public void afterSessionConnect(PlayerSession playerSession)
    {

    }

    public synchronized boolean disconnectSession(PlayerSession playerSession)
    {
        final boolean removeHandlers = this.eventDispatcher.removeHandlersForSession(playerSession);
        playerSession.setSessionEndTime(Calendar.getInstance().getTimeInMillis());
        //playerSession.getEventDispatcher().clear(); // remove network handlers of the session.
        return (removeHandlers && sessions.remove(playerSession));
    }

    public void send(Event event) {
        onEvent(event);
    }

    public void sendBroadcast(DefaultNetworkEvent networkEvent)
    {
        onEvent(networkEvent);

    }

    @Override
    public synchronized void close()
    {
        isShuttingDown = true;
        for(PlayerSession session: sessions)
        {
            session.close();
        }
    }

    public PlayerSession getSessionInstance(Player player)
    {
        PlayerSession playerSession = sessionFactory.createPlayerSession(this,player);
        return playerSession;
    }

    public Set<PlayerSession> getSessions()
    {
        return sessions;
    }

    public void setSessions(Set<PlayerSession> sessions)
    {
        this.sessions = sessions;
    }

    public String getGameChannelName()
    {
        return gameRoomName;
    }

    public void setGameChannelName(String gameRoomName)
    {
        this.gameRoomName = gameRoomName;
    }

    public Game getParentGame()
    {
        return parentGame;
    }

    public void setParentGame(Game parentGame)
    {
        this.parentGame = parentGame;
    }


    @Override
    public boolean isShuttingDown()
    {
        return isShuttingDown;
    }

    public void setShuttingDown(boolean isShuttingDown)
    {
        this.isShuttingDown = isShuttingDown;
    }

    /**
     * Method which will create and add event handlers of the player session to
     * the Game Room's EventDispatcher.
     *
     * @param playerSession
     *            The session for which the event handlers are created.
     */
    protected void createAndAddEventHandlers(PlayerSession playerSession)
    {
        // Create a network event listener for the player session.
        EventHandler networkEventHandler = new NetworkEventHandler(playerSession);
        // Add the handler to the game room's EventDispatcher so that it will
        // pass game room network events to player session session.
        this.eventDispatcher.addHandler(networkEventHandler);
        LOG.trace("Added Network handler to "
                        + "EventDispatcher of GameChannel {}, for session: {}", this,
                playerSession);
    }
}
