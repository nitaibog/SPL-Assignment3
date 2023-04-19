package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {

    private Future<String> future;

    @BeforeEach
    void setUp() {
        future = new Future<>();
    }

    @AfterEach
    void tearDown() {
        future = null;
    }

    @Test
    void get() {
        assertFalse(future.isDone());
        future.resolve("");
        assertTrue(future.isDone());
        assertEquals(future.get(),"");
    }

    @Test
    void resolve() {
        String s = "res";
        future.resolve(s);
        assertTrue(future.isDone());
        assertEquals(future.get(), s);
    }

    @Test
    void isDone() {
        assertFalse(future.isDone());
        future.resolve("");
        assertTrue(future.isDone());
    }

    @Test
    void testGet() {
        assertFalse(future.isDone());
        future.get(100, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.resolve("test");
        assertEquals(future.get(100,TimeUnit.MILLISECONDS),"test");
    }

}