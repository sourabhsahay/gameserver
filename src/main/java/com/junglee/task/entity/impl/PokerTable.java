package com.junglee.task.entity.impl;

import com.junglee.task.entity.GameChannel;
import com.junglee.task.entity.GameStateManagerService;
import com.junglee.task.entity.Player;
import com.junglee.task.event.Event;
import com.junglee.task.event.EventContext;
import com.junglee.task.event.EventDispatcher;
import com.junglee.task.event.Events;
import com.junglee.task.event.impl.*;
import com.junglee.task.session.PlayerSession;
import com.junglee.task.session.Session;
import com.junglee.task.session.SessionFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
public class PokerTable extends GameChannelSession {

    public static int quorum =3;
    public static int MAX_COUNT= 5;
    protected static AtomicInteger currentCount = new AtomicInteger(0);
    private static final String GAME_STARTING_MSG = "Game starting in 5s";
    protected static Timer timer = new Timer();


    public PokerTable(GameChannelSessionBuilder builder, EventDispatcher eventDispatcher, GameStateManagerService
                      gameStateManagerService, SessionFactory sessionFactory) {
        super(builder, eventDispatcher, sessionFactory);
        PokerState state = new PokerState(this.getGameChannelName());
        state.setCards(new HashSet<Card>());
        state.setPoker_state(PokerState.POKER_STATE.WAITING_FOR_PLAYERS);
        stateManager.getAndSetState(state);
    }


     public boolean canTakeMorePlayers()
    {
        return ! (currentCount.get()== MAX_COUNT || ((PokerState)stateManager.getState()).getPoker_state().equals(PokerState.POKER_STATE.IN_GAME));
    }

    @Override
    public synchronized boolean disconnectSession(PlayerSession playerSession) {
        broadCastSessionLeave(playerSession);
        // Return to available pool
        if(this.getSessions().isEmpty())
        {
            ((PokerState)this.stateManager.getState()).setPoker_state(PokerState.POKER_STATE.WAITING_FOR_PLAYERS);
            //TODO should stats contain the fact that these players played
            broadCastGameStopped(this, null);
        }
        return super.disconnectSession(playerSession);

    }

    private void broadCastSessionLeave(PlayerSession playerSession) {
        ChannelJoinEvent channelJoinEvent= new ChannelJoinEvent(Events.GAME_CHANNEL_LEAVE);
        channelJoinEvent.setSource(playerSession.getPlayer().getName());
        sendBroadcast(channelJoinEvent);

    }

    synchronized public void onLogin(final PlayerSession playerSession) {

        if (!canTakeMorePlayers())
        {
            playerSession.onEvent(new ChannelJoinEvent(Events.GAME_CHANNEL_JOIN_FAILURE));

        }
        playerSession.addHandler(new DefaultSessionEventHandler(playerSession) {
            @Override
            protected void onDataIn(Event event) {
                if (null != event.getSource()) {
                    // Pass the player session in the event context
                    DefaultEventContext context = new DefaultEventContext();
                    context.setAttachment(playerSession);
                    event.setEventContext(context);
                    // pass the event to the game room
                    playerSession.getGameChannel().send(event);
                }
            }

            //Instead change implementation of sendbroadcast to only channel-specific sessions in GameChannelSession

            @Override
            protected void onStart(Event event) {
                PlayerSession playerSession1 = (PlayerSession) getSession();
                if(isParentGame(event))
                {
                    super.onStart(event);
                }

            }

            private boolean isParentGame(Event event) {
                return playerSession.getGameChannel().getGameChannelName().equals(((GameChannel)event.getEventContext().getAttachment()).getGameChannelName());
            }
        });

        //broad cast to other players about joining of this player.
        broadCastPlayerJoin(playerSession);

        //check if the player count exceed 3.
        if (currentCount.incrementAndGet() > quorum)
        {
            broadCastGameStarting();
            //start the game after 5 seconds
            timer.schedule(new PokerStartTask(this), 5000);
        }


    }

    private void broadCastGameStarting() {
        DefaultNetworkEvent event = new DefaultNetworkEvent();
        //TODO externalize all the string constants
        event.setSource(GAME_STARTING_MSG);
        DefaultEventContext context = new DefaultEventContext();
        context.setAttachment(this.getId()); //Set the poker channel Id
        event.setEventContext(context);
        sendBroadcast(event);
    }

    private void broadCastPlayerJoin(PlayerSession playerSession) {
        ChannelJoinEvent channelJoinEvent = new ChannelJoinEvent(Events.GAME_CHANNEL_JOIN);
        channelJoinEvent.setSource(playerSession.getPlayer());
        DefaultEventContext context = new DefaultEventContext();
        context.setAttachment(this.getGameChannelName()); //Set the poker channel name
        channelJoinEvent.setEventContext(context);
        sendBroadcast(channelJoinEvent);
    }

    private Card createCard(final PlayerSession playerSession) {
        Card card = new Card();
        card.setCardOwner((String) playerSession.getPlayer().getName());
        return card;
    }

    private void broadCastGameStopped(GameChannelSession channel, List<Player> participants) {
        Event event = new DefaultEvent();
        event.setType(Events.STOP);
        event.setSource(participants);
        EventContext context = new DefaultEventContext();
        context.setAttachment(channel);
        event.setEventContext(context);
        channel.onEvent(event);
    }

    static class PokerStartTask extends TimerTask {

        PokerTable pokerTable;
        PokerStartTask (final PokerTable channel)
        {
            this.pokerTable = channel;
        }

        @Override
        public void run() {
            if(pokerTable.currentCount.get() >= quorum) {
                pokerTable.addHandler(new PokerSessionHandler(pokerTable));
                synchronized(this) {
                    ((PokerState) pokerTable.stateManager.getState()).setPoker_state(PokerState.POKER_STATE.IN_GAME);
                }

            }
        }
    }



    static class PokerEndTask extends TimerTask {

        PokerTable pokerTable;
        PokerEndTask (final PokerTable channel)
        {
            this.pokerTable = channel;
        }

        @Override
        public void run() {
            PokerState pokerState = (PokerState) pokerTable.stateManager.getState();
            broadCastToPlayers(pokerTable);
            List<Player> participants = new ArrayList<Player>();
            for(PlayerSession playerSession : pokerTable.getSessions())
            {
                participants.add(playerSession.getPlayer());
                pokerTable.disconnectSession(playerSession);
            }
            synchronized (this) {
                pokerState.setPoker_state(PokerState.POKER_STATE.WAITING_FOR_PLAYERS);
            }
            pokerTable.broadCastGameStopped(pokerTable, participants);
        }



        private void broadCastToPlayers(PokerTable channel) {
            DefaultNetworkEvent event = new DefaultNetworkEvent();
            event.setSource(channel.stateManager.getState());
            channel.sendBroadcast(event);
        }
    }

    public static class PokerSessionHandler extends DefaultSessionEventHandler {
        PokerTable channel;
        boolean isGameOver =false; ;
        // also.

        public PokerSessionHandler(PokerTable session) {
            super(session);
            this.channel = session;
            GameStateManagerService manager = channel.stateManager;
            PokerState state = (PokerState)manager.getState();
            // Initialize the room state.
            state = new PokerState(channel.getGameChannelName());
            state.setCards(new HashSet<Card>());
            state.setPoker_state(PokerState.POKER_STATE.IN_GAME);
            distributeCards();
            broadCastGameStarted(channel);
        }

        private void broadCastGameStarted(PokerTable channel) {
            Event event = new DefaultEvent();
            event.setType(Events.START);
            EventContext context = new DefaultEventContext();
            context.setAttachment(channel);
            event.setEventContext(context);
            channel.onEvent(event);

        }

        private void distributeCards() {
            //custom logic for distribution of Cards. Can use Knuth shuffle to shuffle the cards, and then send out cards
            //in network event
            for(PlayerSession session : channel.getSessions())
            {
                session.onEvent(new DefaultNetworkEvent()); //attach the appropriate cards.
            }
        }

        @Override
        public void onEvent(Event event) {

            /*
                Depending upon client-server logic and optimization, we can send/recieve entire state or just the move.
                Here all the game specific logic resides.
             */
            PokerState state = ((PokerState) event.getSource());
            Session session = event.getEventContext().getSession();

            update(state, session);
        }

        private void update(PokerState state, Session session) {

            if (isGameOver) {

                //Call game end task to end the game
                timer.schedule(new PokerEndTask(channel), 60*1000);
            }
        }

    }



}
