package jp.gr.java_conf.dyama.rink.parser.core;

import java.util.Arrays;

import jp.gr.java_conf.dyama.rink.parser.Sentence;
import jp.gr.java_conf.dyama.rink.parser.Word;

public class SentenceImpl implements Sentence {

    static final int DEFAULT_CAPACITY = 100;
    private WordImpl.Generator word_generator_ ;
    private WordImpl[] words_;
    private int size_;
    private String string_;

    /**
     * Constructor
     * @param generator word generator. throw IllegalArgumentException if generator is null.
     * @param string sentence surface.
     */
    SentenceImpl(WordImpl.Generator generator, String string){
        if (generator == null)
            throw new IllegalArgumentException("the word generator is null.");

        word_generator_ = generator ;
        words_ = new WordImpl[DEFAULT_CAPACITY];
        size_ = 0;
        string_ = string;
    }

    /**
     * get the word generator
     * @return word generator
     */
    WordImpl.Generator getWordGenerator(){
        return word_generator_;
    }

    @Override
    public int size(){
        return size_;
    }

    int capacity(){
        return words_.length;
    }

    @Override
    public String getString(){
        return string_;
    }

    @Override
    public Word getWord(int i){
        if (i < 0 || i >= size_)
            throw new IllegalArgumentException("the index is out of range.");
        return words_[i];
    }

    /**
     * clear the sentence.
     */
    void clear(){
        resize();
        size_ = 0 ;
        string_ = null;
    }


    /**
     * get reusable word
     * @return word. return null if there is no reusable word.
     */
    WordImpl getReusableWord(){
        if (size_ < words_.length)
            return words_[size_];
        return null;
    }

    /**
     * add a word
     * @param word throw IllegalArgumentException if word is null.
     */
    void addWord(WordImpl word){
        if (word == null)
            throw new IllegalArgumentException("the word is null.");
        if (size_ == words_.length)
            words_ = Arrays.copyOf(words_, words_.length + DEFAULT_CAPACITY);
        words_[size_++] = word ;
    }

    /**
     * resize word buffer
     * words_ is resize DEFAULT_CAPACITY if wors_.length is greater than DEFAULT_CAPACITY * 2 when clear() method is called.
     */
    private void resize(){
        if (words_.length > DEFAULT_CAPACITY * 2)
            words_ = Arrays.copyOf(words_, DEFAULT_CAPACITY);

    }

}
