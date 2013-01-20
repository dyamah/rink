package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.FeatureSpace.Binary;
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.ml.svm.Classifier;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;

/**
 * SVM Action Estimator
 * @author Hiroyasu Yamada
 *
 */
class SVMActionEstimator implements ActionEstimator {

    private static final long serialVersionUID = -6947346187423688573L;

    private static final Action.Type DEFAULT_ACTION = Action.Type.SHIFT;

    /** feature function */
    private FeatureFunction function_ ;

    /** SVMs Classifier */
    private Map<Integer, Classifier<?, FeatureSpace.Binary>> classifiers_ ;

    private GroupIdentifier group_identifier_ ;
    /**
     * Constructor
     * @param function feature function. throw IllegalArgumentException if function is null.
     * @param classifier SVMs classifier.  throw IllegalArgumentException if classifier is null.
     */
    SVMActionEstimator(FeatureFunction function, Classifier<?, FeatureSpace.Binary> classifier) {
        if (function == null)
            throw new IllegalArgumentException("the feature function is null.");
        if (classifier == null)
            throw new IllegalArgumentException("the classifier is null.");

        function_ = function ;
        classifiers_ = new HashMap<Integer, Classifier<?, FeatureSpace.Binary>>();
        group_identifier_ = new GroupIdentifier.UniGroupIdentifier();
        classifiers_.put(group_identifier_.getGroupID(null),  classifier);
    }

    /**
     * Constructor
     * @param function feature function. throw IllegalArgumentException if function is null.
     * @param classifiers SVMs classifiers.  throw IllegalArgumentException if classifier is null.
     * @param identifier group identifier. throw IllegalArgumentException if identifier is null.
     */
    SVMActionEstimator(FeatureFunction function, Map<Integer, Classifier<?, FeatureSpace.Binary>> classifiers, GroupIdentifier identifier) {
        if (function == null)
            throw new IllegalArgumentException("the feature function is null.");
        if (classifiers == null)
            throw new IllegalArgumentException("the classifiers is null.");

        if (identifier == null)
            throw new IllegalArgumentException("the identifier is null.");

        function_ = function ;
        classifiers_ = new HashMap<Integer, Classifier<?, FeatureSpace.Binary>>();
        group_identifier_ = identifier;
        for(Entry<Integer, Classifier<?, Binary>> entry : classifiers.entrySet()){
            classifiers_.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Action estimate(SampleImpl sample){
        if (sample == null)
            throw new IllegalArgumentException("the sample is null.");

        Classifier<?, Binary> classifier = classifiers_.get(group_identifier_.getGroupID(sample));
        if (classifier == null)
            return new ActionImpl(DEFAULT_ACTION) ;

        function_.apply(sample);
        BinaryFeatureVector fv = sample.getFeatureVector();
        fv.reset(sample.getFeatureBuffer());
        int y = classifier.classify(fv);
        Action.Type t = Action.Type.parseInt(y);
        assert(t != null);
        ActionImpl action = new ActionImpl(t);
        return action;
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        function_ = (FeatureFunction) in.readObject();
        group_identifier_ = (GroupIdentifier) in.readObject();
        int n = in.readInt();
        classifiers_ = new HashMap<Integer, Classifier<?, FeatureSpace.Binary>>();
        for(int i = 0 ; i < n ; i ++){
            int gid = in.readInt();
            Classifier<?, Binary> classifier = (Classifier<?, Binary>) in.readObject();
            classifiers_.put(gid, classifier);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeObject(function_);
        out.writeObject(group_identifier_);
        out.writeInt(classifiers_.size());
        for(Entry<Integer, Classifier<?, Binary>> entry : classifiers_.entrySet()){
            out.writeInt(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

}
