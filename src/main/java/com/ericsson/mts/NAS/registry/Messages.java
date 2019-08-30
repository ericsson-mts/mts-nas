package com.ericsson.mts.NAS.registry;

import com.ericsson.mts.NAS.message.AbstractMessage;
import com.ericsson.mts.NAS.message.Message;

import java.util.List;

public class Messages {
    private List<AbstractMessage> messages;

    public void addMessage(AbstractMessage message){
        messages.add(message);
    }

    public AbstractMessage getMessage(String messageName){
        for(AbstractMessage message : messages){
            if(messageName.equals(message.getName())){
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
