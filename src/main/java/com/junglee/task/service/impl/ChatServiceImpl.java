package com.junglee.task.service.impl;

import com.junglee.task.service.ChatService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
@Service
public class ChatServiceImpl implements ChatService {
    //should be replaced by NoSQL db
    private Map<String, List<Pair<String,String>>> tableChat = new HashMap<String, List<Pair<String,String>>>();

    public void saveChatMessage(String tableId, String playerId, String chatMessage) {
        List<Pair<String,String>>  chatMessages = null;
        if (tableChat.containsKey(tableId)) {
            chatMessages = tableChat.get(tableId);

        } else {
            chatMessages = new ArrayList<Pair<String,String>>();
        }
        chatMessages.add(new ImmutablePair(playerId, chatMessage));
    }



    public List<Pair<String,String>>  getChat(String tableId) {

        return  tableChat.get(tableId);
    }

    }

