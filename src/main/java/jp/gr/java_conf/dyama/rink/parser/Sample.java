package jp.gr.java_conf.dyama.rink.parser;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Parsing Sample
 * @author Hiroyasu Yamada
 *
 */
public interface Sample {


    /**
     * parse one step  at the current position
     * @return true if the sample can be parsed. return false if the sample can not be parsed any more.
     */
    public boolean parseOneStep();

    /**
     * prepare to re-parse the current sentence
     */
    public void reparse();

    /**
     * prepare to re-parse the current sentence, and set the start position
     * @param start
     */
    public void reparse(int start);

    /**
     * get the sentence
     * @return sentence
     */
    public Sentence getSentence();

    /**
     * read a new sentence and setup.
     * @return true if the reader can read  a new sentence and setup. return false if there is no sentence.
     * @throws IOException if the reader fail to read a sentence from input stream.
     */
    public boolean read() throws IOException;

    /**
     * show parsing result
     * @param out output stream. do nothing if out is null.
     */
    public void show(PrintStream out) ;

}
