package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.dyama.rink.corpus.TagSet.PartOfSpeech;
import jp.gr.java_conf.dyama.rink.parser.Sentence;
import jp.gr.java_conf.dyama.rink.parser.SentenceReader;

/**
 * Simpel Sentence Reader
 * @author Hiroyasu Yamada
 *
 */
class SimpleSentenceReader implements SentenceReader {

    /** the list of words */
    List<WordImpl> words_ ;

    /** the word generator */
    WordImpl.Generator generator_ ;

    /**
     * Constructor
     * @param generator the word generator.
     * @throws IllegalArgumentException if the word generator is null.
     */
    SimpleSentenceReader(WordImpl.Generator generator){
        if (generator == null)
            throw new IllegalArgumentException("the word generator is null.");
        generator_ = generator ;
        words_ = new ArrayList<WordImpl>();
    }

    /**
     * Adds the word
     * @param surface the surface string of the added word.
     * @param begin the beginning position of the word.
     * @param end the ending position of the word.
     * @param pos the part of speech.
     * @param parentID the ID of the dependency parent.
     * @throws IllegalArgumentException if the surface is null.
     * @throws IllegalArgumentException if the beginning position is negative or less than the ending's one.
     */
    void addWord(String surface, int begin,  int end, PartOfSpeech pos, int parentID){
        words_.add(generator_.generate(surface, begin, end, pos, parentID, null));
    }

    @Override
    public boolean hasAnySentences() throws IOException {
        return words_.size() > 0;
    }

    @Override
    public boolean read(Sentence sentence) throws IOException {
        if (sentence == null)
            throw new IllegalArgumentException("the sentence is null.");

        synchronized(words_){
            if (words_.size() == 0)
                return false;

            SentenceImpl s = (SentenceImpl) sentence ;
            s.clear();
            for(WordImpl w : words_){
                s.addWord(w);
            }
            words_.clear();
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        words_.clear();
    }

}
