package jp.gr.java_conf.dyama.rink.parser;

/**
 *  feature interface for parsing
 * @author Hiroyasu Yamada
 *
 */
public interface Feature {

    /**
     * Encodes the feature information into the positive unique integer.
     * @return the encoded integer
     */
    public int encode();

    /**
     * Decodes the code to the feature information
     * @param code the code
     * @throws IllegalArgumentExcption if the code is a negative number.
     */
    public void decode(int code);

}
