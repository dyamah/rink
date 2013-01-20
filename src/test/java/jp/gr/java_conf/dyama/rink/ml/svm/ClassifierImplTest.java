package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl;
import jp.gr.java_conf.dyama.rink.ml.svm.KernelFunction;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClassifierImplTest {
    static final String train_path_ = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/train.txt";
    static final String test_path_  = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/test.txt";

    static List< DataSetReader.Pair<BinaryFeatureVector, Integer> > train_dataset_ = null ;
    static List< DataSetReader.Pair<BinaryFeatureVector, Integer> > test_dataset_ = null ;

    static {
        try {
            train_dataset_ = DataSetReader.read_dataset(train_path_);
            test_dataset_ = DataSetReader.read_dataset(test_path_);
        } catch (IOException e) {
            e.printStackTrace();
            fail("");
        }
    }

    ClassifierImpl.Arguments args_linear_;
    ClassifierImpl.Arguments args_nonelinear_;

    @Before
    public void setUp() throws Exception {
        args_linear_ = new ClassifierImpl.Arguments();

        args_linear_.labels_ = new int[3]; args_linear_.labels_[0] = 1; args_linear_.labels_[1] = 2; args_linear_.labels_[2] = 3;

        args_linear_.hps_ = new ClassifierImpl.HyperPlane[3];
        args_linear_.hps_[0] = new ClassifierImpl.HyperPlane(0, 1, 1.0);
        args_linear_.hps_[1] = new ClassifierImpl.HyperPlane(0, 2, 1.0);
        args_linear_.hps_[2] = new ClassifierImpl.HyperPlane(1, 2, 1.0);

        args_linear_.kernel_ = new KernelFunction.Linear();
        args_linear_.params_ = new Parameters.ParametersImpl();
        args_linear_.params_.setKernelType(Parameters.KernelType.LINEAR);

        args_linear_.svcoefs_ = new ClassifierImpl.SVCoefficient[3];
        args_linear_.svcoefs_[0] = new ClassifierImpl.SVCoefficient(0, 1.0, 2); args_linear_.svcoefs_[0].addCoefficient(1,  1.0); args_linear_.svcoefs_[0].addCoefficient(2, 2.0);
        args_linear_.svcoefs_[1] = new ClassifierImpl.SVCoefficient(1, 1.0, 2); args_linear_.svcoefs_[1].addCoefficient(0, -1.0); args_linear_.svcoefs_[1].addCoefficient(2, 1.0);
        args_linear_.svcoefs_[2] = new ClassifierImpl.SVCoefficient(2, 1.0, 2); args_linear_.svcoefs_[2].addCoefficient(0, -1.0); args_linear_.svcoefs_[2].addCoefficient(1, -1.0);
        {
            ClassifierImpl.Feature.Real[] features = new ClassifierImpl.Feature.Real[4];
            features[0] = new ClassifierImpl.Feature.Real(10, 2); features[0].addValue(1.1, 0); features[0].addValue(1.2, 1);
            features[1] = new ClassifierImpl.Feature.Real(11, 1); features[1].addValue(-1.1, 1);
            features[2] = new ClassifierImpl.Feature.Real(12, 3); features[2].addValue(2, 0); features[2].addValue(1.2, 1); features[2].addValue(-1.3, 2);
            features[3] = new ClassifierImpl.Feature.Real(13, 2); features[3].addValue(-1, 0); features[3].addValue(1.2, 1);
            args_linear_.features_ = features;
        }

        args_nonelinear_ = new ClassifierImpl.Arguments();

        args_nonelinear_.labels_ = new int[3]; args_nonelinear_.labels_[0] = 1; args_nonelinear_.labels_[1] = 2; args_nonelinear_.labels_[2] = 3;

        args_nonelinear_.hps_ = new ClassifierImpl.HyperPlane[3];
        args_nonelinear_.hps_[0] = new ClassifierImpl.HyperPlane(0, 1, 1.0);
        args_nonelinear_.hps_[1] = new ClassifierImpl.HyperPlane(0, 2, 1.0);
        args_nonelinear_.hps_[2] = new ClassifierImpl.HyperPlane(1, 2, 1.0);

        args_nonelinear_.kernel_ = new KernelFunction.Polynomial(2, 1.0, 1.0);
        args_nonelinear_.params_ = new Parameters.ParametersImpl();
        args_nonelinear_.params_.setKernelType(Parameters.KernelType.POLYNOMIAL);

        args_nonelinear_.svcoefs_ = new ClassifierImpl.SVCoefficient[3];
        args_nonelinear_.svcoefs_[0] = new ClassifierImpl.SVCoefficient(0, 1.0, 2); args_nonelinear_.svcoefs_[0].addCoefficient(1,  1.0); args_nonelinear_.svcoefs_[0].addCoefficient(2, 2.0);
        args_nonelinear_.svcoefs_[1] = new ClassifierImpl.SVCoefficient(1, 1.0, 2); args_nonelinear_.svcoefs_[1].addCoefficient(0, -1.0); args_nonelinear_.svcoefs_[1].addCoefficient(2, 1.0);
        args_nonelinear_.svcoefs_[2] = new ClassifierImpl.SVCoefficient(2, 1.0, 2); args_nonelinear_.svcoefs_[2].addCoefficient(0, -1.0); args_nonelinear_.svcoefs_[2].addCoefficient(1, -1.0);

        {
            ClassifierImpl.Feature.Binary[] features = new ClassifierImpl.Feature.Binary[4];
            features[0] = new ClassifierImpl.Feature.Binary(10, 2); features[0].addSvID(0); features[0].addSvID(1);
            features[1] = new ClassifierImpl.Feature.Binary(11, 1); features[1].addSvID(1);
            features[2] = new ClassifierImpl.Feature.Binary(12, 3); features[2].addSvID(0); features[2].addSvID(1); features[2].addSvID(2);
            features[3] = new ClassifierImpl.Feature.Binary(13, 2); features[3].addSvID(0); features[3].addSvID(1);
            args_nonelinear_.features_ = features;
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClassifierImpl() {

        try {
            new ClassifierImpl.LinearBinary(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the arguments is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            new ClassifierImpl.NoneLinearBinary(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the arguments is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        new ClassifierImpl.LinearBinary(args_linear_);
        new ClassifierImpl.NoneLinearBinary(args_nonelinear_);
    }

    @Test
    public void testGetLabel() {
        ClassifierImpl.LinearBinary classifier = new ClassifierImpl.LinearBinary(args_linear_);
        assertEquals(3, classifier.getNumberOfLabels());
        try {
            classifier.getLabel(-1);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the label ID is negative.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        assertEquals(1, classifier.getLabel(0));
        assertEquals(2, classifier.getLabel(1));
        assertEquals(3, classifier.getLabel(2));

        try {
            classifier.getLabel(3);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the label ID is out of range.", e.getMessage());
        } catch (Exception e){
            fail("");
        }
    }

    @Test
    public void testClassify(){
        BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
        BinaryFeatureVector fv0 = new BinaryFeatureVector(); fv0.reset(buffer);

        buffer.clear();
        buffer.add(1); buffer.add(12);
        BinaryFeatureVector fv1 = new BinaryFeatureVector(); fv1.reset(buffer);

        buffer.clear();
        buffer.add(400); buffer.add(500);
        BinaryFeatureVector fv2 = new BinaryFeatureVector(); fv2.reset(buffer);

        ClassifierImpl.LinearBinary     c1 = new ClassifierImpl.LinearBinary(args_linear_);
        ClassifierImpl.NoneLinearBinary c2 = new ClassifierImpl.NoneLinearBinary(args_nonelinear_);

        try {
            c1.classify(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the feature vector is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        try {
            c2.classify(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the feature vector is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        try {
            c1.classify(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the feature vector is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        c1.classify(fv0);
        c1.classify(fv1);
        c1.classify(fv2);

        c2.classify(fv0);
        c2.classify(fv1);
        c2.classify(fv2);


    }

}
