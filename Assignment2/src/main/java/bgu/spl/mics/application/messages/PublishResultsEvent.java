package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<String> {

    private final String senderName;
    private Model model;

    public PublishResultsEvent(String senderName, Model model){
        this.model = model;
        this.senderName = senderName;
    }

    public String getSenderName(){
        return senderName;
    }

    public Model getModel() {
        return model;
    }
}
