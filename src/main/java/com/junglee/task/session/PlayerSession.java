package com.junglee.task.session;

import com.junglee.task.entity.GameChannel;
import com.junglee.task.entity.Player;
import com.junglee.task.event.Event;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface PlayerSession extends Session
{
    public abstract Player getPlayer();

    public abstract GameChannel getGameChannel();

    public abstract void setGameRoom(GameChannel gameChannel);

    public void sendToGameChannel(Event event);

    public void setSessionStartTime(long sessionStartTime);

    public void setSessionEndTime(long sessionEndTime);

    public long getSessionStartTime();

    public long getSessionEndTime();


}
