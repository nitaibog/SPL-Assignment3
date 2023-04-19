package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {
    private GPU gpuTest;
    private GPUService service;
    @BeforeEach
    void setUp() {
        gpuTest = new GPU(GPU.Type.GTX1080,service, "1");
    }

    @AfterEach
    void tearDown() {

    }


    @Test
    void sendDataBatch() {
        Data data = new Data(Data.Type.Images, 10000);
        DataBatch d = new DataBatch(data);
        int prvSize = gpuTest.getUnprocessedSize();
        assertEquals(prvSize-1,gpuTest.getUnprocessedSize());
    }

    @Test
    void processedInsert(DataBatch processedData){
        Data data = new Data(Data.Type.Images, 10000);
        int prvMemory = gpuTest.getMemory();
        int prvQSize = gpuTest.getProcessedSize();
        DataBatch dataBatch = new DataBatch(data);
        gpuTest.processedInsert(dataBatch);
        assertEquals(prvMemory-1,gpuTest.getMemory());
        assertEquals(prvQSize+1,gpuTest.getProcessedSize());
    }

    @Test
    void unProcessedInsert(DataBatch UnprocessedData) {
    }
}