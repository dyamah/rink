package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.PrintStream;

/**
 * Learner for Dependency Parsing Actions
 * @author Hiroyasu Yamada
 *
 */
public interface ActionLearner {

    /**
     * Adds the training sample.
     * @param x the training sample
     * @param y the action.
     * @throws IllegalArgumentException if the sample is null.
     * @throws IllegalArgumentException if the action is null.
     */
    void addExample(SampleImpl x, Action y);

    /**
     * Learns the action estimator from added training examples.
     */
    ActionEstimator learn() ;

    /**
     * Sets the print stream for printing the learning progress.
     * @param out the print stream. Nothing will be printed if the out is null.
     */
    void setProgressPrintStream(PrintStream out);
}
