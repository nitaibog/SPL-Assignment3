package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DataPreProcessEvent implements Event<String> {
    private String senderName;

    public DataPreProcessEvent(String senderName){
        this.senderName = senderName;
    }

    public String getSenderName(){
        return this.senderName;
    }
}
