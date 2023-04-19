package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private final long speed;
	private final long duration;
	private int currTime = 1;


	public TimeService(long speed, long duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
	}


	@Override
	protected synchronized void initialize() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
//				timer.schedule( this, speed );
				sendBroadcast(new TickBroadcast(duration,currTime));
				currTime++;
				if (currTime >= duration) {
					sendBroadcast( new TerminateBroadcast() );
					timer.cancel();
				}
			}
		};
		timer.scheduleAtFixedRate(task,speed,speed);

//		for (int i = 0; i < duration; i++){
//			try {
//				Thread.sleep(speed);
//			} catch (InterruptedException ignored){}
//		}

//		while (currTime <= duration)
//			task.run();

		subscribeBroadcast( TerminateBroadcast.class, (TerminateBroadcast b) -> {
			terminate();
		});
	}
	public int getTime(){
		return currTime;
	}

}
