package com.junglee.task.service.impl;

import com.junglee.task.entity.Game;
import com.junglee.task.entity.GameChannel;
import com.junglee.task.entity.Player;
import com.junglee.task.service.DBLookupService;
import com.junglee.task.util.Credentials;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
@Service
public class DBLookupServiceImpl implements DBLookupService
{
//    @Autowired
//    private RedisTemplate< String, Object > template;

    private final Map<String, GameChannel> gameKeyGameChannelMap;
    private final Map<String,Player> playerMap;

    public DBLookupServiceImpl()
    {

        gameKeyGameChannelMap = new HashMap<String, GameChannel>();
        playerMap = new HashMap<String, Player>();
    }

    public DBLookupServiceImpl(Map<String, GameChannel> refKeyGameRoomMap, Map<String, Player> playerMap)
    {
        super();
        this.gameKeyGameChannelMap = refKeyGameRoomMap;
        this.playerMap = playerMap;
    }

    public Game gameLookup(Object gameContextKey)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public GameChannel gameChannelLookup(Object gameContextKey)
    {
        return gameKeyGameChannelMap.get((String) gameContextKey);
    }

    public Player playerLookup(Credentials loginDetail)
    {
        Player player = playerMap.get(loginDetail.getUsername());
        return player;

    }

    public void savePlayer(Player player)
    {
       playerMap.put(player.getName(), player);

    }

    public void saveGameSession(GameChannel channel) {
        gameKeyGameChannelMap.put(channel.getGameChannelName(),channel);
    }


    public Map<String, GameChannel> getGameKeyGameChannelMap()
    {
        return gameKeyGameChannelMap;
    }


}
