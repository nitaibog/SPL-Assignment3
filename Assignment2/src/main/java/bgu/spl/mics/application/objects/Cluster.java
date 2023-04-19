package bgu.spl.mics.application.objects;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private Collection<GPU> GPUS;
	private Collection<CPU> CPUS;
	private Map<DataBatch, GPU> GpuByBatch;
	private Map<Integer , PriorityQueue<CPU>> map;
	private Queue<DataBatch> processedBatch;
	private PriorityQueue[] arr;
	private final Object Lock1 = new Object();
	private final Object Lock2 = new Object();
	private final Object Lock3 = new Object();
	private Collection<String> trainedModelsNames;
	private long batchesProcessed = 0;
	private long gpuTotalUsedTime = 0;
	private long cpuTotalUsedTime = 0;
	/**
     * Retrieves the single instance of this class.
     */

	private Cluster(){
		this.CPUS = new LinkedList<>();
		map = new ConcurrentHashMap<>();
		this.GpuByBatch = new ConcurrentHashMap<>();
		this.processedBatch = new LinkedBlockingQueue<>();
		this.arr = new PriorityQueue[6]; /// 1 core = [0] , 2 cores = [1] .... 32 cores = [5]
		for (int i = 0; i < arr.length; i++) {
			arr[i] = new PriorityQueue<CPU>(new ComparatorByTicks());
		}
		trainedModelsNames = new LinkedList<>();
	}

	private static class SingletonHolder{
		private static final Cluster instance = new Cluster();
	}

	public static Cluster getInstance() {
		return SingletonHolder.instance;
	}


	public void setProcessedBatch(DataBatch batch){
			GpuByBatch.get(batch).processedInsert(batch);
			this.batchesProcessed++;

	}

	public void receiveBatch(DataBatch batch,GPU gpu){
		synchronized (GpuByBatch) {
			GpuByBatch.put( batch, gpu );
			sendBatchToCPU( batch );
		}
	}

	private void sendBatchToCPU(DataBatch batch) {
			int minExpectedTime = Integer.MAX_VALUE;
			int index = -1;
			for (int i = 0; i < arr.length; i++) {
				if (((CPU) arr[i].peek()) != null && ((CPU) arr[i].peek()).expectedTicksToPro( batch ) < minExpectedTime) {
					minExpectedTime = ((CPU) arr[i].peek()).expectedTicksToPro( batch );
					index = i;
				}
			}
			CPU temp = (CPU) arr[index].poll();
			temp.addData( batch );
			arr[index].add( temp );

	}


	private int indexByCores(int cores){
		if (cores == 1)
			return 0;
		else if (cores == 2)
			return 1;
		else if (cores == 4)
			return 2;
		else if (cores == 8)
			return 3;
		else if (cores == 16)
			return 4;
		else return 5;
	}

	public void addCPUStoCluster(CPU cpu){
			CPUS.add(cpu);
			arr[indexByCores( cpu.getCores() )].add(cpu);
	}
	public void addGPUStoCluster(GPU gpu){
		GPUS.add(gpu);
	}

	public void setBatchesTrained(long batchesTrained) {
		this.batchesProcessed = batchesTrained;
	}

	public void setGpuTotalUsedTime(long gpuTotalUsedTime) {
		this.gpuTotalUsedTime = this.gpuTotalUsedTime + gpuTotalUsedTime;
	}

	public void setCpuTotalUsedTime(long cpuTotalUsedTime) {
		this.cpuTotalUsedTime = this.cpuTotalUsedTime + cpuTotalUsedTime;
	}

	public void setTrainedModelsNames(String trainedModelName) {
		synchronized (trainedModelsNames) {
			this.trainedModelsNames.add( trainedModelName );
		}
	}

	public long getBatchesProcessed() {
		return batchesProcessed;
	}

	public long getGpuTotalUsedTime() {
		return gpuTotalUsedTime;
	}

	public long getCpuTotalUsedTime() {
		return cpuTotalUsedTime;
	}
}








 class ComparatorByTicks implements Comparator<CPU>{

	 @Override
	 public int compare(CPU o1, CPU o2) {
		 if (o1.getTicksToProc() > o2.getTicksToProc())
		 	return 1;
		 else if (o1.getTicksToProc() == o2.getTicksToProc())
		 	return 0;
		 else return -1;
	 }
 }