package bgu.spl.mics;

import bgu.spl.mics.application.messages.DataPreProcessEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class MessageBusImplTest {

    private MessageBus bus;
    private MicroService m1;
    private MicroService m2;
    private Broadcast b;
    private Event<String> e1;
    private Event<String> e2;
    private Model model;
    private Data data;
    private Student student;
    private Model[] models;
    @BeforeEach
    void setUp() {
        bus =  MessageBusImpl.getInstance();
        model = new Model("model",student, Data.Type.Images,100 );
        student = new Student("",",", Student.Degree.PhD,models);
        m1 = new GPUService("m1", GPU.Type.GTX1080);
        m2 = new CPUService("", 32);
        b = new TickBroadcast(2000,1);
        e1 = new DataPreProcessEvent("");
        e2 = new TestModelEvent("", model, Student.Degree.PhD);

    }

    @AfterEach
    void tearDown() {
        bus.unregister(m1);
        bus.unregister(m2);
    }

    @Test
    void subscribeEvent() {

    }

    @Test
    void subscribeBroadcast() {

    }

    @Test
    void complete() {
        bus.register(m2);
        bus.subscribeEvent((Class)e1.getClass(),m2);
        Future<String> future  = bus.sendEvent(e1);
        bus.complete(e1,"true");
        assertEquals(future.get(),"true");
    }

    @Test
    void sendBroadcast() {
        bus.register(m1);
        bus.register(m2);
        bus.subscribeBroadcast(b.getClass(),m1);
        bus.subscribeBroadcast(b.getClass(),m2);
        bus.sendBroadcast(b);
        try {
            assertEquals(b, bus.awaitMessage(m1));
            assertEquals(b, bus.awaitMessage(m2));
        } catch (Exception e){
            fail();
        }

    }

    @Test
    void sendEvent() {
        bus.register(m1);
        bus.register(m2);
        bus.subscribeEvent((Class)e1.getClass(),m2);
        bus.subscribeEvent((Class)e2.getClass(),m1);
        bus.sendEvent(e1);
        bus.sendEvent(e2);
        try{
            assertEquals(e1,bus.awaitMessage(m2));
            assertEquals(e2,bus.awaitMessage(m1));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void register() {
    }

    @Test
    void unregister() {
    }

    @Test
    void awaitMessage() {
        bus.register(m1);
        bus.subscribeBroadcast((Class)b.getClass(),m1);
        bus.sendBroadcast(b);
        try {
            assertEquals(b, bus.awaitMessage(m1));
        } catch (Exception e){
            fail();
        }

    }
}