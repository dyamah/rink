package jp.gr.java_conf.dyama.rink.parser;
public interface Sentence {

    /**
     * Returns the number of words
     * @return number of words
     */
    public int size();

    /**
     * Returns the i-th word
     * @param i the index.
     * @return word corresponding to the index.
     * @throws IllegalArgumentException if i is negative or greater than {@link #size()} - 1.
     */
    public Word getWord(int i);


    /**
     * Returns the string of the sentence.
     * @return the string of the sentence
     */
    public String getString();

}
