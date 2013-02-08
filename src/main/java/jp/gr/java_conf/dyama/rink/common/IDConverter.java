package jp.gr.java_conf.dyama.rink.common;

public interface IDConverter {
    /** the reserved ID for undefined string and null. */
    public static final int UNDEFINED      = 0;

    /** the reserved ID for BOS */
    public static final int BOS            = 1;

    /** the reserved ID for EOS */
    public static final int EOS            = 2;

    /** the reserved ID for EOS */
    public static final int NONE           = 3;

    /** the starting ID for normal strings */
    static final int START                 = 10;

    /**
     * Converts a string into positive unique ID
     * @param string  the target string.
     * @return ID. return 0 if string is null.
     */
    public int convert(String string);
}
