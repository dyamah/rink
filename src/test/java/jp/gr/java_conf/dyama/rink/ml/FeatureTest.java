/**
 *
 */
package jp.gr.java_conf.dyama.rink.ml;

import static org.junit.Assert.*;

import org.junit.Test;

import jp.gr.java_conf.dyama.rink.ml.FeatureVector.Feature;

/**
 * @author Hiroyasu Yamada
 *
 */
public class FeatureTest {
    static final double DELTA = 0.0000000000001;
    /**
     * [0] id = 1, value = -2.0: No Exception
     * [1] id = 0, value = 0: No Exception
     * [2] id = -1, value 0: IllegalArgumentException
     */
    @Test
    public void testCreate() {
        { // [0]
            Feature f = new Feature(1,  -2.0);
            assertEquals(1, f.getID());
            assertEquals(-2.0, f.getValue(), DELTA);
        }

        { // [1]
            Feature f = new Feature(0,  0.0);
            assertEquals(0, f.getID());
            assertEquals(0.0, f.getValue(), DELTA);
        }

        { // [2]
            try {
                new Feature(-1,  0.0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals(true, e.getMessage().equals("the id is negative."));
            } catch (Exception e){
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void testGetID() {
        Feature f = new Feature(4,  1.11);
        assertEquals(4, f.getID());
    }

    @Test
    public void testGetValue() {
        Feature f = new Feature(4,  2.11);
        assertEquals(2.11, f.getValue(), DELTA);
    }

    /**
     * [0] id =  0, value  = -1.2 : No Exception
     * [1] id = -1, value  = -1.2 : IllegalArgumentException
     */
    @Test
    public void testSet() {
        { // [0]
            Feature f = new Feature(3,  2.11);
            assertEquals(3,    f.getID());
            assertEquals(2.11, f.getValue(), DELTA);

            f.set(0,  -1.2);
            assertEquals(0,    f.getID());
            assertEquals(-1.2, f.getValue(), DELTA);
        }
        {
            Feature f = new Feature(4,  2.11);
            assertEquals(4,    f.getID());
            assertEquals(2.11, f.getValue(), DELTA);

            try {
                f.set(-1,  -1.2);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals(true, e.getMessage().equals("the id is negative."));
            } catch (Exception e){
                fail(e.getMessage());
            }
        }
    }

}
