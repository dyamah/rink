package jp.gr.java_conf.dyama.rink.parser;

import java.io.IOException;

/**
 * Sample for parsing.
 * @author Hiroyasu Yamada
 *
 */
public interface Sample {


    /**
     * Parses one step  at the current position
     * @return true if the sample can be parsed. return false if the sample can not be parsed any more.
     */
    public boolean parseOneStep();

    /**
     * Prepares to re-parse the current sentence
     */
    public void reparse();

    /**
     * Prepares to re-parse the current sentence, and set the start position.
     * @param start the start position.
     */
    public void reparse(int start);

    /**
     * Returns the sentence.
     * @return sentence
     */
    public Sentence getSentence();

    /**
     * Reads a new sentence and sets up .
     * @return true if the reader can read a new sentence and setup. return false if there is no readable sentence.
     * @throws IOException if the reader fail to read a sentence from input stream.
     */
    public boolean read() throws IOException;

}
