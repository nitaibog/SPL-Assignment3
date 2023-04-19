package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DataPreProcessEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private final CPU cpu;
    private long currTick = 1;
    private long processingStartTick;
    private int waitTime;


    public CPUService(String name, int cores) {
        super(name);
        cpu = new CPU(name,cores,this);
        Cluster.getInstance().addCPUStoCluster(cpu);
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast b) -> {
            currTick++;
            if (!cpu.isProcessing() && cpu.getDataSize() > 0){
                cpu.processes();
            } else {
                if (waitTime > 0) {
//                    System.out.println(cpu.getName() + " is processing, currTick = " + currTick +" \n" +"****************************************************************************************");
                    waitTime--;
                    cpu.setTicksToProc();
                    cpu.setCpuUsedTime();
                }   if (waitTime == 0)
                    cpu.send();
            }
        });

        this.subscribeBroadcast( TerminateBroadcast.class,(TerminateBroadcast b) ->{

            cpu.setTimeUsedToCluster();
            terminate();
        } );
    }

    public long getCurrTick() {
        return currTick;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
}
