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
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
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
    static final double E = 0.00000000000001;
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
    public void WeightVectorTest() throws IOException, ClassNotFoundException{
        {
            MIRAActionLearner.WeightVector wv = new MIRAActionLearner.WeightVector();
            assertEquals(0.0, wv.get(10399323), E);
            assertEquals(0.0, wv.squaredL2norm(), E);

            wv.set(10399323, 1.3);
            assertEquals(1.3, wv.get(10399323), E);
            assertEquals(1.69, wv.squaredL2norm(), E);
        }

        {
            MIRAActionLearner.WeightVector wv = new MIRAActionLearner.WeightVector();
            sample_.read();
            function_.apply(sample_);
            BinaryFeatureVector.Buffer x = sample_.getFeatureBuffer();
            assertEquals(true,  x.size() > 0);
            for(int i : x.getFeatures())
                assertEquals(0.0, wv.get(i), E);
            assertEquals(0.0, wv.squaredL2norm(), E);

            wv.update(x, 1.0);
            assertEquals(true,  x.size() > 0);
            for(int i : x.getFeatures())
                assertEquals(1.0, wv.get(i), E);

            assertEquals((double)x.size(), wv.squaredL2norm(), E);
        }

        {
            MIRAActionLearner.WeightVector wv = new MIRAActionLearner.WeightVector();
            BinaryFeatureVector.Buffer x0 = new BinaryFeatureVector.Buffer();
            BinaryFeatureVector.Buffer x1 = new BinaryFeatureVector.Buffer();
            BinaryFeatureVector.Buffer x2 = new BinaryFeatureVector.Buffer();

            x0.add(1); x0.add(2); x0.add(3);
            x1.add(3); x1.add(4);
            x2.add(100); x2.add(101); x2.add(102); x2.add(103);

            wv.set(1,   1.0);
            wv.set(3,   3.0);
            wv.set(103, 9.0);

            assertEquals(4.0, wv.dot(x0));
            assertEquals(3.0, wv.dot(x1));
            assertEquals(9.0, wv.dot(x2));
        }

        File tmpfile = File.createTempFile("MIRAActionLearnerTest", "tmp");
        tmpfile.deleteOnExit();
        {
            MIRAActionLearner.WeightVector wv = new MIRAActionLearner.WeightVector();
            wv.set(  1,  1.0);
            wv.set(  3,  1.0);
            wv.set(  7,  1.0);

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(wv);
            out.close();
        }

        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            MIRAActionLearner.WeightVector wv = (MIRAActionLearner.WeightVector) in.readObject();
            assertEquals(3.0, wv.squaredL2norm());
            assertEquals(0.0, wv.get(0), E);
            assertEquals(1.0, wv.get(1), E);
            assertEquals(0.0, wv.get(2), E);
            assertEquals(1.0, wv.get(3), E);
            assertEquals(0.0, wv.get(4), E);
            assertEquals(0.0, wv.get(5), E);
            assertEquals(0.0, wv.get(6), E);
            assertEquals(1.0, wv.get(7), E);
            assertEquals(0.0, wv.get(8), E);
            in.close();
        }
    }

    @Test
    public void testMIRAActionLearner() throws IOException, ClassNotFoundException {
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

        File tmpfile = File.createTempFile("MIRAActionEstimatorTest", ".tmp");
        tmpfile.deleteOnExit();
        {
            ActionEstimator est = learner.learn();
            State state = sample_.getState();
            while(state.size() > 1){
                Action y = est.estimate(sample_);
                state.apply(y);
            }
            DependencyRelations dep = state.getDependencies();
            dep.hasDependencyRelation(1, 0);
            dep.hasDependencyRelation(1, 3);
            dep.hasDependencyRelation(3, 2);
            dep.hasDependencyRelation(3, 4);
            dep.hasDependencyRelation(4, 6);
            dep.hasDependencyRelation(6, 5);

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(est);
            out.close();
        }

        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            ActionEstimator est = (ActionEstimator) in.readObject();
            sample_.reparse();
            State state = sample_.getState();
            while(state.size() > 1){
                Action y = est.estimate(sample_);
                state.apply(y);
            }
            DependencyRelations dep = state.getDependencies();
            dep.hasDependencyRelation(1, 0);
            dep.hasDependencyRelation(1, 3);
            dep.hasDependencyRelation(3, 2);
            dep.hasDependencyRelation(3, 4);
            dep.hasDependencyRelation(4, 6);
            dep.hasDependencyRelation(6, 5);
            in.close();
        }

    }

    @Test
    public void testAddExample() throws IOException {
        // TODO : add more test code.
        MIRAActionLearner learner  = new MIRAActionLearner(function_, new GroupIdentifier.UniGroupIdentifier());
        ActionImpl shift = new ActionImpl(Action.Type.SHIFT);
        ActionImpl left  = new ActionImpl(Action.Type.LEFT);
        sample_.read();
        learner.addExample(sample_, shift);
        State state = sample_.getState();
        state.apply(shift);
        learner.addExample(sample_, left);
    }



}
