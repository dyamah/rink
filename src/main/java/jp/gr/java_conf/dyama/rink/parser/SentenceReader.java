package jp.gr.java_conf.dyama.rink.parser;

import java.io.IOException;

public interface SentenceReader {

    /**
     * check whether the reader can read any sentences or not.
     * @return true if the reader can read some sentences, otherwise false
     */
    boolean hasAnySentences() throws IOException ;

    /**
     * read one sentence.
     * @param sentence the object to save the reading sentence.
     * @return true if the reader can read a sentence, otherwise false.
     * @throws IllegalArgumentException if the sentence is null.
     * @throws IOException if it fails to read.
     */
    public boolean read(Sentence sentence) throws IOException ;


    /**
     * close the reader.
     * @throws IOException if it fails to close.
     */
    public void close() throws IOException;

}
