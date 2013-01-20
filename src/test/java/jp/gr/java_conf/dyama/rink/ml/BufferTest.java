package jp.gr.java_conf.dyama.rink.ml;

import static org.junit.Assert.*;

import java.util.Map;

import jp.gr.java_conf.dyama.rink.ml.FeatureVector.Impl.Buffer;

import org.junit.Test;

public class BufferTest {

    static final double E = 0.00000001;

    @Test
    public void testFeatureBuffer() {
        Buffer buffer = new Buffer();
        assertEquals(0, buffer.size());
    }

    /**
     * [0] id =  1, value = 2.1: No Exception
     * [1] id =  0, value = 3.1: No Exception
     * [2] id = -1, value = 4.1: IlleaglArgumentException
     *
     */
    @Test
    public void testAdd() {
        { //[0]
            Buffer buffer = new Buffer();
            assertEquals(0, buffer.size());
            buffer.add(1,  2.1);
            assertEquals(1, buffer.size());
            {
                Map<Integer, Double> b = buffer.getBuffer();
                assertEquals(2.1, (double)b.get(1), E);
            }

            buffer.add(2,  3.1);
            assertEquals(2, buffer.size());
            {
                Map<Integer, Double> b = buffer.getBuffer();
                assertEquals(2.1, (double)b.get(1), E);
                assertEquals(3.1, (double)b.get(2), E);
            }

            buffer.add(3,  4.1);
            assertEquals(3, buffer.size());
            {
                Map<Integer, Double> b = buffer.getBuffer();
                assertEquals(2.1, (double)b.get(1), E);
                assertEquals(3.1, (double)b.get(2), E);
                assertEquals(4.1, (double)b.get(3), E);
            }

            buffer.add(2, -1.1);
            assertEquals(3, buffer.size());
            {
                Map<Integer, Double> b = buffer.getBuffer();
                assertEquals(2.1, (double)b.get(1), E);
                assertEquals(2.0, (double)b.get(2), E);
                assertEquals(4.1, (double)b.get(3), E);
            }

            buffer.add(2, 0.7);
            assertEquals(3, buffer.size());
            {
                Map<Integer, Double> b = buffer.getBuffer();
                assertEquals(2.1, (double)b.get(1), E);
                assertEquals(2.7, (double)b.get(2), E);
                assertEquals(4.1, (double)b.get(3), E);
            }
        }
        { // [1]
            Buffer buffer = new Buffer();
            assertEquals(0, buffer.size());
            buffer.add(0,  1.1);
            assertEquals(1, buffer.size());
            {
                Map<Integer, Double> b = buffer.getBuffer();
                assertEquals(1.1, (double)b.get(0), E);
            }
        }

        { // [2]
            Buffer buffer = new Buffer();
            assertEquals(0, buffer.size());
            try {
                buffer.add(-1,  1.1);
                fail("");
            } catch (IllegalArgumentException e ){
                assertEquals(true, e.getMessage().equals("the id is negative."));
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void testClear() {
        Buffer buffer = new Buffer();
        assertEquals(0, buffer.size());
        buffer.clear();
        assertEquals(0, buffer.size());
        assertEquals(0, buffer.getBuffer().size());

        buffer.add(1, 1.0);
        buffer.add(2, 2.0);
        assertEquals(2, buffer.size());
        buffer.clear();
        assertEquals(0, buffer.size());
        assertEquals(0, buffer.getBuffer().size());
    }

    @Test
    public void testSize() {
        Buffer buffer = new Buffer();
        assertEquals(0, buffer.size());
        buffer.add(1, 1.0);
        assertEquals(1, buffer.size());
        buffer.add(2, 2.0);
        assertEquals(2, buffer.size());
        buffer.add(3, 3.0);
        assertEquals(3, buffer.size());
        buffer.clear();
        assertEquals(0, buffer.size());
    }

    @Test
    public void testGetBuffer() {
        Buffer buffer = new Buffer();
        Map<Integer, Double> a = buffer.getBuffer();
        buffer.add(1, 1.0);
        buffer.add(2, 1.0);
        Map<Integer, Double> b = buffer.getBuffer();
        buffer.add(3, 1.0);
        Map<Integer, Double> c = buffer.getBuffer();
        assertEquals(true, a == b);
        assertEquals(true, a == c);
        assertEquals(true, b == c);
    }

}
