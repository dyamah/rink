package jp.gr.java_conf.dyama.rink.ml.svm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.FeatureVector;
import jp.gr.java_conf.dyama.rink.ml.FeatureSpace.Binary;
import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.Arguments;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters.KernelType;

/**
 * Abstract class for SVMs Learner.
 * @author Hiroyasu Yamada
 *
 * @param <FS> the type of Feature Space: Real, Binary
 */
abstract class LearnerImpl<FS extends FeatureSpace> implements Learner<FS> {
    static class ProgressPrintStream implements libsvm.svm_print_interface {
        PrintStream out_ ;
        ProgressPrintStream(){
            out_ = System.err;
        }

        @Override
        public void print(String arg0) {
            out_.print(arg0);
        }
    }
    /** the list of feature vectors */
    private List<libsvm.svm_node[]> x_ ;

    /** the list of labels */
    private List<Integer> y_ ;

    /**
     * Default Constructor:
     */
    LearnerImpl(){
        x_ = new ArrayList<libsvm.svm_node[]>();
        y_ = new ArrayList<Integer>();
    }

    /**
     * Makes libsvm's examples from added examples.
     * @return libsvm's examples.
     * return null if the number of examples is less than 2;
     * return null if the number of different types of classes is less than 2
     */
    libsvm.svm_problem makeSvmProblem(){
        assert(x_.size() == y_.size());
        if (x_.size() < 2)
            return null;
        Set<Integer> labels = new HashSet<Integer>();
        for(Integer y : y_){
            labels.add(y);
        }
        if (labels.size() < 2)
            return null;

        libsvm.svm_problem problem = new libsvm.svm_problem();
        problem.l = x_.size();
        problem.y = new double[problem.l];
        problem.x = new libsvm.svm_node[problem.l][];
        for(int i = 0 ; i < problem.l; i++){
            problem.x[i] = x_.get(i);
            problem.y[i] = y_.get(i);
        }
        return problem ;
    }

    /**
     * Converts into libsvm parameters.
     * @param params the set of parameters for SVMs.
     * @return libsvm's parameters
     * @throw IllegalArgumentException if the set of parameters is null.
     */
    libsvm.svm_parameter convert(Parameters params){
        if (params == null)
            throw new IllegalArgumentException("the set of parameters is null.");

        libsvm.svm_parameter params_ = new libsvm.svm_parameter();
        params_.cache_size = params.getCacheSize();
        params_.degree     = params.getDegree();
        params_.gamma      = params.getGamma();
        params_.coef0      = params.getCoef0();
        params_.C          = params.getC();
        params_.eps        = params.getEpsilon();
        KernelType kt = params.getKernelType() ;
        if (kt == KernelType.LINEAR){
            params_.kernel_type = libsvm.svm_parameter.LINEAR;
        } else if (kt == KernelType.POLYNOMIAL ){
            params_.kernel_type = libsvm.svm_parameter.POLY;
        } else if (kt == KernelType.RBF ){
            params_.kernel_type = libsvm.svm_parameter.RBF;
        } else if (kt == KernelType.SIGMOID ){
            params_.kernel_type = libsvm.svm_parameter.SIGMOID;
        }
        return params_;
    }

    /**
     * Trains the SVM's model by the libsvm solver.
     * @param params the set of parameters for training.
     * @return libsvm's model<br>
     * @throws IllegalArgumentException if the set of parameters is null.
     * @throws IllegalStateException if the number of examples is less than 2.
     * @throws IllegalStateException if the number of classes is less than 2.
     */
    libsvm.svm_model train(Parameters params){
        libsvm.svm_parameter svm_params = convert(params);

        libsvm.svm_problem problem      = makeSvmProblem();

        if (problem == null)
            throw new IllegalStateException("fail to create training examples.");
        libsvm.svm.svm_set_print_string_function(new ProgressPrintStream());
        return libsvm.svm.svm_train(problem, svm_params);
    }

    @Override
    public int getSizeOfSetOfLabels() {
        Set<Integer> set = new HashSet<Integer>();
        for(Integer label : y_){
            set.add(label);
        }
        return set.size();
    }

    @Override
    public Integer getDefaultLabel() {
        if (y_.size() == 0)
            return null ;
        Map<Integer, Integer> map = new TreeMap<Integer,Integer>();
        List<Integer> order = new ArrayList<Integer>();

        for(Integer label : y_){
            Integer v = map.get(label);
            if (v == null){
                order.add(label);
                v = 0;
            }
            map.put(label, v + 1);
        }
        int max = 0;
        for( Entry<Integer, Integer> entry : map.entrySet()){
            if (entry.getValue() > max)
                max = entry.getValue();
        }

        for(Integer m : order){
            if (map.get(m) == max)
                return m ;

        }
        return null; // OK
    }

    @Override
    public int getSizeOfExamples(){
        return y_.size();
    }

    /**
     * SVM Learner for Binary Feature Space.
     * @author Hiroyasu Yamada
     *
     */
    static class BinarySpace extends LearnerImpl<FeatureSpace.Binary>{

        @Override
        public void addExample(FeatureVector<Binary> x, int y) {
            if (x == null)
                throw new IllegalArgumentException("the feature vector is null.");
            libsvm.svm_node[] fv = new libsvm.svm_node[x.size()];
            BinaryFeatureVector bf = (BinaryFeatureVector) x;
            for(int i = 0 ; i < x.size(); i++){
                libsvm.svm_node f = new libsvm.svm_node();
                f.index = bf.getFeatureID(i);
                f.value = 1.0;
                fv[i] = f ;
            }
            super.x_.add(fv);
            super.y_.add(y);
        }

        @Override
        public Classifier<? extends KernelFunction, Binary> learn(
                Parameters params) {

            if (params == null)
                throw new IllegalArgumentException("the set of parameters is null.");

            if (getSizeOfSetOfLabels() < 2){
                ClassifierImpl.Arguments args = ClassifierImpl.Arguments.createDummyArguments();
                args.params_ = params;
                Integer l = getDefaultLabel();
                if (l == null)
                    throw new IllegalStateException("fail to create training examples.");
                args.default_label_ = l;

                return new ClassifierImpl.DefaultBinary(args);
            }

            libsvm.svm_model model = train(params);
            Arguments args = LibSVMAnalyzer.analyzeModel(model);
            args.default_label_ = getDefaultLabel();
            if (params.getKernelType() == KernelType.LINEAR){
                return new ClassifierImpl.LinearBinary(args);
            }
            return new ClassifierImpl.NoneLinearBinary(args);

        }


    }
}
