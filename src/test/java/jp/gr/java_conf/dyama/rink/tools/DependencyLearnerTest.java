package jp.gr.java_conf.dyama.rink.tools;

import static org.junit.Assert.*;


import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.tools.DependencyLearner;


import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DependencyLearnerTest {
    static final double E = 0.0001;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDependencyLearner() {
        DependencyLearner learner = new DependencyLearner();
        assertNull(learner.model_);
        assertNull(learner.train_);
        assertNull(learner.dic_);
        assertNull(learner.parser_);
        assertNull(learner.reader_);
        assertNotNull(learner.opts_);

        assertNotNull(learner.params_);
        assertEquals(Parameters.KernelType.POLYNOMIAL, learner.params_.getKernelType());
        assertEquals(2, learner.params_.getDegree());
        assertEquals(1.0, learner.params_.getGamma(), E);
        assertEquals(1.0, learner.params_.getCoef0(), E);
        assertEquals(1.0, learner.params_.getC(), E);
        assertEquals(0.001, learner.params_.getEpsilon(), E);
        assertEquals(30.0, learner.params_.getCacheSize(), E);

        assertEquals(0, learner.num_sentences_);
        assertEquals(0, learner.steps_);
    }

    @Test
    public void testParseCommandLineArguments() {
        { // -h
            String[] args = {"-h", "-i", "samples/train.txt", "-o", "samples/sample.model"};
            DependencyLearner learner = new DependencyLearner();
            try {
                boolean f = learner.parseCommandLineArguments(args);
                assertEquals(false, f);
                assertNull(learner.model_);
                assertNull(learner.train_);
                assertNull(learner.dic_);
                assertNull(learner.parser_);
                assertNull(learner.reader_);
                assertNotNull(learner.opts_);

                assertNotNull(learner.params_);
                assertEquals(Parameters.KernelType.POLYNOMIAL, learner.params_.getKernelType());
                assertEquals(2, learner.params_.getDegree());
                assertEquals(1.0, learner.params_.getGamma(), E);
                assertEquals(1.0, learner.params_.getCoef0(), E);
                assertEquals(1.0, learner.params_.getC(), E);
                assertEquals(0.001, learner.params_.getEpsilon(), E);
                assertEquals(30.0, learner.params_.getCacheSize(), E);

                assertEquals(0, learner.num_sentences_);
                assertEquals(0, learner.steps_);

            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // valid
            String[] args = {"-i", "samples/train.txt", "-o", "samples/sample.model"};
            DependencyLearner learner = new DependencyLearner();
            try {
                boolean f = learner.parseCommandLineArguments(args);
                assertEquals(true, f);
                assertNotNull(learner.model_); assertEquals("samples/sample.model", learner.model_.getPath());
                assertNotNull(learner.train_); assertEquals("samples/train.txt", learner.train_.getPath());
                assertNull(learner.dic_);
                assertNotNull(learner.parser_);
                assertNotNull(learner.reader_);
                assertNotNull(learner.opts_);

                assertNotNull(learner.params_);
                assertEquals(Parameters.KernelType.POLYNOMIAL, learner.params_.getKernelType());
                assertEquals(2, learner.params_.getDegree());
                assertEquals(1.0, learner.params_.getGamma(), E);
                assertEquals(1.0, learner.params_.getCoef0(), E);
                assertEquals(1.0, learner.params_.getC(), E);
                assertEquals(0.001, learner.params_.getEpsilon(), E);
                assertEquals(30.0, learner.params_.getCacheSize(), E);

                assertEquals(0, learner.num_sentences_);
                assertEquals(0, learner.steps_);

            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // no -i
            String[] args = {"-o", "samples/sample.model"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals("not found the option -i.", e.getMessage());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -i directory
            String[] args = {"-i", "samples", "-o", "samples/sample.model"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals("samples is a directory.", e.getMessage());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -i no existing file
            String[] args = {"-i", "samples/XXX", "-o", "samples/sample.model"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertNotNull(e.getMessage());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // no -o
            String[] args = {"-i", "samples/train.txt"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals("not found the option -o.", e.getMessage());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -o directory
            String[] args = {"-i", "samples/train.txt", "-o", "samples"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals("samples is a directory.", e.getMessage());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -o no existing path
            String[] args = {"-i", "samples/train.txt", "-o", "samples/X/hoge.model"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals("can not write to samples/X/hoge.model", e.getMessage());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -l SVM
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model",  "-l", "SVM"};
            DependencyLearner learner = new DependencyLearner();
            try {

                assertEquals(true, learner.parseCommandLineArguments(args));
                assertNotNull(learner.model_); assertEquals("samples/hoge.model", learner.model_.getPath());
                assertNotNull(learner.train_); assertEquals("samples/train.txt", learner.train_.getPath());
                assertNull(learner.dic_);
                assertNotNull(learner.parser_);
                assertNotNull(learner.reader_);
                assertNotNull(learner.opts_);

                assertNotNull(learner.params_);
                assertEquals(Parameters.KernelType.POLYNOMIAL, learner.params_.getKernelType());
                assertEquals(2, learner.params_.getDegree());
                assertEquals(1.0, learner.params_.getGamma(), E);
                assertEquals(1.0, learner.params_.getCoef0(), E);
                assertEquals(1.0, learner.params_.getC(), E);
                assertEquals(0.001, learner.params_.getEpsilon(), E);
                assertEquals(30.0, learner.params_.getCacheSize(), E);

                assertEquals(0, learner.num_sentences_);
                assertEquals(0, learner.steps_);
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -l SVMs
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-l", "SVMs"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals("undefined learner type: SVMs", e.getMessage());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -t LINEAR
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-t", "LINEAR"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(Parameters.KernelType.LINEAR, learner.params_.getKernelType());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -t POLYNOMIAL
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-t", "POLYNOMIAL"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(Parameters.KernelType.POLYNOMIAL, learner.params_.getKernelType());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -t RBF
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-t", "RBF"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(Parameters.KernelType.RBF, learner.params_.getKernelType());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -t SIGMOID
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-t", "SIGMOID"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(Parameters.KernelType.SIGMOID, learner.params_.getKernelType());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -t LINeAR
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-t", "LINeAR"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals("undefined kernel type: LINeAR", e.getMessage());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -d 3
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-d", "3"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(3, learner.params_.getDegree());
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -d 3.1
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-d", "3.1"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals(true, e.getMessage() != null);
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -s 1.2
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-s", "1.2"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(1.2, learner.params_.getGamma(), E);
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -s -1k
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-s", "-1k"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals(true, e.getMessage() != null);
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -r 2.2
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-r", "2.2"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(2.2, learner.params_.getCoef0(), E);
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -r  2..2
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-r", "2..2"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals(true, e.getMessage() != null);
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -m 11.1
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-m", "11.1"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(30.0, learner.params_.getCacheSize(), E);
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -m 30.99
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-m", "30.99"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(30.99, learner.params_.getCacheSize(), E);

            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -m  KK
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-m", "KK"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                fail("");
            } catch( ParseException e){
                assertEquals(true, e.getMessage() != null);
            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -c 1.1 -e 1.2 -v
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-c", "1.1", "-e", "1.2", "-v"};
            DependencyLearner learner = new DependencyLearner();
            try {
                learner.parseCommandLineArguments(args);
                assertEquals(1.1, learner.params_.getC(), E);
                assertEquals(1.2, learner.params_.getEpsilon(), E);


            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // -g
            String[] args = {"-i", "samples/train.txt", "-o", "samples/hoge.model", "-g"};
            DependencyLearner learner = new DependencyLearner();
            try {
                assertEquals(false, learner.grouping_);
                learner.parseCommandLineArguments(args);
                assertEquals(true, learner.grouping_);

            } catch( Exception e){
                e.printStackTrace();
                fail("");
            }
        }
    }

}
