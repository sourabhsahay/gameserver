package com.junglee.task.entity.impl;

import com.junglee.task.entity.Player;
import com.junglee.task.session.PlayerSession;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public class PlayerImpl implements Player
{
    private Object id;

    private String name;

    private String emailId;

    private int gamesPlayed;

    /**
      multiple sessions for multiple games.
     */
    private Set<PlayerSession> playerSessions;

    public PlayerImpl()
    {
        playerSessions = new HashSet<PlayerSession>();
    }

    public PlayerImpl(Object id, String name, String emailId)
    {
        super();
        this.id = id;
        this.name = name;
        this.emailId = emailId;
        playerSessions = new HashSet<PlayerSession>();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(id).append(name).append(emailId).build().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof PlayerImpl))
        {
            return false;
        }
        PlayerImpl otherPlayer = (PlayerImpl) obj;

        return new EqualsBuilder().append(this.emailId,otherPlayer.emailId)
                           .append(this.id, otherPlayer.id)
                           .append(this.getName(),otherPlayer.getName())
                           .build().booleanValue();
    }

    public Object getId()
    {
        return id;
    }

    public void setId(Object id)
    {
        this.id = id;
    }


    public String getName()
    {
        return name;
    }



    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmailId()
    {
        return emailId;
    }

    public void setEmailId(String emailId)
    {
        this.emailId = emailId;
    }

    public synchronized boolean addSession(PlayerSession session)
    {
        return playerSessions.add(session);
    }

    public synchronized boolean removeSession(PlayerSession session)
    {
        boolean remove = playerSessions.remove(session);
        if(playerSessions.size() == 0){
            logout(session);
        }
        return remove;
    }

    public synchronized void logout(PlayerSession session)
    {
        session.close();
        if(null != playerSessions)
        {
            playerSessions.remove(session);
        }
    }

    public Set<PlayerSession> getPlayerSessions()
    {
        return playerSessions;
    }

    public void setPlayerSessions(Set<PlayerSession> playerSessions)
    {
        this.playerSessions = playerSessions;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

}

