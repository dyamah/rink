package jp.gr.java_conf.dyama.rink.parser;

import jp.gr.java_conf.dyama.rink.corpus.TagSet;

public interface Word extends Range {

    /**
     * get the beginning position of the word.
     * @return  beginning position of the word
     */
    @Override
    public int getBegin();

    /**
     * get the end position of the word.
     * @return the end position of the word
     */
    @Override
    public int getEnd();

    /**
     * get the length of the word ( = the number of characters )
     * @return length of the word
     */
    public int length();

    /**
     * get the word ID.
     * @return word ID
     */
    public int getID();


    /**
     * get the surface string of the word
     * @return surface string
     */
    public String getSurface();

    /**
     * get the base form string of the word
     * @return base form string
     */
    public String getBaseForm();

    /**
     * get the part-of-speech
     * @return part-of-speech
     */
    public TagSet.PartOfSpeech getPOS();


}
