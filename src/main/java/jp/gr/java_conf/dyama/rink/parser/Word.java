package jp.gr.java_conf.dyama.rink.parser;

import jp.gr.java_conf.dyama.rink.corpus.TagSet;

public interface Word extends Range {

    /**
     * Returns the beginning position of the word.
     * @return the beginning position of the word.
     */
    @Override
    public int getBegin();

    /**
     * Returns the ending position of the word.
     * @return the ending position of the word.
     */
    @Override
    public int getEnd();

    /**
     * Returns the length of the word ( = the number of characters )
     * @return the length of the word.
     */
    public int length();

    /**
     * Returns the word's ID.
     * @return word's ID.
     */
    public int getID();


    /**
     * Returns the surface string of the word.
     * @return the surface string of the word.
     */
    public String getSurface();

    /**
     * Returns the base form of the word.
     * @return base form string.
     */
    public String getBaseForm();

    /**
     * Returns the part-of-speech tag.
     * @return the part-of-speech tag.
     */
    public TagSet.PartOfSpeech getPOS();


}
