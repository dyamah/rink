package jp.gr.java_conf.dyama.rink.corpus;

import jp.gr.java_conf.dyama.rink.corpus.TagSet.PartOfSpeech;

public interface Common {

    public enum POS implements PartOfSpeech {
        UNDEFINED(   0, "Undefined part of speech tag"),
        BOS(         1, "Beginning Of Sentence"),
        EOS(         2, "End Of Sentence"),
        NONE(        3, "NONE"),
        BLANK(       4, "Blank"),
        ;

        private int id_ ;
        private String description_ ;
        private POS(int id, String description){
            id_ = id;
            description_ = description ;
            if (description == null)
                description_ = "";
        }


        static private Common.POS[] ARRAY = Common.POS.values();

        /**
         * Parses a ID into a POS tag
         * @param i ID
         * @return POS tag. return UNDEFINED if i is undefined.
         */
        public static TagSet.PartOfSpeech parseInt(int i){
            for(POS pos : ARRAY){
                if (pos.getID() == i)
                    return pos;
            }
            return UNDEFINED;
        }

        /**
         * Parses a string into a POS tag.
         * @param string the POS tag string.
         * @return POS tag. return UNDEFINED if the string is undefined or null.
         */
        public static TagSet.PartOfSpeech parseString(String string){
            if (string == null)
                return UNDEFINED;
            for(POS pos : ARRAY){
                if (pos.toString().equals(string))
                    return pos;
            }
            return UNDEFINED;
        }

        @Override
        public int getID() {
            return id_;
        }

        @Override
        public String getDescription() {
            return description_;
        }

    }
}
