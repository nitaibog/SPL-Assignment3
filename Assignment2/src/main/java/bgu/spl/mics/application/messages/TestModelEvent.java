package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event<String>{

    private final String senderName;
    private final Model model;
    private final Student.Degree d;

    public TestModelEvent(String senderName, Model model, Student.Degree d){
        this.senderName = senderName;
        this.model = model;
        this.d = d;
    }

    public String getSenderName(){
        return this.senderName;
    }

    public Model getModel(){
        return model;
    }

    public Student.Degree getD() {
        return d;
    }
}
