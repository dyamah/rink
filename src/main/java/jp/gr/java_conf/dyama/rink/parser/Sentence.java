package jp.gr.java_conf.dyama.rink.parser;
public interface Sentence {

    /**
     * get the number of words
     * @return number of words
     */
    public int size();

    /**
     * get the i-th word
     * @param i index. throw IllegalArgumentException if i is negative or greater than {@link #size()} - 1.
     * @return word
     */
    public Word getWord(int i);


    /**
     * get the string of the sentence.
     * @return string of the sentence
     */
    public String getString();

}
