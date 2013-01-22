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

    /** list of words */
    List<WordImpl> words_ ;

    /** word generator */
    WordImpl.Generator generator_ ;

    /**
     * Constructor
     * @param generator word generator. throw IllegalArgumentException if generator is null.
     */
    SimpleSentenceReader(WordImpl.Generator generator){
        if (generator == null)
            throw new IllegalArgumentException("the word generator is null.");
        generator_ = generator ;
        words_ = new ArrayList<WordImpl>();
    }

    /**
     * add a word
     * @param surface surface string of the word. throw IllegalArgumentException if surface is null.
     * @param begin the beginning position of the word. throw IllegalArgumentException if begin is negative or less than the end.
     * @param end the ending position of the word.
     * @param pos part of speech.
     * @param parent parent dependency parent ID.
     */
    void addWord(String surface, int begin,  int end, PartOfSpeech pos, int parent){
        words_.add(generator_.generate(surface, begin, end, pos, parent, null));
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
    }

}
