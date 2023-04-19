package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Collection;
import java.util.Map;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class StudentService extends MicroService {

    private Model[] models;
    private final Student.Degree d;
    private Future<String> TrainMod;
    private Future<String> TestMod;
    private Future<String> PublishMod;
    private int Published = 0;
    private final MessageBus messageBus;
    private Map<Student, Model> studentModelMap;
    private final Student myStudent;
    private long currTick = 1;
    private boolean busy = false;
    private int currModel = 0;


    public StudentService(String name, Model[] models, Student.Degree d,Student student) {
        super(name);
        this.models = models;
        this.d = d;
        this.messageBus = MessageBusImpl.getInstance();
        this.myStudent = student;

    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(PublishConfrenceBroadcast.class, (PublishConfrenceBroadcast b) -> {
            studentModelMap = b.getStudentModelMap();
            Collection<Model> publishedModels = studentModelMap.values();
            for(Model mod : publishedModels){
                if (mod.getStudent() == myStudent)
                    myStudent.setPublications();
                else myStudent.setPapersRead();

            }
        });

        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast b) -> {
            if (currModel <= models.length) {
                currTick++;
                if (!this.busy) {
                    if (messageBus.isThereMSRegister( TrainModelEvent.class )) {
                        this.TrainMod = sendEvent( new TrainModelEvent( getName(), models[currModel] ) );
                        this.busy = true;
                    }
                }
//            System.out.println(myStudent.getName() + " just sended an trainModel eve");
                if (this.busy) {
                    if (TrainMod.isDone() && TrainMod.get().equals( "Trained" ) && TestMod == null) {
                        this.TestMod = sendEvent( new TestModelEvent( this.getName(), models[currModel], d ) );
                    }

                    if (TestMod.isDone()) {
                        if (TestMod.get().equals( "Good" )) {
                            PublishMod = sendEvent( new PublishResultsEvent( getName(), models[currModel] ) );
                            PublishMod.get();
                            Published++;
                        }
                        TestMod = null;
                        this.busy = false;
                        currModel++;
                    }
                }
            }
        });


//        for (Model model : models) {
//
//            while (!messageBus.isThereMSRegister( TrainModelEvent.class )) {
//            } /// deeebuggggggg
//
//            this.TrainMod = sendEvent( new TrainModelEvent( getName(), model ) );
////            System.out.println(myStudent.getName() + " just sended an trainModel eve");
//            if (TrainMod.get().equals( "Trained" )) {
//                this.TestMod = sendEvent( new TestModelEvent( this.getName(), model, d ));
//            }
//
//            while (!messageBus.isThereMSRegister( PublishResultsEvent.class )) {
//
//            }
//            if (TestMod.get().equals( "Good" )) {
//                PublishMod = sendEvent( new PublishResultsEvent( getName(), model ));
//                PublishMod.get();
//                Published++;
//            }
//
//
//        }

        subscribeBroadcast( TerminateBroadcast.class, (TerminateBroadcast b) -> {
            terminate();
        });
        }


    public int getPublished() {
        return Published;
    }
}
