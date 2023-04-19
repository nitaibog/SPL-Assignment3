package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private CPUService cpuService;
    private int cores;
    private Collection<DataBatch> preProcData;
    private Cluster cluster;
    private long Ticks;
    private long cpuUsedTime;
    private DataBatch currBatch;
    private boolean processing;
    private int TicksToProc = 0;
    private String name;
    private int processedCount = 0;


    public CPU(String name, int cores,CPUService cpuService){
        this.cores = cores;
        this.preProcData = new LinkedBlockingQueue<DataBatch>();
//        this.preProcData = new ConcurrentLinkedQueue<>();
        this.cluster = Cluster.getInstance();
        this.processing = false;
        this.Ticks = 0;
        this.cpuUsedTime = 0;
        this.cpuService = cpuService;
        this.name = name;
    }
    /**
     * @PRE: none
     * @POST: data.size() == data.size @PRE + 1
     */
    public void addData(DataBatch data){
        synchronized (preProcData) {
            preProcData.add( data );
            TicksToProc = TicksToProc + waitTime( data.getType() );
        }
    }

    public DataBatch getCurrBatch() {
        return currBatch;
    }

    /**
     *
     * @param
     * @PRE: isAvailable == true
     * @POST: lgsksfdksojfnlkdjfdklsfjklsdfjkldsfkdslkfjdsklfdlskflksdjf
     */
    public void processes(){
        currBatch = ((LinkedBlockingQueue<DataBatch>)preProcData).remove() ; // should we check if theres any data
        if (currBatch != null) {
            processing = true;
            cpuService.setWaitTime(waitTime(currBatch.getType()));
        }

    }

    /**
     * @PRE: data.size() > 0
     * @POST: ddata.size() == data.size @PRE - 1
     */
    public void send(){
        processing = false;
        cpuService.setWaitTime(-1);
        this.processedCount++;
        cluster.setProcessedBatch(currBatch);
    }

    public int getDataSize(){
        return preProcData.size();
    }

    private int waitTime(Data.Type type){
        int temp = 32/cores;
        if (type == Data.Type.Images)
            return temp*4;
        else if (type == Data.Type.Text)
            return temp*2;
        return temp;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public boolean isProcessing() {
        return processing;
    }

    public int getCores() {
        return cores;
    }

    public void setTicksToProc() {
        TicksToProc = TicksToProc -1;
    }

    public int getTicksToProc() {
        return TicksToProc;
    }


    public int expectedTicksToPro(DataBatch batch){
            return waitTime(batch.getType()) + TicksToProc;

    }


    public int getPreProcDataSize(){
        return preProcData.size();
    }

    public long getCpuTimeService() {
        return cpuService.getCurrTick();
    }

    public String getName() {
        return name;
    }

    public void setCpuUsedTime() {
        this.cpuUsedTime = cpuUsedTime +1;
    }

    public long getCpuUsedTime() {
        return cpuUsedTime;
    }
    public void setTimeUsedToCluster(){
        cluster.setCpuTotalUsedTime(cpuUsedTime);
    }

    public int getProcessedCount() {
        return processedCount;
    }
}
