package jp.gr.java_conf.dyama.rink.parser;

import java.io.IOException;

public interface SentenceReader {

    /**
     * check whether the reader can read any sentences or not.
     * @return true if the reader can read some sentences, otherwise false
     */
    boolean hasAnySentences() throws IOException ;

    /**
     * read a sentence
     * @param sentence throw IllegalArgumentException if sentence is null.
     * @return true if the reader can read a sentence. return false if no sentence is read.
     */
    public boolean read(Sentence sentence) throws IOException ;

}
