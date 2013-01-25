package jp.gr.java_conf.dyama.rink.tools;

public interface Common {
    enum Format {
        ORIGINAL,
        CoNLLX;

        /**
         * parse the string.
         * @param str the target string
         * @return format. return null if the target string is null or undefined format.
         */
        static Format parseString(String str){
            if (str == null)
                return null ;
            if (ORIGINAL.toString().equals(str))
                return ORIGINAL;

            if (CoNLLX.toString().equals(str))
                return CoNLLX;

            return null;
        }
    }
}
