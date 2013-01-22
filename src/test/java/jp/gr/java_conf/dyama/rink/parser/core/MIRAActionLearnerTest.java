package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.IOException;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.core.Action;
import jp.gr.java_conf.dyama.rink.parser.core.ActionEstimator;
import jp.gr.java_conf.dyama.rink.parser.core.GroupIdentifier;
import jp.gr.java_conf.dyama.rink.parser.core.IWPT2003BestFeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.core.MIRAActionLearner;
import jp.gr.java_conf.dyama.rink.parser.core.OracleActionEstimator;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SimpleSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.State;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImplTest.DummyParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MIRAActionLearnerTest {
    Parameters params_ ;
    FeatureFunction function_ ;
    SampleImpl sample_ ;
    SimpleSentenceReader reader_ ;
    OracleActionEstimator estimator_ ;

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
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMIRAActionLearner() throws IOException {
        MIRAActionLearner learner  = new MIRAActionLearner(function_, new GroupIdentifier.UniGroupIdentifier());
        sample_.read();

        for(int k = 0 ; k < 3; k++){
            reader_.addWord("I",          0,  1, PTB.POS.PRP,  1);
            reader_.addWord("saw",        2,  5, PTB.POS.VBD, -1);
            reader_.addWord("a",          6,  7, PTB.POS.DT,   3);
            reader_.addWord("girl",       8, 12, PTB.POS.NNS,  1);
            reader_.addWord("with",      13, 17, PTB.POS.IN,   3);
            reader_.addWord("a",         18, 19, PTB.POS.CC,   6);
            reader_.addWord("telescope", 20, 29, PTB.POS.JJ,   4);
            sample_.read();
            State state = sample_.getState();
            while(state.size() > 1){
                Action y = estimator_.estimate(sample_);
                learner.addExample(sample_, y);
                state.apply(y);
            }
        }
        reader_.addWord("I",          0,  1, PTB.POS.PRP,  1);
        reader_.addWord("saw",        2,  5, PTB.POS.VBD, -1);
        reader_.addWord("a",          6,  7, PTB.POS.DT,   3);
        reader_.addWord("girl",       8, 12, PTB.POS.NNS,  1);
        reader_.addWord("with",      13, 17, PTB.POS.IN,   3);
        reader_.addWord("a",         18, 19, PTB.POS.CC,   6);
        reader_.addWord("telescope", 20, 29, PTB.POS.JJ,   4);
        sample_.read();
        ActionEstimator est = learner.learn();
        {
            State state = sample_.getState();
            while(state.size() > 1){
                int l = state.getLeftTarget();
                int r = state.getRightTarget();
                Action y = est.estimate(sample_);
                Action.Type t = y.getType();
                state.apply(y);
            }
        }

    }

    @Test
    public void testAddExample() {
        // TODO fail("まだ実装されていません");
    }

    @Test
    public void testLearnBufferType() {
        // TODO fail("まだ実装されていません");
    }

    @Test
    public void testLearn() {
        // TODO fail("まだ実装されていません");
    }

    @Test
    public void testSetProgressPrintStream() {
        // TODO fail("まだ実装されていません");
    }

}
