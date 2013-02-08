package jp.gr.java_conf.dyama.rink.parser;

import java.io.Serializable;

public interface FeatureFunction extends Serializable {

    /**
     * Applies this feature function to the target sample. (= extract features from the sample, and store them to the feature buffer of the sample.)
     * @param sample the target sample.
     * @throws IllegalArgumentException if the sample is null.
     */
    public void apply(Sample sample);

}
