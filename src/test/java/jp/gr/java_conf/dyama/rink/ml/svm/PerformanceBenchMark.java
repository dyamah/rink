package jp.gr.java_conf.dyama.rink.ml.svm;

import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

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


public class PerformanceBenchMark {
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
    /**
     * @param args
     */
    public static void main(String[] args) {
        {
            // TODO 自動生成されたメソッド・スタブ
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

            {
                long start = System.currentTimeMillis();
                for(int n = 0 ; n < 100; n++)
                    for(int l = 0 ; l < test_problem_.l; l++)
                        libsvm.svm.svm_predict(libsvm_model, test_problem_.x[l]);
                System.out.println("LIBSVM@LINEAR: " + (System.currentTimeMillis() - start) );
            }
            {
                long start = System.currentTimeMillis();
                for(int n = 0 ; n < 100; n++)
                    for(int l = 0 ; l < test_problem_.l; l++)
                        classifier.classify(test_dataset_.get(l).x_);
                System.out.println("SAGITTARIUS@LINEAR: " + (System.currentTimeMillis() - start) );
            }
        }

        {
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

            {
                long start = System.currentTimeMillis();
                for(int n = 0 ; n < 1000; n++)
                    for(int l = 0 ; l < test_problem_.l; l++)
                        libsvm.svm.svm_predict(libsvm_model, test_problem_.x[l]);
                System.out.println("LIBSVM@POLY(2): " + (System.currentTimeMillis() - start) );
            }
            // Estimation.setObjectPool(3);

            {
                long start = System.currentTimeMillis();
                for(int n = 0 ; n < 1000; n++)
                    for(int l = 0 ; l < test_problem_.l; l++)
                        classifier.classify(test_dataset_.get(l).x_);
                System.out.println("SAGITTARIUS@POLY(2): " + (System.currentTimeMillis() - start) );
            }
        }
    }
}
