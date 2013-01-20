package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.svm.Classifier;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.core.Action;
import jp.gr.java_conf.dyama.rink.parser.core.IWPT2003BestFeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.core.OracleActionEstimator;
import jp.gr.java_conf.dyama.rink.parser.core.SVMActionEstimator;
import jp.gr.java_conf.dyama.rink.parser.core.SVMActionLearner;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SimpleSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.State;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImplTest.DummyParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SVMActionEstimatorTest {
    Parameters params_ ;
    FeatureFunction function_ ;
    SampleImpl sample_ ;
    SimpleSentenceReader reader_ ;
    OracleActionEstimator estimator_ ;
    SVMActionLearner learner_ ;

    @Before
    public void setUp() throws Exception {
        function_ = new IWPT2003BestFeatureFunction(2, 2);
        DummyParser dummy_parser_ = new DummyParser();
        IDConverter idconverter_ = dummy_parser_.getIDConverter();
        reader_ = new SimpleSentenceReader(new WordImpl.Generator(idconverter_));

        reader_.addWord("I",          0,  1, PTB.POS.PRP,  1);
        reader_.addWord("saw",        2,  5, PTB.POS.VBD, -1);
        reader_.addWord("a",          6,  7, PTB.POS.DT,   3);
        reader_.addWord("girl",       8, 12, PTB.POS.NNS,  1);
        reader_.addWord("with",      13, 17, PTB.POS.IN,   3);
        reader_.addWord("a",         18, 19, PTB.POS.CC,   6);
        reader_.addWord("telescope", 20, 29, PTB.POS.JJ,   4);

        sample_ = (SampleImpl) dummy_parser_.createSample(reader_);
        params_ = new Parameters.ParametersImpl();
        params_.setKernelType(Parameters.KernelType.POLYNOMIAL);
        params_.setDegree(2);
        params_.setGamma(1.0);
        params_.setCoef0(1.0);
        estimator_ = new OracleActionEstimator(function_) ;


        learner_ = new SVMActionLearner(params_, function_);
        sample_.read();
        State state = sample_.getState();
        while (true){
            Action action = estimator_.estimate(sample_);
            learner_.addExample(sample_, action);
            if (! state.apply(action))
                break;
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSVMActionEstimator() {

        Classifier<?, FeatureSpace.Binary> classifier = null;

        try {
            new SVMActionEstimator(null, classifier);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the feature function is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            new SVMActionEstimator(function_, classifier);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the classifier is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testEstimate() {
        SVMActionEstimator estimator = (SVMActionEstimator) learner_.learn();

        try {
            estimator.estimate(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the sample is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException{
        File tmpfile = File.createTempFile("SVMActionEstimatorTest", ".tmp");
        List<Action> expect = new ArrayList<Action>();
        List<Action> result = new ArrayList<Action>();
        {
            SVMActionEstimator src = (SVMActionEstimator) learner_.learn();

            reader_.addWord("I",          0,  1, PTB.POS.PRP,  1);
            reader_.addWord("saw",        2,  5, PTB.POS.VBD, -1);
            reader_.addWord("a",          6,  7, PTB.POS.DT,   3);
            reader_.addWord("girl",       8, 12, PTB.POS.NNS,  1);
            reader_.addWord("with",      13, 17, PTB.POS.IN,   3);
            reader_.addWord("a",         18, 19, PTB.POS.CC,   6);
            reader_.addWord("telescope", 20, 29, PTB.POS.JJ,   4);
            sample_.read();
            State state = sample_.getState();

            while (true){
                Action action = src.estimate(sample_);
                if (! state.apply(action))
                    break;
                expect.add(action);
            }

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(src);
            out.close();
        }

        {

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            SVMActionEstimator dist = (SVMActionEstimator) in.readObject();
            in.close();

            reader_.addWord("I",          0,  1, PTB.POS.PRP,  1);
            reader_.addWord("saw",        2,  5, PTB.POS.VBD, -1);
            reader_.addWord("a",          6,  7, PTB.POS.DT,   3);
            reader_.addWord("girl",       8, 12, PTB.POS.NNS,  1);
            reader_.addWord("with",      13, 17, PTB.POS.IN,   3);
            reader_.addWord("a",         18, 19, PTB.POS.CC,   6);
            reader_.addWord("telescope", 20, 29, PTB.POS.JJ,   4);
            sample_.read();
            State state = sample_.getState();

            while (true){
                Action action = dist.estimate(sample_);
                if (! state.apply(action))
                    break;
                result.add(action);
            }

        }

        assertEquals(11, expect.size());
        assertEquals(Action.Type.RIGHT, expect.get( 0).getType());
        assertEquals(Action.Type.SHIFT, expect.get( 1).getType());
        assertEquals(Action.Type.RIGHT, expect.get( 2).getType());
        assertEquals(Action.Type.WAIT,  expect.get( 3).getType());
        assertEquals(Action.Type.WAIT,  expect.get( 4).getType());
        assertEquals(Action.Type.SHIFT, expect.get( 5).getType());
        assertEquals(Action.Type.RIGHT, expect.get( 6).getType());
        assertEquals(Action.Type.LEFT,  expect.get( 7).getType());
        assertEquals(Action.Type.LEFT,  expect.get( 8).getType());
        assertEquals(Action.Type.LEFT,  expect.get( 9).getType());
        assertEquals(Action.Type.SHIFT, expect.get(10).getType());

        assertEquals(true, expect.size() > 0);
        assertEquals(expect.size(), result.size());
        for(int i = 0; i < expect.size(); i++){
            Action a1 = expect.get(i);
            Action a2 = result.get(i);
            assertEquals(a1.getType(), a2.getType());
        }
    }

}
