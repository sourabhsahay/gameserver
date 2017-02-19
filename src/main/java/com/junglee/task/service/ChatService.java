package com.junglee.task.service;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
public interface ChatService  {

    public void saveChatMessage(String tableId, String playerId, String chatMessage);

    public  List<Pair<String,String>> getChat(String tableId);

}
