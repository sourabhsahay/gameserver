package com.junglee.task.session.impl;

import com.junglee.task.entity.GameChannel;
import com.junglee.task.entity.Player;
import com.junglee.task.session.PlayerSession;
import com.junglee.task.session.Session;
import com.junglee.task.session.SessionFactory;
import com.junglee.task.session.id.UniqueIDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
@Component
public class SessionFactoryImpl implements SessionFactory
{

    UniqueIDService uniqueIDService;

     @Autowired
    public SessionFactoryImpl(UniqueIDService uniqueIDService) {
        this.uniqueIDService = uniqueIDService;
    }

    public  Session createSession()
    {

        return new DefaultSession.SessionBuilder(uniqueIDService).build();
    }

    public PlayerSession createPlayerSession(GameChannel gameChannel, Player player)
    {
        return new DefaultPlayerSession.PlayerSessionBuilder(uniqueIDService).parentGameRoom(gameChannel).player(player).build();
    }

}

