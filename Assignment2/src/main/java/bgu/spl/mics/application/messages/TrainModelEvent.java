package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<String>{

    private final String senderName;
    private final Model model;

    public TrainModelEvent(String senderName, Model model){
        this.senderName = senderName;
        this.model = model;
    }

    public String getSenderName(){
        return this.senderName;
    }

    public Model getModel(){
        return model;
    }

}
