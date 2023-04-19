package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Map;

public class PublishConfrenceBroadcast implements Broadcast {

    private Map<Student, Model> studentModelMap;


    public PublishConfrenceBroadcast(Map<Student, Model> map){
        this.studentModelMap = map;
    }

    public Map<Student, Model> getStudentModelMap() {
        return studentModelMap;
    }
}
