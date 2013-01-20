package jp.gr.java_conf.dyama.rink.ml;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.ml.FeatureVector.Feature;
import jp.gr.java_conf.dyama.rink.ml.FeatureVector.Impl;
import jp.gr.java_conf.dyama.rink.ml.FeatureVector.Impl.Buffer;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FeatureVectorImplTest {

    static final double E = 0.0000001;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testImpl(){
        {
            try {
                new Impl(null);
                fail("");
            } catch (IllegalArgumentException e ){
                assertEquals("the buffer is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        {
            Buffer buffer = new Buffer();
            Impl fv = new Impl(buffer);
            assertEquals(0, fv.size());
            assertEquals(0.0, fv.getSquareOfL2Norm(), E);
        }

        {
            Buffer buffer = new Buffer();
            buffer.add(7, 7.0);
            buffer.add(1, 1.0);
            buffer.add(1, 1.0);
            buffer.add(3, 3.0);
            buffer.add(4, 3.1);

            Impl fv = new Impl(buffer);
            assertEquals(4, fv.size());
            assertEquals(71.61, fv.getSquareOfL2Norm(), E);
            {
                Feature f = fv.getFeature(0);
                assertEquals(1, f.getID());
                assertEquals(2.0, f.getValue(), E);
            }

            {
                Feature f = fv.getFeature(1);
                assertEquals(3, f.getID());
                assertEquals(3.0, f.getValue(), E);
            }

            {
                Feature f = fv.getFeature(2);
                assertEquals(4, f.getID());
                assertEquals(3.1, f.getValue(), E);
            }
            {
                Feature f = fv.getFeature(3);
                assertEquals(7, f.getID());
                assertEquals(7.0, f.getValue(), E);
            }
        }
    }

    @Test
    public void testSize() {
        {
            Buffer buffer = new Buffer();
            Impl fv = new Impl(buffer);
            assertEquals(  0, fv.size());
        }

        {
            Buffer buffer = new Buffer();
            for(int  i = 0 ; i < 7; i ++)
                buffer.add(i,  1.0);
            Impl fv = new Impl(buffer);
            assertEquals(  7, fv.size());
        }

    }

    @Test
    public void testGetSquareOfL2Norm() {
        {
            Buffer buffer = new Buffer();
            Impl fv = new Impl(buffer);
            assertEquals( 0.0, fv.getSquareOfL2Norm(), E);
        }

        {
            Buffer buffer = new Buffer();
            buffer.add( 1,  1.0);
            buffer.add( 2,  3.0);
            buffer.add( 9,  1.0);
            buffer.add( 9,  1.0);
            buffer.add(101, 2.0);
            Impl fv = new Impl(buffer);
            assertEquals( 18.0, fv.getSquareOfL2Norm(), E);
        }
    }

    @Test
    public void testGetFeature() {
        {
            Buffer buffer = new Buffer();
            Impl fv = new Impl(buffer);
            try {
                fv.getFeature(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals(true, e.getMessage().equals("the index is out of range."));
            } catch (Exception e){
                fail(e.getMessage());
            }

            try {
                fv.getFeature(0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals(true, e.getMessage().equals("the index is out of range."));
            } catch (Exception e){
                fail(e.getMessage());
            }
        }

        {
            Buffer buffer = new Buffer();
            for(int  i = 0 ; i < 7; i ++)
                buffer.add(i,  (double)i);

            buffer.add(2, 1.1);
            buffer.add(6, -6.1);

            Impl fv = new Impl(buffer);

            try {
                fv.getFeature(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals(true, e.getMessage().equals("the index is out of range."));
            } catch (Exception e){
                fail(e.getMessage());
            }

            Feature f = null;
            f = fv.getFeature(0); assertEquals(0, f.getID()); assertEquals(0.0, f.getValue(), E);
            f = fv.getFeature(1); assertEquals(1, f.getID()); assertEquals(1.0, f.getValue(), E);
            f = fv.getFeature(2); assertEquals(2, f.getID()); assertEquals(3.1, f.getValue(), E);
            f = fv.getFeature(3); assertEquals(3, f.getID()); assertEquals(3.0, f.getValue(), E);
            f = fv.getFeature(4); assertEquals(4, f.getID()); assertEquals(4.0, f.getValue(), E);
            f = fv.getFeature(5); assertEquals(5, f.getID()); assertEquals(5.0, f.getValue(), E);
            f = fv.getFeature(6); assertEquals(6, f.getID()); assertEquals(-0.1, f.getValue(), E);

            try {
                fv.getFeature(7);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals(true, e.getMessage().equals("the index is out of range."));
            } catch (Exception e){
                fail(e.getMessage());
            }
        }
    }
}
