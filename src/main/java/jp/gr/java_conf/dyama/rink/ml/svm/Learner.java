package jp.gr.java_conf.dyama.rink.ml.svm;

import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.FeatureVector;

/**
 * Learner
 * @author Hiroyasu Yamada
 *
 * @param <FS> the type of Feature SPcae: Real, Binary.
 */
public interface Learner<FS extends FeatureSpace>  {

    /**
     * add a training example.
     * @param x feature vector. throw IllegalArgumentException if x is null.
     * @param y label
     */
    public void addExample(FeatureVector<FS> x, int y);

    /**
     * learn SVM Classifiers by using added examples.
     * @param params paramters for learning. throw IllegalArgumentException if params is null.
     * @return SVM classifiers
     */
    public Classifier<? extends KernelFunction, FS> learn(Parameters params);


    /**
     * get the size of set of labels for added examples.
     * @return the size of set of labels
     */
    public int getSizeOfSetOfLabels();

    /**
     * get the default label for added examples. the default label is defined as follows:
     * most frequent labels.
     * if there are 2 more  most frequent labels, the default label is more earlier added label in the added examples.
     * @return default label. return null if no example has been added.
     */
    public Integer getDefaultLabel();


    /**
     * get the size of added examples.
     * @return the size of added examples.
     */
    public int getSizeOfExamples();


}
