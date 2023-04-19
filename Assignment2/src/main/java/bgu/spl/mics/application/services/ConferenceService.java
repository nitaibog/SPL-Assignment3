package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConferenceInformation;
import bgu.spl.mics.application.objects.Model;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */


public class ConferenceService extends MicroService {


    private ConferenceInformation conf;
    private int date;
    private boolean timePassed;



    public ConferenceService(String name, int date) {
        super(name);
        this.date = date;
        this.conf = new ConferenceInformation(name,date);

    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) ->{
            if (date == tick.getCurrTick()){
                this.sendBroadcast(new PublishConfrenceBroadcast(conf.getStudentModelMap()));
                terminate();
            }

        });

        this.subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent e) ->{
            conf.addModel(e.getModel().getStudent(),e.getModel());
            complete(e, "Published");
        });
    }

    public ConferenceInformation getConf() {
        return conf;
    }
}
