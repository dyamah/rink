package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl;

import org.junit.Test;

public class SVCoefficientTest {
    static final double E = 0.0000001;

    @Test
    public void testSVCoefficient(){

        try {
            new ClassifierImpl.SVCoefficient(-1, 0, 2);
            fail("");
        } catch (AssertionError e){
            assertEquals(null, e.getMessage());
        } catch (Exception e ){
            fail("");
        }

        try {
            new ClassifierImpl.SVCoefficient(0, -0.00001, 2);
            fail("");
        } catch (AssertionError e){
            assertEquals(null, e.getMessage());
        } catch (Exception e ){
            fail("");
        }

        try {
            new ClassifierImpl.SVCoefficient(0, 0.0, 0);
            fail("");
        } catch (AssertionError e){
            assertEquals(null, e.getMessage());
        } catch (Exception e ){
            fail("");
        }

        new ClassifierImpl.SVCoefficient(0, 0.0, 1);
    }

    @Test
    public void testAddCoefficient(){
        ClassifierImpl.SVCoefficient c = new ClassifierImpl.SVCoefficient(0, 0.0, 3);
        try {
            c.addCoefficient(-1, 0.1);
            fail("");
        } catch (AssertionError e){
            assertEquals(null, e.getMessage());
        } catch (Exception e ){
            fail("");
        }

        c.addCoefficient(0, 0.1);
        c.addCoefficient(1, 0.1);
        c.addCoefficient(2, 0.1);

        try {
            c.addCoefficient(3, 0.1);
            fail("");
        } catch (AssertionError e){
            assertEquals(null, e.getMessage());
        } catch (Exception e ){
            fail("");
        }



    }

}
