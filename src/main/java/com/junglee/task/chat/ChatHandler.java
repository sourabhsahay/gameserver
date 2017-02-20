package com.junglee.task.chat;

import com.junglee.task.entity.impl.PokerTable;
import com.junglee.task.event.*;
import com.junglee.task.event.impl.ChatEvent;
import com.junglee.task.event.impl.DefaultEventContext;
import com.junglee.task.service.ChatService;
import com.junglee.task.session.PlayerSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
@Component
public class ChatHandler implements EventHandler {

    private ChatService chatService;


    @Autowired
    public ChatHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    public void onEvent(Event event) {
        switch(event.getType())
        {
            case Events.CHAT_IN:
                handleChat(event);
                break;
            case Events.CHAT_OUT:
                handleChat(event);
                break;
            default:
                break;

        }

    }

    private void handleChat(Event event) {
        String chatMessage = (String)event.getSource();
        String chatOriginator;
        if(event.getEventContext()==null )
        {
            //this means it is a system generated chat Message
            chatOriginator = "Admin";
        }
        final PlayerSession playerSession = (PlayerSession) event.getEventContext().getAttachment();
        final PokerTable pokerTable = (PokerTable) playerSession.getGameChannel();
        chatOriginator = playerSession.getPlayer().getEmailId();
        chatService.saveChatMessage(pokerTable.getGameChannelName(),chatOriginator ,chatMessage);
        broadCastToOtherPlayers(pokerTable, chatMessage);


    }

    private void broadCastToOtherPlayers(PokerTable pokerTable, String chatMessage) {
        ChatEvent event = new ChatEvent();
        EventContext context = new DefaultEventContext();
        context.setAttachment(pokerTable.getId());
        event.setEventContext(context);
        event.setSource(chatMessage);
        pokerTable.onEvent(event);


    }

    public int getEventType() {
        return Events.ANY;
    }
}
