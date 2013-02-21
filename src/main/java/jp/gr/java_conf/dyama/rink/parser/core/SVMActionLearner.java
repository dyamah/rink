package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.FeatureSpace.Binary;
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.ml.svm.Builder;
import jp.gr.java_conf.dyama.rink.ml.svm.Classifier;
import jp.gr.java_conf.dyama.rink.ml.svm.Learner;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;

/**
 * SVM Action Learner
 * @author Hiroyasu Yamada
 *
 */
class SVMActionLearner implements ActionLearner {

    /** the set of parameters for SVMs */
    private Parameters params_ ;

    /** the learners */
    private Map<Integer, Learner<FeatureSpace.Binary>> learners_;

    /** the feature function */
    private FeatureFunction function_ ;


    /** the group identifier */
    private GroupIdentifier group_identifier_ ;

    /** the print stream for printing learning progress */
    private PrintStream out_ ;

    /**
     * Constructor
     * @param params the set of parameters for SVMs.
     * @param function the feature function.
     * @throws IllegalArgumentException if the set of parameters is null.
     * @throws IllegalArgumentException if the feature function is null.
     */
    SVMActionLearner(Parameters params, FeatureFunction function){
        if (params == null)
            throw new IllegalArgumentException("the set of parameters is null.");
        if (function == null)
            throw new IllegalArgumentException("the feature function is null.");

        params_ = params;
        function_ = function ;
        learners_ = new HashMap<Integer, Learner<FeatureSpace.Binary>>();
        group_identifier_ = new GroupIdentifier.UniGroupIdentifier();
        out_ = System.err;
    }

    SVMActionLearner(Parameters params, FeatureFunction function, GroupIdentifier identifier){
        if (params == null)
            throw new IllegalArgumentException("the parameters is null.");
        if (function == null)
            throw new IllegalArgumentException("the feature function is null.");
        if (identifier == null)
            throw new IllegalArgumentException("the group identifier is null.");

        params_ = params;
        function_ = function ;
        learners_ = new HashMap<Integer, Learner<FeatureSpace.Binary>>();
        group_identifier_ = identifier ;
        out_ = System.err;
    }

    /**
     * set the group identifier.
     * @param group_identifier group identifier. throw new IllegalArgumentException if group_identifier is null.
     */
    void setGroupIdentifier(GroupIdentifier group_identifier){
        if (group_identifier == null)
            throw new IllegalArgumentException("the group identifier is null.");
        group_identifier_ = group_identifier;
    }

    @Override
    public void addExample(SampleImpl x, Action y) {
        if (x == null)
            throw new IllegalArgumentException("the sample is null.");
        if (y == null)
            throw new IllegalArgumentException("the action is null.");

        BinaryFeatureVector.Buffer buffer = x.getFeatureBuffer();

        BinaryFeatureVector fv = x.getFeatureVector();
        fv.reset(buffer);
        int gid = group_identifier_.getGroupID(x);
        Learner<FeatureSpace.Binary> learner = learners_.get(gid);
        if (learner == null){
            learner = Builder.buildLearner();
            learners_.put(gid, learner);
        }
        learner.addExample(fv, y.getType().getID());
    }

    @Override
    public ActionEstimator learn() {
        Map<Integer, Classifier<?, FeatureSpace.Binary>> classifiers = new HashMap<Integer, Classifier<?, FeatureSpace.Binary>>();

        int total = 0;
        for(Learner<Binary> l : learners_.values() )
            total += l.getSizeOfExamples();

        if (total == 0)
            throw new IllegalStateException("No example has been added.");

        int sum = 0;

        for(Entry<Integer, Learner<Binary>> entry: learners_.entrySet()){
            int gID = entry.getKey();
            Learner<Binary> learner = entry.getValue();
            assert( learner.getSizeOfSetOfLabels() > 0);
            printBefore(gID, learner);
            Classifier<?, FeatureSpace.Binary> c = learner.learn(params_);
            classifiers.put(gID, c);
            sum += learner.getSizeOfExamples();
            printAfter(sum, learner, total);
        }
        return new SVMActionEstimator(function_, classifiers, group_identifier_);
    }

    private void printBefore(int groupID, Learner<Binary> learner){
        if (out_ == null)
            return ;
        out_.println("Group: " + group_identifier_.getString(groupID));
        out_.println("#labels: " + learner.getSizeOfSetOfLabels());
        out_.println("#examples: " + learner.getSizeOfExamples());
    }

    private void printAfter(int sum, Learner<Binary> learner, int total){
        if (out_ == null)
            return ;
        double progress =  ((double) sum) / total ;
        out_.printf("progress %3.1f %% (%d / %d) ", progress * 100, sum, total);
        out_.println();
        out_.println();
    }

    @Override
    public void setProgressPrintStream(PrintStream out) {
        out_ = out;
    }

}
