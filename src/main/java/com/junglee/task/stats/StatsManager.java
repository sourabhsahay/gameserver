package com.junglee.task.stats;

import com.junglee.task.entity.Player;
import com.junglee.task.event.Event;
import com.junglee.task.event.EventHandler;
import com.junglee.task.event.Events;
import com.junglee.task.service.DBLookupService;

import java.util.List;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
public class StatsManager implements EventHandler {

    private DBLookupService DBLookupService;

    public StatsManager(DBLookupService DBLookupService)
    {
        this.DBLookupService = DBLookupService;
    }

    public  void onEvent(Event event) {
        List<Player> playerList= (List<Player>) event.getSource();
        for(Player player : playerList)
        {
            player.setGamesPlayed(player.getGamesPlayed() +1);
            this.DBLookupService.savePlayer(player);
        }
    }

    public int getEventType() {
       return Events.STOP;
    }
}
