package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private final GPU gpu;
    private long currTick = 1;
    private TrainModelEvent trainEve;
    private TestModelEvent TestEve;
    private int waitTime = -1;
    private Queue<Event> pendingEvents;


    public GPUService(String name, GPU.Type type) {
        super(name);
        gpu = new GPU(type,this,name);
        this.pendingEvents = new LinkedBlockingQueue<>();
//        pendingEvents = new ConcurrentLinkedQueue<>();
//        Cluster.getInstance().addGPUStoCluster(gpu);
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast( TickBroadcast.class, (TickBroadcast b) ->{
            currTick++;
            if (gpu.isBusy()){
                if (gpu.getModelStatus() == Model.status.PreTrained) {
                    gpu.setModelStatus(Model.status.Training);
                    gpu.sendUnProcDataBatch();
                }
                if (gpu.getModelStatus() == Model.status.Training) {
                    if (gpu.getBatchesToTrain() > 0) {
                        if (!gpu.isTraining()) {
                            if (gpu.getProcessedSize() > 0) {
                                gpu.train();

//                                System.out.println(gpu.getName() +" just started to train a batch, waitTime = " + gpu.waitTime() +"\n" +"" +
//                                        "****************************************************************************************");
                            }
                        } else
                            if (waitTime > 0) {
//                            System.out.println(gpu.getName() +" is Training, currTick = " + currTick +"\n" +
//                                    "****************************************************************************************");
                            waitTime--;
                            gpu.setGpuUsedTime();
                        }
                        if (waitTime == 0) {
//                            System.out.println( gpu.getName() + " just finished to train a batch" + "\n" +
//                                    " remaining batches to train = " + gpu.getBatchesToTrain() + "\n" + "remaining batches to send = " + gpu.getUnprocessedSize() + "\n"
//                                    + "****************************************************************************************" );
                            gpu.sendUnProcDataBatch();
                            waitTime = -1;
                        }
                    } else {
                        gpu.setModelStatus(Model.status.Trained);
                        gpu.addTrainedModelNameToCluster();
                        complete( trainEve, "Trained" );
                        gpu.setBusy(false);

                    }
                }
            } else {
                if (!pendingEvents.isEmpty()) {
                    if (pendingEvents.peek() instanceof TrainModelEvent) {

                        trainEve = (TrainModelEvent) pendingEvents.poll();
                        gpu.setModel(trainEve.getModel());
                        gpu.divedDataAndInsert();

//                        System.out.println( "Ive just started to Train the " + trainEve.getModel().getName() + " Model and this student sended it - " + trainEve.getSenderName() + "\n"
//                                + "Model size: " + trainEve.getModel().getData().getSize() + ", Model type: " + trainEve.getModel().getData().getType() + "\n"
//                                + "GPU details: Gpu Type: " + gpu.getType() + ", Gpu name: " + gpu.getName() + "\n"
//                                + "currTick = " + currTick + "\n" + "****************************************************************************************" );
                    } else if (pendingEvents.peek() instanceof TestModelEvent){

                                TestEve = (TestModelEvent) pendingEvents.poll();
                                gpu.setModel(TestEve.getModel());
                                gpu.probByStudent( TestEve.getD());
                                TestEve.getModel().setStatus( Model.status.Tested);
                                gpu.setBusy(false);
                            complete( TestEve, TestEve.getModel().getResult().toString() );


                    }
                }

            }
        } );


        this.subscribeEvent(TrainModelEvent.class, (TrainModelEvent e) -> {
              pendingEvents.add(e);

        });


        this.subscribeEvent(TestModelEvent.class, (TestModelEvent e) -> {
            pendingEvents.add(e);

        });

        this.subscribeBroadcast( TerminateBroadcast.class, (TerminateBroadcast b) ->{
            gpu.setTimeUsedToCluster();
            terminate();
        });

    }
    public long getCurrTick() {
        return currTick;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
}
