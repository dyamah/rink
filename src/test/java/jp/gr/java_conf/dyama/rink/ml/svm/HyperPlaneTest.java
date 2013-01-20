package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.lang.reflect.Field;

import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.HyperPlane;

public class HyperPlaneTest {

    Field positive_ ;
    Field negative_ ;
    Field bias_ ;
    @Before
    public void setUp() throws Exception {
        positive_ = HyperPlane.class.getDeclaredField("positive_");
        positive_.setAccessible(true);

        negative_ = HyperPlane.class.getDeclaredField("negative_");
        negative_.setAccessible(true);

        bias_ = HyperPlane.class.getDeclaredField("bias_");
        bias_.setAccessible(true);
    }

    static final double E = 0.000001;
    /**
     * [0] pos =  0, neg =  1, bias = 1.0 : No Exception
     * [1] pos = -1, neg =  1, bias = 1.0 : IllegalArgumentException
     * [2] pos =  0, neg = -1, bias = 1.0 : IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @Test
    public void testHyperPlane() throws IllegalArgumentException, IllegalAccessException {
        { // [0]
            HyperPlane hp = new HyperPlane(0, 1, 1.0);
            assertEquals(0, positive_.getInt(hp));
            assertEquals(1, negative_.getInt(hp));
            assertEquals(1.0, bias_.getDouble(hp), E);
        }

        { // [1]
            try {
                new HyperPlane(-1, 1, 1.0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the positive ID is negative.", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        { // [2]
            try {
                new HyperPlane(0, -1, 1.0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the negative ID is equal to the positive ID and fewer.", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        { // [2]
            try {
                new HyperPlane(1, 1, 1.0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the negative ID is equal to the positive ID and fewer.", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            HyperPlane hp = new HyperPlane(2, 7, 1.9);
            assertEquals(2, positive_.getInt(hp));
            assertEquals(7, negative_.getInt(hp));
            assertEquals(1.9, bias_.getDouble(hp), E);
        }
    }
}
