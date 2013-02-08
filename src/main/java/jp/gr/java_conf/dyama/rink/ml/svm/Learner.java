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
     * Adds the training example.
     * @param x the feature vector.
     * @param y the label.
     * @throws IllegalArgumentException if the feature vector is null.
     */
    public void addExample(FeatureVector<FS> x, int y);

    /**
     * learns SVM Classifiers
     * @param params the set of parameters for learning.
     * @return SVM classifiers.
     * @throw IllegalArgumentException if the set of parameters is null.
     */
    public Classifier<? extends KernelFunction, FS> learn(Parameters params);


    /**
     * Returns the number of different types of labels in the added examples.
     * @return the number of different types of labels
     */
    public int getSizeOfSetOfLabels();

    /**
     * Returns the default label. The default label is defined as follows:
     * the most frequent labels.
     * if there are 2 more  most frequent labels, the default label is more earlier added label in the added examples.
     * @return default label. return null if no example has been added.
     */
    public Integer getDefaultLabel();


    /**
     * Returns the size of added examples.
     * @return the size of added examples.
     */
    public int getSizeOfExamples();


}
