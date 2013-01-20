package jp.gr.java_conf.dyama.rink.parser;

/**
 *  feature interface for parsing
 * @author Hiroyasu Yamada
 *
 */
public interface Feature {

    /**
     * encode a feature information into positive unique integer.
     * @return encoded integer
     */
    public int encode();

    /**
     * decode an integer to a feature information
     * @param code encoded integer. throw IllegalArgumentExcption if code is negative number.
     */
    public void decode(int code);

}
