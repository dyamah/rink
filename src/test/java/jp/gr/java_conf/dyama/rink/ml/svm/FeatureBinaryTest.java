package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.Feature;

public class FeatureBinaryTest {

    Field id_ ;
    Field size_ ;
    Field svids_ ;
    @Before
    public void setUp() throws Exception {
        id_ = Feature.Binary.class.getDeclaredField("id_");
        id_.setAccessible(true);


        svids_ = Feature.Binary.class.getDeclaredField("svids_");
        svids_.setAccessible(true);

        size_ = Feature.Binary.class.getDeclaredField("size_");
        size_.setAccessible(true);
    }

    /**
     * [0] id =  0, capacity = 3 : No Exception
     * [1] id = -1, capacity = 3 : IllegalArgumentException
     * [2] id = -1, capacity = -1 : No Exception
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @Test
    public void testFeatureBinary() throws Exception {


        { // [0]
            Feature.Binary bf = new Feature.Binary(0, 3);
            assertEquals(0, id_.getInt(bf));
            assertEquals(0, size_.getInt(bf));
            int[] svids = (int[]) svids_.get(bf);
            assertEquals(3, svids.length);
            for(int i = 0; i < svids.length; i++)
                assertEquals(-1, svids[i]);
        }

        { // [1]
            try {
                new Feature.Binary(-1, 3);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the feature ID is negative.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // [2]
            Feature.Binary bf = new Feature.Binary(7, 3);
            assertEquals(7, id_.getInt(bf));
            assertEquals(0, size_.getInt(bf));
            int[] svids = (int[]) svids_.get(bf);
            assertEquals(3, svids.length);
            for(int i = 0; i < svids.length; i++)
                assertEquals(-1, svids[i]);
        }

        { // [3]
            Feature.Binary bf = new Feature.Binary(7, -1);
            assertEquals(7, id_.getInt(bf));
            assertEquals(0, size_.getInt(bf));
            int[] svids = (int[]) svids_.get(bf);

            assertEquals(10, svids.length);
            for(int i = 0; i < svids.length; i++)
                assertEquals(-1, svids[i]);
        }
    }

    @Test
    public void testAddSvID() throws IllegalArgumentException, IllegalAccessException {
        Feature.Binary bf = new Feature.Binary(0, 3);

        assertEquals(0, size_.getInt(bf));
        int[] svids = (int[]) svids_.get(bf);

        assertEquals(3, svids.length);
        for(int i = 0; i < svids.length; i++)
            assertEquals(-1, svids[i]);



        bf.addSvID(0);
        assertEquals(1, size_.getInt(bf));
        assertEquals( 0, svids[0]);
        assertEquals(-1, svids[1]);
        assertEquals(-1, svids[2]);

        try {
            bf.addSvID(-1);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the support vector ID is negative.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        bf.addSvID(1);
        assertEquals(2, size_.getInt(bf));
        assertEquals( 0, svids[0]);
        assertEquals( 1, svids[1]);
        assertEquals(-1, svids[2]);

        bf.addSvID(2);
        assertEquals(3, size_.getInt(bf));
        assertEquals( 0, svids[0]);
        assertEquals( 1, svids[1]);
        assertEquals( 2, svids[2]);

        bf.addSvID(3);
        svids = (int[]) svids_.get(bf);
        assertEquals(4, size_.getInt(bf));
        assertEquals( 0, svids[0]);
        assertEquals( 1, svids[1]);
        assertEquals( 2, svids[2]);
        assertEquals( 3, svids[3]);

    }
}
