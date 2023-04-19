package bgu.spl.mics;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private final Map<MicroService, LinkedBlockingQueue<Message>> messageQueues; // Maybe its should be private final?,, there is better data structure then Queue??
	private final Map<Class<? extends Event<?>>, Queue<MicroService>> eventMap; // private final?
	private final Map<Class<? extends Broadcast>, Set<MicroService>> broadcastMap;
	private final Map<Event<?>, Future> futureMap;

	private final Object roundRobinLock = new Object();

	private static class SingletonHolder{
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBus getInstance(){
		return SingletonHolder.instance;
	}
	private MessageBusImpl() {
		this.messageQueues = new ConcurrentHashMap<>();
		this.eventMap = new ConcurrentHashMap<>();
		this.broadcastMap = new ConcurrentHashMap<>();
		this.futureMap = new ConcurrentHashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// if the micro service isn't registered to the messageBus, should we throw Exception??


		synchronized(eventMap) {
			if (!this.eventMap.containsKey( type )) {
				this.eventMap.put( type, new LinkedBlockingQueue<MicroService>() );
			}
			Queue<MicroService> microServiceQueue = this.eventMap.get( type );
			microServiceQueue.add( m );
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (broadcastMap) {
			if (!this.broadcastMap.containsKey( type )) {
				this.broadcastMap.put( type, ConcurrentHashMap.newKeySet() );
			}
		}
		this.broadcastMap.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futureMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(broadcastMap.get(b.getClass()) != null){
			for (MicroService service : broadcastMap.get(b.getClass())){
				messageQueues.get(service).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (roundRobinLock) {
			Queue<MicroService> q = eventMap.get(e.getClass());
			if (q != null) {
				MicroService service = q.poll();
				if (service != null) {
					messageQueues.get(service).add(e);
					q.add(service);
					Future<T> future = new Future<>();
					futureMap.put(e, future);
					return future;
				}
			}
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		// should I check if the microService is already registered???
		this.messageQueues.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		///messageQueues.remove(m); //////// should we remove this????  3.12.2021 q in forum

		for (Class<? extends Event<?>> event : eventMap.keySet()){
			eventMap.get(event).remove(m);
		}

		for (Class<? extends Broadcast> b: broadcastMap.keySet()){
			broadcastMap.get(b).remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		////////////// should we use wait??
		try {
			return messageQueues.get(m).take();

		} catch (InterruptedException e){
			System.out.println(m.getName() + " ");
			throw e;
		}
	}

	public boolean isThereMSRegister(Class<? extends Event<?>> event) {
		synchronized (eventMap) {
			if (!eventMap.get( event ).isEmpty())
				return true;
			else return false;
		}
	}

}
