package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private final GPUService GpuService;
    private Data data;
    private int dataSize;
    private final Type type;
    private Model model;
    private final Cluster cluster;
    private int Memory;
    private int freeMemory;
    private int batchesToTrain;
    private Queue<DataBatch> processedBatches;
    private Queue<DataBatch> unProcessedBatches;
    private boolean training = false;
    private boolean busy = false;
    private String name;
    private long GpuUsedTime = 0;


    public GPU(Type type, GPUService service, String name){
        this.name = name;
        this.type = type;
        this.cluster = Cluster.getInstance();
        this.Memory = memoryByType();
        this.processedBatches = new LinkedBlockingQueue<DataBatch>();
        this.unProcessedBatches = new LinkedBlockingQueue<DataBatch>();
//        unProcessedBatches = new ConcurrentLinkedQueue<>();
//        processedBatches = new ConcurrentLinkedQueue<>();
        this.GpuService = service;


    }

    public boolean isBusy() {
        return busy;
    }


    public int getMemory(){
        return Memory;
    }


    public int getProcessedSize(){
        return processedBatches.size();
    }

    public int getUnprocessedSize(){
        return unProcessedBatches.size();
    }


    /**
     * @PRE: unProcessedBatches.size() > 0
     * @POST: unProcessedBatches.size() == unProcessedBatches.size()@PRE -1;
     *
     */


    /**
     *
     * @param processedData
     * @PRE: freeMemory > 0
     * @POST: ProcessedBatches.size() == ProcessedBatches.size()@PRE +1;
     * @POST: freeMemory == freeMemory@PRE -1;
     *
     */
    public void processedInsert(DataBatch processedData){
            processedBatches.add(processedData);

    }


    public int getBatchesToTrain() {
        return batchesToTrain;
    }

    public void divedDataAndInsert(){
        while(dataSize > 0){
            unProcessedBatches.add(new DataBatch(data));
            dataSize = dataSize -1000;
            batchesToTrain++;
        }

    }

    private int memoryByType(){
        if (type == Type.RTX3090)
            return 32;
        else if (type == Type.RTX2080)
            return 16;
        else return 8;
    }

    public void setModel(Model model) {
        this.model = model;
        this.busy = true;
        this.data = model.getData();
        dataSize = data.getSize();
    }

    public Model getModel() {
        return model;
    }

    public Model.status getModelStatus(){
        return this.model.getStatus();
    }
    public void setModelStatus(Model.status stat){
        this.model.setStatus(stat);
    }

    public int waitTime(){
        if (type == Type.RTX3090)
            return 1;
        else if (type == Type.RTX2080)
            return 2;
        else return 4;
    }


    public Queue<DataBatch> getProcessedBatches() {
        return processedBatches;
    }

    public void train(){
            this.training = true;
            processedBatches.poll();
//            long currTick = GpuService.getCurrTick();
//            long wait = this.waitTime();
            GpuService.setWaitTime(this.waitTime());
//            while (GpuService.getCurrTick() < currTick + wait){
//                GpuUsedTime++;
//            }
            Memory++;
            batchesToTrain--;
    }

    public void sendUnProcDataBatch(){
        while (Memory > 0 & !unProcessedBatches.isEmpty()) {
            cluster.receiveBatch(unProcessedBatches.poll(), this);
            Memory--;
        }
        this.training = false;
    }

    public void probByStudent(Student.Degree d){
        double temp = Math.random();
        if (d == Student.Degree.PhD) {
            if (temp < 0.8) {
                model.setResult( Model.results.Good );
            }
            else model.setResult( Model.results.Bad );
        }
        else {
            if (temp < 0.6) {
                model.setResult( Model.results.Good );
            }
            else {
                model.setResult( Model.results.Bad );
            }
        }


    }

    public boolean isTraining() {
        return training;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setGpuUsedTime() {
        GpuUsedTime = GpuUsedTime + 1 ;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public void setTimeUsedToCluster(){
        cluster.setGpuTotalUsedTime(GpuUsedTime);
    }
    public void addTrainedModelNameToCluster(){
        cluster.setTrainedModelsNames(model.getName());
    }
}

