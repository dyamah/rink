package jp.gr.java_conf.dyama.rink.common;

public interface IDConverter {
    /** reserved ID for undefined string and null. */
    public static final int UNDEFINED      = 0;

    /** reserved ID for BOS */
    public static final int BOS            = 1;

    /** reserved ID for EOS */
    public static final int EOS            = 2;

    /** reserved ID for EOS */
    public static final int NONE           = 3;

    /** starting ID for normal strings */
    static final int START                 = 10;

    /**
     * convert a string into positive unique ID
     * @param string  target string.
     * @return unique ID. return 0 if string is null.
     */
    public int convert(String string);
}
