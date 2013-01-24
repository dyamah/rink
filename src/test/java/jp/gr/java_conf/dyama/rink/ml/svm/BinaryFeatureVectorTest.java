package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BinaryFeatureVectorTest {

    static final double E = 0.000000001;
    static final int DEFAULT_CAPACITY = 100;

    BinaryFeatureVector.Buffer buffer_ ;
    @Before
    public void setUp() throws Exception {
        buffer_ = new BinaryFeatureVector.Buffer();
        assertEquals(100, DEFAULT_CAPACITY);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBuffer(){
        BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
        assertEquals(0, buffer.size());
    }

    /**
     * [0] id =  0: No Exception
     * [1] id = -1: IllegalArgumentException
     */
    @Test
    public void testBufferAdd(){
        { // [0]
            BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
            assertEquals(0, buffer.size());
            buffer.add(0);
            assertEquals(1, buffer.size());
            buffer.add(7);
            assertEquals(2, buffer.size());
        }

        { // [1]
            BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
            try {
                buffer.add(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the feature ID is negative.", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }
    }


    @Test
    public void testBufferClear(){
        BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
        assertEquals(0, buffer.size());
        buffer.clear();
        assertEquals(0, buffer.size());

        buffer.add(0);
        assertEquals(1, buffer.size());
        buffer.add(7);
        assertEquals(2, buffer.size());

        buffer.clear();
        assertEquals(0, buffer.size());
    }

    @Test
    public void testBufferGetFeatures(){

        {
            BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
            int[] expect = {};
            assertEquals(0, buffer.size());
            int size = 0;
            int[] result = new int[10];
            for(int i : buffer.getFeatures())
                result[size++] = i;

            assertEquals(0, expect.length);
            assertEquals(0, size);
        }

        {
            BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
            int[] expect = {1,2,3,4};
            assertEquals(0, buffer.size());
            buffer.add(3);  buffer.add(3); buffer.add(4); buffer.add(1); buffer.add(2); buffer.add(1);
            assertEquals(4, buffer.size());
            int size = 0;
            int[] result = new int[10];
            for(int i : buffer.getFeatures())
                result[size++] = i;

            assertEquals(4, expect.length);
            assertEquals(4, size);
            for(int i = 0 ; i < size; i++)
                assertEquals(expect[i], result[i]);
        }

    }


    @Test
    public void testBinaryFeatureVector(){
        BinaryFeatureVector fv = new BinaryFeatureVector();
        assertEquals(0, fv.size());
        assertEquals(0.0, fv.getSquareOfL2Norm(), E);
        int[] features = fv.getFeatureIDs();
        assertEquals(DEFAULT_CAPACITY, features.length);
        for(int i = 0 ; i < features.length; i++)
            assertEquals(-1, features[i]);
    }

    @Test
    public void testReset(){
        {
            BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer);
            assertEquals(0, fv.size());
            assertEquals(0.0, fv.getSquareOfL2Norm(), E);
            int[] features = fv.getFeatureIDs();
            assertEquals(DEFAULT_CAPACITY, features.length);
            for(int i = 0 ; i < features.length; i++)
                assertEquals(-1, features[i]);

        }

        {
            BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
            buffer.add(7);  buffer.add(1); buffer.add(4); buffer.add(7);
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer);
            assertEquals(3, fv.size());
            assertEquals(3.0, fv.getSquareOfL2Norm(), E);
            int[] features = fv.getFeatureIDs();
            assertEquals(DEFAULT_CAPACITY, features.length);
            assertEquals(1, features[0]);
            assertEquals(4, features[1]);
            assertEquals(7, features[2]);
            for(int i = 3 ; i < features.length; i++)
                assertEquals(-1, features[i]);
        }

        {
            BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();

            for(int i = 0; i < DEFAULT_CAPACITY; i++ )
                buffer.add(i);
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer);
            assertEquals(DEFAULT_CAPACITY, fv.size());
            assertEquals((double)DEFAULT_CAPACITY, fv.getSquareOfL2Norm(), E);
            int[] features = fv.getFeatureIDs();
            assertEquals(DEFAULT_CAPACITY, features.length);
            for(int i = 0 ; i < features.length; i++)
                assertEquals(i, features[i]);
        }

        {
            BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();

            for(int i = 0; i < DEFAULT_CAPACITY + 10; i++ )
                buffer.add(i);
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer);
            assertEquals(DEFAULT_CAPACITY + 10, fv.size());
            assertEquals((double)(DEFAULT_CAPACITY + 10), fv.getSquareOfL2Norm(), E);
            int[] features = fv.getFeatureIDs();
            assertEquals(DEFAULT_CAPACITY + 10, features.length);
            for(int i = 0 ; i < features.length; i++)
                assertEquals(i, features[i]);

            buffer.clear();
            buffer.add(9);
            fv.reset(buffer);
            assertEquals(1, fv.size());
            assertEquals(1.0, fv.getSquareOfL2Norm(), E);
            features = fv.getFeatureIDs();
            assertEquals(DEFAULT_CAPACITY + 10, features.length);
            assertEquals(9, features[0]);

        }


    }



    @Test
    public void testSize() {
        buffer_.add(1); buffer_.add(0); buffer_.add(3); buffer_.add(2);
        {
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            assertEquals(4, fv.size());
        }

        {
            buffer_.clear();
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            assertEquals(0, fv.size());
        }
    }

    @Test
    public void testGetSquareOfL2Norm() {
        buffer_.add(1); buffer_.add(0); buffer_.add(3); buffer_.add(2);
        {
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            assertEquals(4.0, fv.getSquareOfL2Norm(), E);
        }

        {
            buffer_.clear();
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            assertEquals(0.0, fv.getSquareOfL2Norm(), E);
        }
    }

    /**
     * [0] i = 0 : NoException
     * [1] i = size() - 1 : No Exception
     * [2] i = -1 :  IllegalArgumentException
     * [3] k = size():   IllegalArgumentException
     */
    @Test
    public void testGetFeature() {
        buffer_.add(1); buffer_.add(0); buffer_.add(3); buffer_.add(2);
        jp.gr.java_conf.dyama.rink.ml.FeatureVector.Feature feature ;
        { // [0] [1]
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            assertEquals(4, fv.size());
            feature = fv.getFeature(0);
            assertEquals(0, feature.getID());
            assertEquals(1.0, feature.getValue(), E);

            feature = fv.getFeature(1);
            assertEquals(1, feature.getID());
            assertEquals(1.0, feature.getValue(), E);

            feature = fv.getFeature(2);
            assertEquals(2, feature.getID());
            assertEquals(1.0, feature.getValue(), E);

            feature = fv.getFeature(3);
            assertEquals(3, feature.getID());
            assertEquals(1.0, feature.getValue(), E);

            // [2]

            try {
                fv.getFeature(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

            // [3]
            try {
                fv.getFeature(4);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

        }

        {
            buffer_.clear();
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            assertEquals(0, fv.size());

            try {
                fv.getFeature(0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }


    }

    /**
     * [0] i = 0 : NoException
     * [1] i = size() - 1 : No Exception
     * [2] i = -1 :  IllegalArgumentException
     * [3] k = size():   IllegalArgumentException
     */
    @Test
    public void testGetFeatureID() {
        buffer_.add(1); buffer_.add(0); buffer_.add(7); buffer_.add(2);

        { // [0] [1]
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            assertEquals(4, fv.size());
            assertEquals(0, fv.getFeatureID(0));
            assertEquals(1, fv.getFeatureID(1));
            assertEquals(2, fv.getFeatureID(2));
            assertEquals(7, fv.getFeatureID(3));

            // [2]
            try {
                fv.getFeatureID(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

            // [3]
            try {
                fv.getFeatureID(4);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

        }

        {
            buffer_.clear();
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            assertEquals(0, fv.size());

            try {
                fv.getFeatureID(0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }
    }

    @Test
    public void testGetFeatureIDs() {
        buffer_.add(1); buffer_.add(0); buffer_.add(5); buffer_.add(2);
        {
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            int[] features = fv.getFeatureIDs();
            assertEquals(100, features.length);
            assertEquals(0, features[0]);
            assertEquals(1, features[1]);
            assertEquals(2, features[2]);
            assertEquals(5, features[3]);
            for(int i = 4 ; i < 100 ; i++)
                assertEquals(-1, features[i]);

        }

        {
            buffer_.clear();
            BinaryFeatureVector fv = new BinaryFeatureVector();
            fv.reset(buffer_);
            int[] features = fv.getFeatureIDs();
            assertEquals(100, features.length);
            for(int i = 0 ; i < 100 ; i++)
                assertEquals(-1, features[i]);
        }

    }

}
