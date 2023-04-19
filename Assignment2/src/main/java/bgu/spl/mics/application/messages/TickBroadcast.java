package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private final long duration;
    private long currTick = 1;


    public TickBroadcast(long duration, long currTick){
        this.duration = duration;
        this.currTick = currTick;
    }

    public long getDuration(){
        return duration;
    }
    public long getCurrTick() {
        return currTick;
    }



}
