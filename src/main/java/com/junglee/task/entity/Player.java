package com.junglee.task.entity;

import com.junglee.task.session.PlayerSession;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface Player
{

    public Object getId();

    public void setId(Object uniqueKey);

    public String getName();

    public void setName(String name);

    public String getEmailId();

    public void setEmailId(String emailId);

    public int getGamesPlayed();

    public void setGamesPlayed(int gamesPlayed);

    public boolean addSession(PlayerSession session);

    public boolean removeSession(PlayerSession session);

    public void logout(PlayerSession playerSession);

}