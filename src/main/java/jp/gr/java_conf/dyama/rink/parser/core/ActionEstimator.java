package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.Serializable;


public interface ActionEstimator extends Serializable {

    /**
     * Estimates the best parsing action at the current position in the target sample.
     * @param sample the target sample.
     * @return action the best parsing action.
     * @throws IllegalArgumentException if the sample is null.
     */
    public Action estimate(SampleImpl sample);

}
