package jp.gr.java_conf.dyama.rink.parser.core;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.corpus.Common;
import jp.gr.java_conf.dyama.rink.corpus.TagSet;
import jp.gr.java_conf.dyama.rink.corpus.TagSet.PartOfSpeech;
import jp.gr.java_conf.dyama.rink.parser.Word;

/**
 * Word class for Dependency Parsers
 * @author Hiroyasu Yamada
 */
public class WordImpl implements Word {

    /** the beginning position of the word */
    private int begin_;

    /** the end position of the word */
    private int end_;

    /** word ID */
    private int id_;

    /** POS */
    private TagSet.PartOfSpeech pos_;

    /** surface string */
    private String surface_;

    /** base form string */
    private String baseform_ ;

    /** dependency parent */
    private int parent_;

    public static final WordImpl BOS ;
    public static final WordImpl EOS ;
    public static final WordImpl NONE;

    static {
        BOS = new WordImpl();
        BOS.begin_ = -1; BOS.end_ = -1;
        BOS.id_ = IDConverter.BOS;
        BOS.pos_ = Common.POS.BOS;
        BOS.surface_  = "";
        BOS.baseform_ = "";
        BOS.parent_   = -1;

        EOS = new WordImpl();
        EOS.begin_ = Integer.MAX_VALUE; EOS.end_ = Integer.MAX_VALUE;
        EOS.id_    = IDConverter.EOS;
        EOS.pos_   = Common.POS.EOS;
        EOS.surface_ = "";
        EOS.baseform_ = "";
        EOS.parent_   = -1;

        NONE = new WordImpl();
        NONE.begin_ = Integer.MAX_VALUE; EOS.end_ = Integer.MAX_VALUE;
        NONE.id_    = IDConverter.NONE;
        NONE.pos_   = Common.POS.NONE;
        NONE.surface_ = "";
        NONE.baseform_ = "";
        NONE.parent_   = -1;
    }

    /**
     * Word Generator
     */
    static class Generator {

        public static final int DEFAULT_PARENT = -1;

        private IDConverter id_converter_;
        /**
         * Constructor
         * @param converter IDConverter. throw IllegalArgumentException if converter is null.
         */
        public Generator(IDConverter converter){
            if (converter == null)
                throw new IllegalArgumentException("the converter is null.");
            id_converter_ = converter;
        }

        /**
         * generate a word
         * @param surface the surface of the word. throw IllegalArgumentException if surface is null
         * @param begin the beginning of the word. throw IllegalArgumentException if begin is negative or greater than end.
         * @param end the end of the word.
         * @param pos part of speech tag.
         * @param parent ID of dependency parent.
         * @param word a word instance for reuse. if word is null, a new word instance is created.
         */
        public WordImpl generate(String surface, int begin, int end, PartOfSpeech pos, int parent, WordImpl word){
            if (surface == null)
                throw new IllegalArgumentException("the surface is null.");
            if (begin < 0)
                throw new IllegalArgumentException("the beginning of position is negative.");
            if (end < begin)
                throw new IllegalArgumentException("the end of position is less than the beginning one.");

            if (word == null)
                word =  new WordImpl();

            word.begin_   = begin ;
            word.end_     = end;
            word.pos_     = pos;
            word.surface_ = surface;
            word.id_      = id_converter_.convert(surface);
            word.parent_  = parent;
            return word;
        }
    }

    /**
    * Default Constructor
    */
   WordImpl(){
       begin_    = -1;
       end_      = -1;
       id_       = -1;
       pos_      = null;
       surface_  = null;
       baseform_ = null;
       parent_   = -1;
   }

    @Override
    public int getBegin() {
        return begin_;
    }

    @Override
    public int getEnd() {
        return end_;
    }

    @Override
    public int length() {
        return end_ - begin_ ;
    }

    @Override
    public int getID() {
        return id_;
    }

    @Override
    public String getSurface() {
        return surface_ ;
    }

    @Override
    public String getBaseForm() {
        return baseform_;
    }

    @Override
    public PartOfSpeech getPOS() {
        return pos_;
    }

    /**
     * get the ID of the dependency parent.
     * @return ID
     */
    public int getParent() {
        return parent_ ;
    }
}
