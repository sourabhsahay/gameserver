package com.junglee.task.session;

import com.junglee.task.entity.GameChannel;
import com.junglee.task.entity.Player;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public interface SessionFactory
{
    public  Session createSession();
    public  PlayerSession createPlayerSession(GameChannel gameChannel, Player player);
}