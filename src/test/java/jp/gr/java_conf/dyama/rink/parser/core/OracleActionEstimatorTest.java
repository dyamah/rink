package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.core.Action;
import jp.gr.java_conf.dyama.rink.parser.core.ActionImpl;
import jp.gr.java_conf.dyama.rink.parser.core.IWPT2003BestFeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.core.OracleActionEstimator;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SimpleSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.State;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImplTest.DummyParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OracleActionEstimatorTest {

    FeatureFunction function_ ;
    SampleImpl sample_ ;
    SimpleSentenceReader reader_ ;

    @Before
    public void setUp() throws Exception {
        function_ = new IWPT2003BestFeatureFunction(2, 1);
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
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOracleActionEstimator() {
        try {
            new OracleActionEstimator(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the feature function is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        new OracleActionEstimator(function_) ;
    }

    @Test
    public void testEstimate00() throws IOException {
        OracleActionEstimator estimator = new OracleActionEstimator(function_) ;

        try {
            estimator.estimate(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the sample is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
        sample_.read();
        assertEquals(0, sample_.getFeatureBuffer().size());

        State state = sample_.getState();
        assertEquals(0, state.getPosition());
        assertEquals(7, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.RIGHT, action.getType());
            assertEquals(true, sample_.getFeatureBuffer().size() > 0);
            assertEquals(true, state.apply(action));
        }

        assertEquals(0, state.getPosition());
        assertEquals(6, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.SHIFT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(1, state.getPosition());
        assertEquals(6, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.RIGHT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(0, state.getPosition());
        assertEquals(5, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.WAIT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(1, state.getPosition());
        assertEquals(5, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.WAIT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(2, state.getPosition());
        assertEquals(5, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.SHIFT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(3, state.getPosition());
        assertEquals(5, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.RIGHT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(2, state.getPosition());
        assertEquals(4, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.LEFT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(1, state.getPosition());
        assertEquals(3, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.LEFT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(0, state.getPosition());
        assertEquals(2, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.LEFT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(0, state.getPosition());
        assertEquals(1, state.size());

        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.SHIFT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(1, state.getPosition());
        assertEquals(1, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.SHIFT, action.getType());
            assertEquals(false, state.apply(action));
        }

    }

    @Test
    public void testEstimate01() throws IOException {
        OracleActionEstimator estimator = new OracleActionEstimator(function_) ;
        assertEquals(0, sample_.getFeatureBuffer().size());
        State state = sample_.getState();
        assertEquals(0, state.getPosition());
        assertEquals(0, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.SHIFT, action.getType());
            assertEquals(true, sample_.getFeatureBuffer().size() > 0);
            assertEquals(false, state.apply(action));
        }

        assertEquals(0, state.getPosition());
        assertEquals(0, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.SHIFT, action.getType());
            assertEquals(true, sample_.getFeatureBuffer().size() > 0);
            assertEquals(false, state.apply(action));
        }
    }

    @Test
    public void testEstimate02() throws IOException {
        OracleActionEstimator estimator = new OracleActionEstimator(function_) ;
        sample_.read();
        reader_.addWord("I",          0,  1, PTB.POS.PRP,  1);
        reader_.addWord("saw",        2,  5, PTB.POS.VBD,  2);
        reader_.addWord("a",          6,  7, PTB.POS.DT,  -1);
        reader_.addWord("girl",       8, 12, PTB.POS.NNS,  4);
        reader_.addWord("with",      13, 17, PTB.POS.IN,   2);
        reader_.addWord("a",         18, 19, PTB.POS.CC,   4);
        reader_.addWord("telescope", 20, 29, PTB.POS.JJ,   4);
        sample_.read();
        assertEquals(0, sample_.getFeatureBuffer().size());
        State state = sample_.getState();
        assertEquals(true, state.apply(new ActionImpl(Action.Type.SHIFT)));
        assertEquals(1, state.getPosition());
        assertEquals(7, state.size());
        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.WAIT, action.getType());
            assertEquals(true, sample_.getFeatureBuffer().size() > 0);
            assertEquals(true, state.apply(action));
        }

        assertEquals(2, state.getPosition());
        assertEquals(7, state.size());
        assertEquals(true, state.apply(new ActionImpl(Action.Type.LEFT)));
        state.setPosition(2);

        assertEquals(2, state.getPosition());
        assertEquals(6, state.size());

        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.WAIT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(3, state.getPosition());
        assertEquals(6, state.size());

        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.LEFT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(2, state.getPosition());
        assertEquals(5, state.size());

        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.WAIT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(3, state.getPosition());
        assertEquals(5, state.size());

        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.LEFT, action.getType());
            assertEquals(true, state.apply(action));
        }

        assertEquals(2, state.getPosition());
        assertEquals(4, state.size());

        {
            Action action = estimator.estimate(sample_);
            assertEquals(Action.Type.WAIT, action.getType());
            assertEquals(true, state.apply(action));
        }
    }

    @Test
    public void testSerialization() throws IOException{

        File tmpfile = File.createTempFile("OracleActionEstimatorTest", ".tmp");
        tmpfile.deleteOnExit();
        {
            OracleActionEstimator estimator = new OracleActionEstimator(function_) ;
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));

            try {
                out.writeObject(estimator);
                fail("");
            } catch (UnsupportedOperationException e){
                assertEquals("Serialization is unsupported.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            out.close();
        }

        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));

            try {
                in.readObject();
                fail("");
            } catch (UnsupportedOperationException e){
                assertEquals("Serialization is unsupported.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            in.close();
        }
    }

}
