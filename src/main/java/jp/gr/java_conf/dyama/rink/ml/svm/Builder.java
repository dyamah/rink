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
     * Builds a new SVM Learner (Binary Feature Space)
     * @return a new SVM learner
     */
    public static Learner<FeatureSpace.Binary> buildLearner(){
        return new LearnerImpl.BinarySpace();
    }

}
