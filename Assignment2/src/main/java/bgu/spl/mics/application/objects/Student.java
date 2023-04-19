package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.StudentService;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
  public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private MicroService microService;
    private Model[] models;


    public Student(String name, String department, Degree status,Model[] models){
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = 0;
        this.papersRead = 0;
        this.microService = new StudentService(name,models,status,this);
        this.models = models;
    }

    public String getName() {
        return name;
    }

    public Model[] getModels() {
        return models;
    }

    public Degree getStatus() {
        return status;
    }

    public String getDepartment() {
        return department;
    }

    public void setPublications() {
        this.publications = publications + 1;
    }

    public void setPapersRead() {
        this.papersRead = papersRead + 1;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }
}
