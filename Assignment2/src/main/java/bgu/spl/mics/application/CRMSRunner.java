package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.ConferenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.Vector;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {


    public static void main(String[] args) {


        Vector<Thread> programThreads = new Vector<>();
        Gson gson = new Gson();
        Reader reader = null;
        try {
            reader = new FileReader(args[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonToObjects input = gson.fromJson(reader, JsonToObjects.class);


        for (int i = 0; i < input.getCPUS().length ; i++) {
            CPUService cpuManger = new CPUService("cpu "+i , input.getCPUS()[i]);
            Thread thread = new Thread(cpuManger);
            programThreads.add(thread);
            thread.start();
        }
        for (int i = 0; i < input.getGPUS().length; i++) {
            GPUService gpuService = new GPUService("gpu "+i ,input.getGPUS()[i]);
            Thread thread = new Thread(gpuService);
            programThreads.add(thread);
            thread.start();
        }
        ConferenceInformation[] confrencesForOoutput = new ConferenceInformation[input.getConferences().length];
        for (int i = 0; i < input.getConferences().length; i++) {
//            confrencesForOoutput[i] = new ConferenceInformation(input.getConferences()[i].getName(),input.getConferences()[i].getDate());
            ConferenceService conferenceService = new ConferenceService("conf "+i , input.getConferences()[i].getDate());
            confrencesForOoutput[i] = conferenceService.getConf();
            Thread thread = new Thread(conferenceService);
            programThreads.add(thread);
            thread.start();
        }

        Student[] studentsForOutput = new Student[input.getStudents().length];
        for (int i = 0; i < input.getStudents().length; i++) {
            studentsForOutput[i] = new Student(input.getStudents()[i].getName()
                    ,input.getStudents()[i].getDepartment(),input.getStudents()[i].getStatus()
                    ,input.getStudents()[i].getModels());

            for (Model m: studentsForOutput[i].getModels()) {
                m.setStudent(studentsForOutput[i]);
                m.startModel();
            }
            StudentService studentService = new StudentService("student" +i,studentsForOutput[i].getModels(),
                    studentsForOutput[i].getStatus(),studentsForOutput[i] );
            Thread thread = new Thread(studentService);
            programThreads.add(thread);
            thread.start();

        }


        TimeService timeService = new TimeService(input.getTickTime(),input.getDuration());
        Thread timeThread = new Thread(timeService);
        programThreads.add(timeThread);
        timeThread.start();

        JsonToObjects outPut = new JsonToObjects();
        for (Thread thread : programThreads){
            try {

                thread.join();
            } catch (InterruptedException ignored){}
        }


       File myFile = new File( "SPL-Ass2-main/src");
        try {
            FileWriter writer = new FileWriter( "output.txt");
            writer.write(outPut.studentOutPut(studentsForOutput));
            writer.write(outPut.conferences( confrencesForOoutput ) );
            writer.write(outPut.statistics());
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

}

