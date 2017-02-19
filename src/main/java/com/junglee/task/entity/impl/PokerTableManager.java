package com.junglee.task.entity.impl;

import com.junglee.task.entity.GameStateManagerService;
import com.junglee.task.event.Event;
import com.junglee.task.event.EventDispatcher;
import com.junglee.task.event.EventHandler;
import com.junglee.task.event.Events;
import com.junglee.task.service.DBLookupService;
import com.junglee.task.session.SessionFactory;
import com.junglee.task.session.id.UniqueIDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
@Component
public class PokerTableManager implements EventHandler {

    private EventDispatcher eventDispatcher;
    private com.junglee.task.service.DBLookupService DBLookupService;
    private UniqueIDService uniqueIDService;
    private GameStateManagerService gameStateManagerService;
    private SessionFactory sessionFactory;
    private GameChannelSession.GameChannelSessionBuilder sessionBuilder;

    @Autowired
    public PokerTableManager(EventDispatcher eventDispatcher, DBLookupService DBLookupService, UniqueIDService uniqueIDService, GameStateManagerService
                             gameStateManagerService, SessionFactory sessionFactory) {
        this.eventDispatcher = eventDispatcher;
        this.DBLookupService = DBLookupService;
        this.uniqueIDService = uniqueIDService;
        this.gameStateManagerService = gameStateManagerService;
        this.sessionFactory = sessionFactory;
        sessionBuilder = new GameChannelSession.GameChannelSessionBuilder(eventDispatcher, uniqueIDService);
        eventDispatcher.addHandler(this);
    }

    List<PokerTable> usedTableList = Collections.synchronizedList( new ArrayList<PokerTable>());
    List<PokerTable> avaiableTableList = Collections.synchronizedList (new ArrayList<PokerTable>());


    public PokerTable createOrGetTable()
    {
       if(avaiableTableList.isEmpty())
       {
           synchronized (this)
           {
               if(avaiableTableList.isEmpty()) //do
               {
                   return createNewPokerTable();
               }
           }
       }
       else
       {

           Iterator iterator = avaiableTableList.iterator();
           while(iterator.hasNext())
           {
               PokerTable pokerTable = (PokerTable) iterator.next();
               if(pokerTable.canTakeMorePlayers())
               {
                   return pokerTable;
               }
           }
       }
        return createNewPokerTable();
    }

    private PokerTable createNewPokerTable() {
        PokerTable table = new PokerTable(sessionBuilder, eventDispatcher, gameStateManagerService, sessionFactory);
        avaiableTableList.add(table);
        return table;
    }

    public void onEvent(Event event) {

        switch(event.getType())
        {
            case Events.START:
                populateUsedTables(event);
                break;
            case Events.STOP:
                 returnTableToAvailableTablePool(event);
                break;


        }

    }

    private void returnTableToAvailableTablePool(Event event) {
        PokerTable table = (PokerTable) event.getEventContext().getAttachment();
        synchronized (this)
        {
            usedTableList.remove(table);
            avaiableTableList.add(table);
        }
    }

    private void populateUsedTables(Event event) {
        PokerTable table = (PokerTable) event.getEventContext().getAttachment();
        synchronized (this)
        {
            avaiableTableList.remove(table);
            usedTableList.add(table);
        }
    }

    public int getEventType() {
        return Events.ANY;
    }
}
