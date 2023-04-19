package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;

import java.util.Collection;

public class JsonToObjects {


    private Student[] Students;
    private GPU.Type[] GPUS;
    private int [] CPUS;
    private ConferenceInformation[] Conferences;
    private long TickTime;
    private Long Duration;

    public GPU.Type[] getGPUS() {
        return GPUS;
    }

    public int[] getCPUS() {
        return CPUS;
    }

    public ConferenceInformation[] getConferences() {
        return Conferences;
    }

    public long getTickTime() {
        return TickTime;
    }

    public Long getDuration() {
        return Duration;
    }

    public Student[] getStudents(){
        return Students;
    }

    public String studentOutPut(Student[] students){
        String output ="students:\n";
        String space = "    ";
        for (Student s : students){
            output+= "{\n";
            output+= space + "name: "+ s.getName() +"\n";
            output+= space + "department: " + s.getDepartment() + "\n";
            output+= space + "status: " + s.getStatus().toString() + "\n";
            output+= space + "publications: " + s.getPublications() + "\n";
            output+= space + "papersRead: " + s.getPapersRead() + "\n";
            output+= space + "trainedModels:\n";
            Model[] models = s.getModels();
            for (int i = 0; i < models.length; i++) {
                if (models[i].getStatus().toString().equals("Trained") ||
                        models[i].getStatus().toString().equals("Tested")){
                    output+= writeModel(models[i]);
                }
            }
            output+= "}\n";
        }
        return output;
    }
    public String conferences (ConferenceInformation[] conferences) {
        String output = "conferences:\n";
        String space = "    ";
        for (ConferenceInformation conf : conferences) {
            output += "{\n";
            output += space + "name: " + conf.getName() + "\n";
            output += space + "date: " + conf.getDate() + "\n";
            output+= space + "publications: " + "\n";
            Collection<Model> models = conf.getStudentModelMap().values();
            for (Model model : models){
                output+= writeModel( model );

            }
            output += "}\n";
        }
        return output;
    }

    public String statistics(){
        String output = "cpuTimeUsed: " + Cluster.getInstance().getCpuTotalUsedTime() + "\n";
        output+= "gpuTimeUsed: " + Cluster.getInstance().getGpuTotalUsedTime() + "\n";
        output+= "batchesProcessed: " + Cluster.getInstance().getBatchesProcessed() + "\n";
        return output;
    }

    public String writeModel(Model model){
        String output = "";
        String space = "    ";
        output+= space + "{\n";
        output += space + space + "name: " + model.getName() + "\n";
        output += space + space + "data:\n";
        output += space + space + space +"type: " + model.getData().getType().toString() + "\n";
        output += space + space + space + "size: " + model.getData().getSize() + "\n";
        output += space + space + "status:" + model.getStatus().toString() +"\n";
        output += space + space + "result: "+model.getResult().toString() + "\n";
        output+= space + "}\n";
        return output;

    }
}

