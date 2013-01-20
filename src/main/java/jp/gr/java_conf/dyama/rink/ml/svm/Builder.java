package jp.gr.java_conf.dyama.rink.ml.svm;

import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;


/**
 * SVMs Builder
 * @author Hiroyasu Yamada
 *
 */
public enum Builder {
    INATANCE;

    /**
     * build SVMs Classifier (Binary Feature Space)
     * @return
     */
    public static Classifier<?, FeatureSpace.Binary> buildClassifier(){
        return null;
    }

    /**
     * build SVMs Learner (Binary Feature Space)
     * @return
     */
    public static Learner<FeatureSpace.Binary> buildLearner(){
        return new LearnerImpl.BinarySpace();
    }

}
