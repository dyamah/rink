package jp.gr.java_conf.dyama.rink.tools;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.tools.PerformanceMeasuring;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PerformanceMeasuringTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPerformanceMeasuring() {
        PerformanceMeasuring pm = new PerformanceMeasuring();
        assertEquals(true, pm.getTime() >= 0);
        assertEquals(true, pm.getPeakHeapMemorySize() > 0);
        assertEquals(true, pm.getHeapMemorySize() >= 0);
    }

    @Test
    public void testGetTime() throws InterruptedException {
        PerformanceMeasuring pm = new PerformanceMeasuring();
        Thread.sleep(100);
        assertEquals(true, pm.getTime() >= 100);
    }

    @Test
    public void testGetPeakHeapMemorySize() {
        PerformanceMeasuring pm = new PerformanceMeasuring();
        double[] a = new double[1024];
        assertEquals(true, pm.getPeakHeapMemorySize() > 8000);
        assertEquals(1024, a.length);
    }

    @Test
    public void testGetHeapMemorySize() {
        double[] a = new double[1024];
        PerformanceMeasuring pm = new PerformanceMeasuring();
        assertEquals(true, pm.getHeapMemorySize() < 100);
        assertEquals(true, pm.getHeapMemorySize() >= 0);
        assertEquals(1024, a.length);
    }

    @Test
    public void testShow() {
        PerformanceMeasuring pm = new PerformanceMeasuring();
        pm.show(null);
        pm.show(System.err);
    }

}
