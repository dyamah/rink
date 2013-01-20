package jp.gr.java_conf.dyama.rink.ml.svm;


import java.io.Serializable;

import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.FeatureVector;

/**
 * Interface for Classifier
 * @author Hiroyasu Yamada
 *
 * @param <K>  the type of Kernel functions.
 * @param <FS> the type of Feature Space
 */
public interface Classifier <K extends KernelFunction, FS extends FeatureSpace> extends Serializable {

    /**
     * classify a feature vector
     * @param x feature vector. throw IllegalArgumentException if x == null.
     * @return the best result of classification.
     */
    public int classify(FeatureVector<FS> x);

}
