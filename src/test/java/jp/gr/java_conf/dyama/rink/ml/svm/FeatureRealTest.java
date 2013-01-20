package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import org.junit.Before;

import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.Feature;
import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.Feature.Real.Value;

import org.junit.Test;

public class FeatureRealTest {

    static final double E = 0.000001;

    Field id_ ;
    Field size_ ;
    Field values_ ;
    @Before
    public void setUp() throws Exception {
        id_ = Feature.Real.class.getDeclaredField("id_");
        id_.setAccessible(true);


        values_ = Feature.Real.class.getDeclaredField("values_");
        values_.setAccessible(true);

        size_ = Feature.Real.class.getDeclaredField("size_");
        size_.setAccessible(true);
    }

    /**
     * [0] id =  0, capacity = 3: No Exception
     * [1] id = -1, capacity = 3: IllegalArugmentException
     * [2] id =  2, capacity = 1: No Exception
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @Test
    public void testFeatureReal() throws IllegalArgumentException, IllegalAccessException {
        { // [0]
            Feature.Real rf = new Feature.Real(0, 3);
            assertEquals(0, id_.getInt(rf));
            assertEquals(0, size_.getInt(rf));
            Value[] values = (Value[])values_.get(rf);
            assertEquals(3, values.length);
            for(int i = 0 ; i < values.length; i++)
                assertEquals(true, values[i] == null);
        }

        { // [1]

            try {
                new Feature.Real(-1, 3);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the feature ID is negative.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // [2]
            Feature.Real rf = new Feature.Real(2, -1);
            assertEquals(2, id_.getInt(rf));
            assertEquals(0, size_.getInt(rf));
            Value[] values = (Value[]) values_.get(rf);
            assertEquals(10, values.length);
            for(int i = 0 ; i < values.length; i++)
                assertEquals(true, values[i] == null);
        }
    }


    @Test
    public void testAddValue() throws IllegalArgumentException, IllegalAccessException {
        Feature.Real rf = new Feature.Real(2, 2);
        Value[] values = (Value[]) values_.get(rf);
        assertEquals(0, size_.getInt(rf));
        assertEquals(2, values.length);
        Value v = null;

        v = values[0]; assertEquals(true, v == null);
        v = values[1]; assertEquals(true, v == null);

        rf.addValue(0, 1);
        assertEquals(1, size_.getInt(rf));
        assertEquals(2, values.length);

        v = values[0]; assertEquals(0.0, v.getValue(), E); assertEquals(1, v.getSvID());
        v = values[1]; assertEquals(true, v == null);

        try {
            rf.addValue(1.1, -1);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the support vector ID is negative.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        rf.addValue(1, 2);
        assertEquals(2, size_.getInt(rf));
        assertEquals(2, values.length);

        v = values[0]; assertEquals(0.0, v.getValue(), E); assertEquals(1, v.getSvID());
        v = values[1]; assertEquals(1.0, v.getValue(), E); assertEquals(2, v.getSvID());

        rf.addValue(2, 3);
        assertEquals(3, size_.getInt(rf));
        assertEquals(2, values.length);
        values = (Value[]) values_.get(rf);
        assertEquals(12, values.length);

        v = values[0]; assertEquals(0.0, v.getValue(), E); assertEquals(1, v.getSvID());
        v = values[1]; assertEquals(1.0, v.getValue(), E); assertEquals(2, v.getSvID());
        v = values[2]; assertEquals(2.0, v.getValue(), E); assertEquals(3, v.getSvID());
    }
}
