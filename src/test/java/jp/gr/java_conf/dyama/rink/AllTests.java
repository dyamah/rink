package jp.gr.java_conf.dyama.rink;

import jp.gr.java_conf.dyama.rink.corpus.CommonTest;
import jp.gr.java_conf.dyama.rink.corpus.PTBTest;
import jp.gr.java_conf.dyama.rink.ml.BufferTest;
import jp.gr.java_conf.dyama.rink.ml.FeatureTest;
import jp.gr.java_conf.dyama.rink.ml.FeatureVectorImplTest;
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVectorTest;
import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImplTest;
import jp.gr.java_conf.dyama.rink.ml.svm.DataSetReaderTest;
import jp.gr.java_conf.dyama.rink.ml.svm.EstimationTest;
import jp.gr.java_conf.dyama.rink.ml.svm.FeatureBinaryTest;
import jp.gr.java_conf.dyama.rink.ml.svm.FeatureRealTest;
import jp.gr.java_conf.dyama.rink.ml.svm.HyperPlaneTest;
import jp.gr.java_conf.dyama.rink.ml.svm.KernelFunctionTest;
import jp.gr.java_conf.dyama.rink.ml.svm.LearnerImplTest;
import jp.gr.java_conf.dyama.rink.ml.svm.LibSVMAnalyzerTest;
import jp.gr.java_conf.dyama.rink.ml.svm.ParametersTest;
import jp.gr.java_conf.dyama.rink.ml.svm.SVCoefficientTest;
import jp.gr.java_conf.dyama.rink.ml.svm.SVMTest;
import jp.gr.java_conf.dyama.rink.ml.svm.ScoreTest;
import jp.gr.java_conf.dyama.rink.parser.ImmutableIDConverterTest;
import jp.gr.java_conf.dyama.rink.parser.MutableIDConverterTest;
import jp.gr.java_conf.dyama.rink.parser.core.ActionImplTest;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReaderTest;
import jp.gr.java_conf.dyama.rink.parser.core.BitVectorTest;
import jp.gr.java_conf.dyama.rink.parser.core.CoNLLXSentenceReaderTest;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyParserTest;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyRelationsTest;
import jp.gr.java_conf.dyama.rink.parser.core.DeterministicBottomUpParserTest;
import jp.gr.java_conf.dyama.rink.parser.core.FeatureImplTest;
import jp.gr.java_conf.dyama.rink.parser.core.IWPT2003BestFeatureFunctionTest;
import jp.gr.java_conf.dyama.rink.parser.core.OracleActionEstimatorTest;
import jp.gr.java_conf.dyama.rink.parser.core.SVMActionEstimatorTest;
import jp.gr.java_conf.dyama.rink.parser.core.SVMActionLearnerTest;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImplTest;
import jp.gr.java_conf.dyama.rink.parser.core.SentenceImplTest;
import jp.gr.java_conf.dyama.rink.parser.core.SimpleSentenceReaderTest;
import jp.gr.java_conf.dyama.rink.parser.core.StateTest;
import jp.gr.java_conf.dyama.rink.parser.core.WordImplTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

    //  common

    // corpus
    CommonTest.class,
    PTBTest.class,

    // parser
    MutableIDConverterTest.class,
    ImmutableIDConverterTest.class,

    // parser.dependency
    ActionImplTest.class,
    AnnotatedSentenceReaderTest.class,
    CoNLLXSentenceReaderTest.class,
    DependencyRelationsTest.class,
    DependencyParserTest.class,
    DeterministicBottomUpParserTest.class,
    FeatureImplTest.class,
    IWPT2003BestFeatureFunctionTest.class,
    OracleActionEstimatorTest.class,
    SampleImplTest.class,
    SentenceImplTest.class,
    SimpleSentenceReaderTest.class,
    StateTest.class,
    SVMActionEstimatorTest.class,
    SVMActionLearnerTest.class,
    WordImplTest.class,

    // ml.svm
    BinaryFeatureVectorTest.class,
    BufferTest.class,
    ClassifierImplTest.class,
    DataSetReaderTest.class,
    EstimationTest.class,
    FeatureBinaryTest.class,
    FeatureTest.class,
    FeatureVectorImplTest.class,
    HyperPlaneTest.class,
    KernelFunctionTest.class,
    LearnerImplTest.class,
    LibSVMAnalyzerTest.class,
    ParametersTest.class,
    FeatureRealTest.class,
    ScoreTest.class,
    SVCoefficientTest.class,
    SVMTest.class,

    // utils
    BitVectorTest.class,

    })
public class AllTests {
}
