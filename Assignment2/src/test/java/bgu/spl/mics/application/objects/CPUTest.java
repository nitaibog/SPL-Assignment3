package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    private CPU cpuTest;
    private CPUService s;
    @BeforeEach
    void setUp() {
       cpuTest = new CPU("1", 32,s);
    }

    @AfterEach
    void tearDown() {
        cpuTest = null;
    }

    @Test
    void addData() {
        Data data = new Data(Data.Type.Images, 10000);
         int prvSize = cpuTest.getDataSize();
         DataBatch dataBatch = new DataBatch(data);
         cpuTest.addData(dataBatch);
         assertEquals(prvSize+1, cpuTest.getDataSize());
    }

    @Test
    void processes() {

    }

    @Test
    void send() {
        Data data = new Data(Data.Type.Images, 10000);
        DataBatch dataBatch = new DataBatch(data);
        int prvSize = cpuTest.getDataSize();
        cpuTest.send();
        assertEquals(prvSize-1,cpuTest.getDataSize());
    }
}