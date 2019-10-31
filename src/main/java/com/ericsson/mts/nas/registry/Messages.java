package com.ericsson.mts.nas.registry;

import com.ericsson.mts.nas.message.AbstractMessage;

import java.util.List;

public class Messages {
    private List<AbstractMessage> messages;

    public void addMessage(AbstractMessage message){
        messages.add(message);
    }

    public AbstractMessage getMessage(String messageName){
        for(AbstractMessage message : messages){
            if(messageName.equals(message.name)){
                return message;
            }
        }
        return null;
    }

    public List<AbstractMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<AbstractMessage> messages) {
        this.messages = messages;
    }
}
