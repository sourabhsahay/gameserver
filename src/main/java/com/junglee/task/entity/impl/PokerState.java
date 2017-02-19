package com.junglee.task.entity.impl;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
public class PokerState implements Serializable {

    private static final long serialVersionUID = 1L;
    public enum POKER_STATE  {
        WAITING_FOR_PLAYERS,IN_GAME;
    }

    String parentChannel;

    private POKER_STATE poker_state = POKER_STATE.WAITING_FOR_PLAYERS;
    private String winner;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public POKER_STATE getPoker_state() {
        return poker_state;
    }

    public void setPoker_state(POKER_STATE poker_state) {
        this.poker_state = poker_state;
    }

    private Set<Card> cards;

    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }

    public PokerState(String parentChannel)
    {
       this.parentChannel = parentChannel;
    }
    public PokerState(Set<Card> cards, String parentChannel)
    {
        super();
        this.cards = cards;
        this.parentChannel = parentChannel;
    }

    public void addCard(Card card)
    {
        // only the id will match, but other values maybe different.
        cards.add(card);
    }



}
