package bgu.spl.mics.application.objects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConferenceInformation {

    private String name;
    private int date;
    private Map<Student ,Model> studentModelMap;



    public ConferenceInformation(String name, int date){
        this.name = name;
        this.date = date;
        studentModelMap = new ConcurrentHashMap<>();
    }

    public Map<Student, Model> getStudentModelMap() {
        return studentModelMap;
    }

    public void addModel(Student student,Model model){
        studentModelMap.put(student, model);
    }

    public int getDate() {
        return date;
    }

    public String getName() {
        return name;
    }
}
