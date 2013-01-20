package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.ml.svm.LearnerImpl;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LearnerImplTest {
    static final double E = 0.0000001;
    BinaryFeatureVector.Buffer buffer_ ;
    @Before
    public void setUp() throws Exception {
        buffer_ = new BinaryFeatureVector.Buffer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLearnerImpl() {
        new LearnerImpl.BinarySpace();
    }

    @Test
    public void testMakeSvmProblem() {

        LearnerImpl.BinarySpace learner = new LearnerImpl.BinarySpace();
        assertEquals(null, learner.makeSvmProblem());

        {
            buffer_.clear();
            buffer_.add(1); buffer_.add(2);
            BinaryFeatureVector x = new BinaryFeatureVector(); x.reset(buffer_);
            learner.addExample(x, 1);
        }

        assertEquals(null, learner.makeSvmProblem());

        {
            buffer_.clear();
            buffer_.add(2); buffer_.add(3);
            BinaryFeatureVector x = new BinaryFeatureVector(); x.reset(buffer_);
            learner.addExample(x, 1);

        }
        assertEquals(null, learner.makeSvmProblem());

        {
            buffer_.clear();
            buffer_.add(4); buffer_.add(3);
            BinaryFeatureVector x = new BinaryFeatureVector(); x.reset(buffer_);
            learner.addExample(x, -1);
        }

        libsvm.svm_problem prob = learner.makeSvmProblem();
        assertEquals(true, prob != null);
        assertEquals(3, prob.l);
        assertEquals(3, prob.x.length);
        assertEquals(2, prob.x[0].length);
        assertEquals(1, prob.x[0][0].index); assertEquals(2, prob.x[0][1].index);
        assertEquals(1.0, prob.x[0][0].value, E); assertEquals(1.0, prob.x[0][1].value, E);

        assertEquals(2, prob.x[1].length);
        assertEquals(2, prob.x[1][0].index); assertEquals(3, prob.x[1][1].index);
        assertEquals(1.0, prob.x[1][0].value, E); assertEquals(1.0, prob.x[1][1].value, E);

        assertEquals(2, prob.x[2].length);
        assertEquals(3, prob.x[2][0].index); assertEquals(4, prob.x[2][1].index);
        assertEquals(1.0, prob.x[2][0].value, E); assertEquals(1.0, prob.x[2][1].value, E);

        assertEquals(3, prob.y.length);
        assertEquals( 1.0, prob.y[0], E);
        assertEquals( 1.0, prob.y[1], E);
        assertEquals(-1.0, prob.y[2], E);
    }

    @Test
    public void testConvert() {
        LearnerImpl.BinarySpace learner = new LearnerImpl.BinarySpace();
        Parameters params = new Parameters.ParametersImpl();
        params.setC(7.1);
        params.setCacheSize(47);
        params.setDegree(9);
        params.setGamma(2.99);
        params.setCoef0(0.11);
        params.setEpsilon(9.1);
        params.setKernelType(Parameters.KernelType.SIGMOID);

        try {
            learner.convert(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the parameters is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        libsvm.svm_parameter result = learner.convert(params);

        assertEquals(   7.1, result.C, E);
        assertEquals(  47.0, result.cache_size, E);
        assertEquals(     9, result.degree);
        assertEquals(  2.99, result.gamma, E);
        assertEquals(  0.11, result.coef0, E);
        assertEquals(   9.1, result.eps, E);
        assertEquals(libsvm.svm_parameter.SIGMOID, result.kernel_type);
        assertEquals(libsvm.svm_parameter.C_SVC, result.svm_type);
    }

    @Test
    public void testTrain() {
        LearnerImpl.BinarySpace learner = new LearnerImpl.BinarySpace();
        Parameters params = new Parameters.ParametersImpl();

        try {
            learner.train(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the parameters is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        try {
            learner.train(params);
            fail("");
        } catch (IllegalStateException e){
            assertEquals("fail to create training examples.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testAddExample(){

        {
            LearnerImpl.BinarySpace learner = new LearnerImpl.BinarySpace();
            try {
                learner.addExample(null, 1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the feature vector is null.", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            LearnerImpl.BinarySpace learner = new LearnerImpl.BinarySpace();
            buffer_.clear();
            BinaryFeatureVector fv = new BinaryFeatureVector(); fv.reset(buffer_);

            learner.addExample(fv,  1);
            learner.addExample(fv,  2);
            learner.addExample(fv, -1);

            libsvm.svm_problem prob = learner.makeSvmProblem();
            assertEquals(3, prob.l); assertEquals(3, prob.x.length); assertEquals(3, prob.y.length);
            assertEquals(0, prob.x[0].length);
            assertEquals(0, prob.x[1].length);
            assertEquals(0, prob.x[2].length);
            assertEquals( 1.0, prob.y[0], E);
            assertEquals( 2.0, prob.y[1], E);
            assertEquals(-1.0, prob.y[2], E);
        }

        {
            LearnerImpl.BinarySpace learner = new LearnerImpl.BinarySpace();
            {
                buffer_.clear();
                buffer_.add(7); buffer_.add(2); buffer_.add(1); buffer_.add(7);
                BinaryFeatureVector fv = new BinaryFeatureVector(); fv.reset(buffer_);
                learner.addExample(fv,  1);
            }

            {
                buffer_.clear();
                buffer_.add(1); buffer_.add(2); buffer_.add(1);
                BinaryFeatureVector fv = new BinaryFeatureVector(); fv.reset(buffer_);
                learner.addExample(fv,  9);
            }


            libsvm.svm_problem prob = learner.makeSvmProblem();
            assertEquals(2, prob.l); assertEquals(2, prob.x.length); assertEquals(2, prob.y.length);
            assertEquals(3, prob.x[0].length);
            assertEquals(1, prob.x[0][0].index); assertEquals(2, prob.x[0][1].index); assertEquals(7, prob.x[0][2].index);
            for(int i = 0 ; i < prob.x[0].length; i++)
                assertEquals(1.0, prob.x[0][i].value, E);
            assertEquals(2, prob.x[1].length);
            assertEquals(1, prob.x[1][0].index); assertEquals(2, prob.x[1][1].index);
            for(int i = 0 ; i < prob.x[1].length; i++)
                assertEquals(1.0, prob.x[1][i].value, E);

            assertEquals( 1.0, prob.y[0], E);
            assertEquals( 9.0, prob.y[1], E);
        }
    }

    @Test
    public void testLearn(){
        LearnerImpl.BinarySpace learner = new LearnerImpl.BinarySpace();
        Parameters params = new Parameters.ParametersImpl();

        try {
            learner.learn(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the parameters is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        try {
            learner.learn(params);
            fail("");
        } catch (IllegalStateException e){
            assertEquals("fail to create training examples.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testGetSizeOfSetOfLabelsDefaultLabel(){
        buffer_.add(1); buffer_.add(4); buffer_.add(9);
        BinaryFeatureVector fv = new BinaryFeatureVector(); fv.reset(buffer_);

        LearnerImpl.BinarySpace learner = new LearnerImpl.BinarySpace();

        assertEquals(0, learner.getSizeOfSetOfLabels());
        assertEquals(null, learner.getDefaultLabel());
        assertEquals(0, learner.getSizeOfExamples());

        learner.addExample(fv, 1);
        assertEquals(1, learner.getSizeOfSetOfLabels());
        assertEquals(1, (int)learner.getDefaultLabel());
        assertEquals(1, learner.getSizeOfExamples());

        learner.addExample(fv, 1);
        assertEquals(1, learner.getSizeOfSetOfLabels());
        assertEquals(1, (int)learner.getDefaultLabel());
        assertEquals(2, learner.getSizeOfExamples());

        learner.addExample(fv, 0);
        assertEquals(2, learner.getSizeOfSetOfLabels());
        assertEquals(1, (int)learner.getDefaultLabel());
        assertEquals(3, learner.getSizeOfExamples());

        learner.addExample(fv, 0);
        assertEquals(2, learner.getSizeOfSetOfLabels());
        assertEquals(1, (int)learner.getDefaultLabel());
        assertEquals(4, learner.getSizeOfExamples());

        learner.addExample(fv, 0);
        assertEquals(2, learner.getSizeOfSetOfLabels());
        assertEquals(0, (int)learner.getDefaultLabel());
        assertEquals(5, learner.getSizeOfExamples());

        learner.addExample(fv, -1);
        assertEquals(3, learner.getSizeOfSetOfLabels());
        assertEquals(0, (int)learner.getDefaultLabel());
        assertEquals(6, learner.getSizeOfExamples());

        learner.addExample(fv, -1);
        assertEquals(3, learner.getSizeOfSetOfLabels());
        assertEquals(0, (int)learner.getDefaultLabel());
        assertEquals(7, learner.getSizeOfExamples());

        learner.addExample(fv, -1);
        assertEquals(3, learner.getSizeOfSetOfLabels());
        assertEquals(0, (int)learner.getDefaultLabel());
        assertEquals(8, learner.getSizeOfExamples());

        learner.addExample(fv, 1);
        assertEquals(3, learner.getSizeOfSetOfLabels());
        assertEquals(1, (int)learner.getDefaultLabel());
        assertEquals(9, learner.getSizeOfExamples());


    }

}
