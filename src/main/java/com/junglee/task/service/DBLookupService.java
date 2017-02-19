package com.junglee.task.service;

import com.junglee.task.entity.Game;
import com.junglee.task.entity.GameChannel;
import com.junglee.task.util.Credentials;
import com.junglee.task.entity.Player;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
    public interface DBLookupService
    {
        public abstract GameChannel gameChannelLookup(Object gameContextKey);

        public abstract Game gameLookup(Object gameContextKey);

        public abstract Player playerLookup(Credentials loginDetail);

        public void savePlayer(Player player);

        public void saveGameSession(GameChannel channel);



}
