package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;


import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.FeatureSpace.Binary;
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.ml.svm.Classifier;
import jp.gr.java_conf.dyama.rink.ml.svm.KernelFunction;
import jp.gr.java_conf.dyama.rink.ml.svm.Learner;
import jp.gr.java_conf.dyama.rink.ml.svm.LearnerImpl;
import jp.gr.java_conf.dyama.rink.ml.svm.LibSVMAnalyzer;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters.KernelType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SVMTest {
    static final String train_path_ = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/train.txt";
    static final String test_path_  = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/test.txt";
    static libsvm.svm_problem train_problem_  = null;
    static libsvm.svm_problem test_problem_   = null;
    static List< DataSetReader.Pair<BinaryFeatureVector, Integer> > train_dataset_ = null ;
    static List< DataSetReader.Pair<BinaryFeatureVector, Integer> > test_dataset_ = null ;

    static {
        try {
            train_problem_ = DataSetReader.read_problem(train_path_) ;
            test_problem_  = DataSetReader.read_problem(test_path_) ;

            train_dataset_ = DataSetReader.read_dataset(train_path_);
            test_dataset_ = DataSetReader.read_dataset(test_path_);
        } catch (IOException e) {
            e.printStackTrace();
            fail("");
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLinearBinary() throws IOException {
        libsvm.svm_model libsvm_model = null;
        {
            libsvm.svm_parameter params = LibSVMAnalyzer.makeDefaultParameters();
            params.kernel_type = libsvm.svm_parameter.LINEAR;
            libsvm_model = libsvm.svm.svm_train(train_problem_, params);
        }

        Learner<FeatureSpace.Binary> learner = new LearnerImpl.BinarySpace();

        for( DataSetReader.Pair<BinaryFeatureVector, Integer> e:  train_dataset_){
            learner.addExample(e.x_, e.y_);
        }

        Classifier<? extends KernelFunction, Binary> classifier = null ;
        {
            Parameters params = new Parameters.ParametersImpl();
            params.setKernelType(KernelType.LINEAR);
            classifier = learner.learn(params);
        }

        assertEquals(1000, test_problem_.l);
        assertEquals(test_problem_.l, test_dataset_.size());

        for(int l = 0 ; l < test_problem_.l; l++){
            double p  = libsvm.svm.svm_predict(libsvm_model, test_problem_.x[l]);
            double p_ = classifier.classify(test_dataset_.get(l).x_);
            assertEquals(p, p_, 0.000001);
        }

        int error = 0 ;
        for(int l = 0 ; l < train_problem_.l; l++){
            double p  = libsvm.svm.svm_predict(libsvm_model, train_problem_.x[l]);
            double p_ = classifier.classify(train_dataset_.get(l).x_);
            assertEquals(p, p_, 0.000001);
            if (train_problem_.y[l] != p_)
                error ++ ;
        }
        assertEquals(true, error < 5);
        System.out.println("#error@LINEAR:"  + error);
    }

    @Test
    public void testPolynomialBinary() {
        libsvm.svm_model libsvm_model = null;
        {
            libsvm.svm_parameter params = LibSVMAnalyzer.makeDefaultParameters();
            params.kernel_type = libsvm.svm_parameter.POLY;
            params.degree = 2;
            params.gamma = 1.0 ;
            params.coef0 = 1.0 ;
            libsvm_model = libsvm.svm.svm_train(train_problem_, params);
        }

        Learner<FeatureSpace.Binary> learner = new LearnerImpl.BinarySpace();

        for( DataSetReader.Pair<BinaryFeatureVector, Integer> e:  train_dataset_){
            learner.addExample(e.x_, e.y_);
        }

        Classifier<? extends KernelFunction, Binary> classifier = null ;
        {
            Parameters params = new Parameters.ParametersImpl();
            params.setKernelType(KernelType.POLYNOMIAL);
            params.setDegree(2);
            params.setGamma(1.0);
            params.setCoef0(1.0);
            classifier = learner.learn(params);
        }

        assertEquals(1000, test_problem_.l);
        assertEquals(test_problem_.l, test_dataset_.size());

        for(int l = 0 ; l < test_problem_.l; l++){
            double p  = libsvm.svm.svm_predict(libsvm_model, test_problem_.x[l]);
            double p_ = classifier.classify(test_dataset_.get(l).x_);
            assertEquals(p, p_, 0.000001);
        }

        int error = 0 ;
        for(int l = 0 ; l < train_problem_.l; l++){
            double p  = libsvm.svm.svm_predict(libsvm_model, train_problem_.x[l]);
            double p_ = classifier.classify(train_dataset_.get(l).x_);
            assertEquals(p, p_, 0.000001);
            if (train_problem_.y[l] != p_)
                error ++ ;
        }
        assertEquals(true, error < 5);
        System.out.println("#error@POLYNOMIAL:"  + error);
    }

    @Test
    public void testRBFBinary() {
        libsvm.svm_model libsvm_model = null;
        {
            libsvm.svm_parameter params = LibSVMAnalyzer.makeDefaultParameters();
            params.kernel_type = libsvm.svm_parameter.RBF;
            params.gamma = 1.0 ;
            libsvm_model = libsvm.svm.svm_train(train_problem_, params);
        }

        Learner<FeatureSpace.Binary> learner = new LearnerImpl.BinarySpace();

        for( DataSetReader.Pair<BinaryFeatureVector, Integer> e:  train_dataset_){
            learner.addExample(e.x_, e.y_);
        }

        Classifier<? extends KernelFunction, Binary> classifier = null ;
        {
            Parameters params = new Parameters.ParametersImpl();
            params.setKernelType(KernelType.RBF);
            params.setGamma(1.0);
            classifier = learner.learn(params);
        }

        assertEquals(1000, test_problem_.l);
        assertEquals(test_problem_.l, test_dataset_.size());

        for(int l = 0 ; l < test_problem_.l; l++){
            double p  = libsvm.svm.svm_predict(libsvm_model, test_problem_.x[l]);
            double p_ = classifier.classify(test_dataset_.get(l).x_);
            assertEquals(p, p_, 0.000001);
        }
        int error = 0 ;
        for(int l = 0 ; l < train_problem_.l; l++){
            double p  = libsvm.svm.svm_predict(libsvm_model, train_problem_.x[l]);
            double p_ = classifier.classify(train_dataset_.get(l).x_);
            assertEquals(p, p_, 0.000001);
            if (train_problem_.y[l] != p_)
                error ++ ;
        }
        assertEquals(true, error < 5);
        System.out.println("#error@RBF:"  + error);
    }

    @Test
    public void testSigmoidBinary() {
        libsvm.svm_model libsvm_model = null;
        {
            libsvm.svm_parameter params = LibSVMAnalyzer.makeDefaultParameters();
            params.kernel_type = libsvm.svm_parameter.SIGMOID;
            params.gamma = 1.0 ;
            params.coef0 = 1.1;
            libsvm_model = libsvm.svm.svm_train(train_problem_, params);
        }

        Learner<FeatureSpace.Binary> learner = new LearnerImpl.BinarySpace();

        for( DataSetReader.Pair<BinaryFeatureVector, Integer> e:  train_dataset_){
            learner.addExample(e.x_, e.y_);
        }

        Classifier<? extends KernelFunction, Binary> classifier = null ;
        {
            Parameters params = new Parameters.ParametersImpl();
            params.setKernelType(KernelType.SIGMOID);
            params.setGamma(1.0);
            params.setCoef0(1.1);
            classifier = learner.learn(params);
        }

        assertEquals(1000, test_problem_.l);
        assertEquals(test_problem_.l, test_dataset_.size());

        for(int l = 0 ; l < test_problem_.l; l++){
            double p  = libsvm.svm.svm_predict(libsvm_model, test_problem_.x[l]);
            double p_ = classifier.classify(test_dataset_.get(l).x_);
            assertEquals(p, p_, 0.000001);
        }

        int error = 0 ;
        for(int l = 0 ; l < train_problem_.l; l++){
            double p  = libsvm.svm.svm_predict(libsvm_model, train_problem_.x[l]);
            double p_ = classifier.classify(train_dataset_.get(l).x_);
            assertEquals(p, p_, 0.000001);
            if (train_problem_.y[l] != p_)
                error ++ ;
        }
        assertEquals(true, error < 5);
        System.out.println("#error@SIGMOID:"  + error);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException{
        File tmpfile = File.createTempFile("SVMTest", ".tmp");
        tmpfile.deleteOnExit();

        {
            Learner<FeatureSpace.Binary> learner = new LearnerImpl.BinarySpace();
            for( DataSetReader.Pair<BinaryFeatureVector, Integer> e:  train_dataset_){
                learner.addExample(e.x_, e.y_);
            }

            Classifier<? extends KernelFunction, Binary> src = null ;
            {
                Parameters params = new Parameters.ParametersImpl();
                params.setKernelType(KernelType.LINEAR);
                src = learner.learn(params);
            }

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            @SuppressWarnings("unchecked")
            Classifier<? extends KernelFunction, Binary> dist = (Classifier<? extends KernelFunction, Binary>) in.readObject() ;
            in.close();

            assertEquals(1000, test_dataset_.size());

            for(int l = 0 ; l < test_dataset_.size(); l++){
                double p  =  src.classify(test_dataset_.get(l).x_);
                double p_ = dist.classify(test_dataset_.get(l).x_);
                assertEquals(p, p_, 0.000001);
            }

            for(int l = 0 ; l < train_dataset_.size(); l++){
                double p  =  src.classify(train_dataset_.get(l).x_);
                double p_ = dist.classify(train_dataset_.get(l).x_);
                assertEquals(p, p_, 0.000001);
            }
        }

        { // polynomial
            Learner<FeatureSpace.Binary> learner = new LearnerImpl.BinarySpace();
            for( DataSetReader.Pair<BinaryFeatureVector, Integer> e:  train_dataset_){
                learner.addExample(e.x_, e.y_);
            }

            Classifier<? extends KernelFunction, Binary> src = null ;
            {
                Parameters params = new Parameters.ParametersImpl();
                params.setKernelType(KernelType.POLYNOMIAL);
                params.setGamma(1.0);
                params.setCoef0(1.1);
                params.setDegree(2);
                src = learner.learn(params);
            }

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            @SuppressWarnings("unchecked")
            Classifier<? extends KernelFunction, Binary> dist = (Classifier<? extends KernelFunction, Binary>) in.readObject() ;
            in.close();

            assertEquals(1000, test_dataset_.size());

            for(int l = 0 ; l < test_dataset_.size(); l++){
                double p  =  src.classify(test_dataset_.get(l).x_);
                double p_ = dist.classify(test_dataset_.get(l).x_);
                assertEquals(p, p_, 0.000001);
            }

            for(int l = 0 ; l < train_dataset_.size(); l++){
                double p  =  src.classify(train_dataset_.get(l).x_);
                double p_ = dist.classify(train_dataset_.get(l).x_);
                assertEquals(p, p_, 0.000001);
            }
        }

        { // RBF
            Learner<FeatureSpace.Binary> learner = new LearnerImpl.BinarySpace();
            for( DataSetReader.Pair<BinaryFeatureVector, Integer> e:  train_dataset_){
                learner.addExample(e.x_, e.y_);
            }

            Classifier<? extends KernelFunction, Binary> src = null ;
            {
                Parameters params = new Parameters.ParametersImpl();
                params.setKernelType(KernelType.RBF);
                params.setGamma(1.0);
                src = learner.learn(params);
            }

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            @SuppressWarnings("unchecked")
            Classifier<? extends KernelFunction, Binary> dist = (Classifier<? extends KernelFunction, Binary>) in.readObject() ;
            in.close();

            assertEquals(1000, test_dataset_.size());

            for(int l = 0 ; l < test_dataset_.size(); l++){
                double p  =  src.classify(test_dataset_.get(l).x_);
                double p_ = dist.classify(test_dataset_.get(l).x_);
                assertEquals(p, p_, 0.000001);
            }

            for(int l = 0 ; l < train_dataset_.size(); l++){
                double p  =  src.classify(train_dataset_.get(l).x_);
                double p_ = dist.classify(train_dataset_.get(l).x_);
                assertEquals(p, p_, 0.000001);
            }

        }

        { // sigmoid
            Learner<FeatureSpace.Binary> learner = new LearnerImpl.BinarySpace();
            for( DataSetReader.Pair<BinaryFeatureVector, Integer> e:  train_dataset_){
                learner.addExample(e.x_, e.y_);
            }

            Classifier<? extends KernelFunction, Binary> src = null ;
            {
                Parameters params = new Parameters.ParametersImpl();
                params.setKernelType(KernelType.SIGMOID);
                params.setGamma(1.0);
                params.setCoef0(2.1);
                src = learner.learn(params);
            }

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            @SuppressWarnings("unchecked")
            Classifier<? extends KernelFunction, Binary> dist = (Classifier<? extends KernelFunction, Binary>) in.readObject() ;
            in.close();

            assertEquals(1000, test_dataset_.size());

            for(int l = 0 ; l < test_dataset_.size(); l++){
                double p  =  src.classify(test_dataset_.get(l).x_);
                double p_ = dist.classify(test_dataset_.get(l).x_);
                assertEquals(p, p_, 0.000001);
            }

            for(int l = 0 ; l < train_dataset_.size(); l++){
                double p  =  src.classify(train_dataset_.get(l).x_);
                double p_ = dist.classify(train_dataset_.get(l).x_);
                assertEquals(p, p_, 0.000001);
            }
        }
    }
}
