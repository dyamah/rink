package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SentenceImpl;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SentenceImplTest {

    @Before
    public void setUp() throws Exception {
        assertEquals(100, SentenceImpl.DEFAULT_CAPACITY) ;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSentenceImpl() {
        WordImpl.Generator generator = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());

        {
            SentenceImpl sentence = new SentenceImpl(generator, null);
            assertEquals(0, sentence.size());
            assertEquals(null, sentence.getString());
            assertEquals(null, sentence.getReusableWord());
            assertEquals(SentenceImpl.DEFAULT_CAPACITY,  sentence.capacity());
        }

        {
            SentenceImpl sentence = new SentenceImpl(generator, "who are you?");
            assertEquals(0, sentence.size());
            assertEquals("who are you?", sentence.getString());
            assertEquals(null, sentence.getReusableWord());
        }

        {
            try {
                new SentenceImpl(null, "who are you?");
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("the word generator is null.", e.getMessage());
            } catch (Exception e ){
                e.printStackTrace();
                fail("");
            }
        }
    }

    @Test
    public void testGetWordGenerator() {
        WordImpl.Generator generator = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());
        SentenceImpl sentence = new SentenceImpl(generator, null);
        assertEquals(true, generator == sentence.getWordGenerator() );
    }

    @Test
    public void testGetString() {
        WordImpl.Generator generator = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());
        {
            SentenceImpl sentence = new SentenceImpl(generator, "");
            assertEquals("", sentence.getString());
        }

        {
            SentenceImpl sentence = new SentenceImpl(generator, "xx xx xx");
            assertEquals("xx xx xx", sentence.getString());
        }

        {
            SentenceImpl sentence = new SentenceImpl(generator, null);
            assertEquals(null, sentence.getString());
        }
    }

    @Test
    public void testGetWord() {
        WordImpl.Generator generator = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());
        {
            SentenceImpl sentence = new SentenceImpl(generator, "foo");

            try {
                sentence.getWord(0);
                fail("");
            } catch (IllegalArgumentException  e) {
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

            try {
                sentence.getWord(-1);
                fail("");
            } catch (IllegalArgumentException  e) {
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

        }

        {
            SentenceImpl sentence = new SentenceImpl(generator, null);
            assertEquals(0, sentence.size());
            assertEquals(null, sentence.getString());
            WordImpl word0 = generator.generate("word0",     0,  5, PTB.POS.CC, -1, null);
            WordImpl word1 = generator.generate("word 1",    6, 12, PTB.POS.DT, -2, null);
            WordImpl word2 = generator.generate("word  2",  13, 20, PTB.POS.NN, -3, null);
            WordImpl word3 = generator.generate("word   3", 21, 29, PTB.POS.IN, -4, null);
            sentence.addWord(word0);
            sentence.addWord(word1);
            sentence.addWord(word2);
            sentence.addWord(word3);
            assertEquals(4, sentence.size());
            try {
                sentence.getWord(-1);
                fail("");
            } catch (IllegalArgumentException  e) {
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            assertEquals(true, word0 == sentence.getWord(0));
            assertEquals(true, word1 == sentence.getWord(1));
            assertEquals(true, word2 == sentence.getWord(2));
            assertEquals(true, word3 == sentence.getWord(3));

            try {
                sentence.getWord(4);
                fail("");
            } catch (IllegalArgumentException  e) {
                assertEquals("the index is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

        }
    }

    @Test
    public void testClear() {
        WordImpl.Generator generator = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());
        {
            SentenceImpl sentence = new SentenceImpl(generator, "foo");
            assertEquals(0, sentence.size());
            assertEquals("foo", sentence.getString());
            sentence.clear();
            assertEquals(0, sentence.size());
            assertEquals(null, sentence.getString());
        }

        {
            SentenceImpl sentence = new SentenceImpl(generator, null);
            assertEquals(0, sentence.size());
            assertEquals(null, sentence.getString());
            sentence.clear();
            assertEquals(0, sentence.size());
            assertEquals(null, sentence.getString());
        }

        {
            SentenceImpl sentence = new SentenceImpl(generator, null);
            assertEquals(0, sentence.size());
            assertEquals(null, sentence.getString());
            WordImpl word0 = generator.generate("word0",     0,  5, PTB.POS.CC, -1, null);
            WordImpl word1 = generator.generate("word 1",    6, 12, PTB.POS.DT, -2, null);
            WordImpl word2 = generator.generate("word  2",  13, 20, PTB.POS.NN, -3, null);
            WordImpl word3 = generator.generate("word   3", 21, 29, PTB.POS.IN, -4, null);
            sentence.addWord(word0);
            sentence.addWord(word1);
            sentence.addWord(word2);
            sentence.addWord(word3);
            assertEquals(4, sentence.size());
            assertEquals(null, sentence.getString());
            sentence.clear();
            assertEquals(0, sentence.size());
            assertEquals(null, sentence.getString());
        }

        {
            SentenceImpl sentence = new SentenceImpl(generator, null);
            assertEquals(0, sentence.size());
            assertEquals(null, sentence.getString());
            for(int i = 0 ; i < SentenceImpl.DEFAULT_CAPACITY * 2 ; i ++ )
                sentence.addWord(generator.generate("word0", i,  i, PTB.POS.CC, -1, null));

            assertEquals(SentenceImpl.DEFAULT_CAPACITY * 2, sentence.size());
            assertEquals(SentenceImpl.DEFAULT_CAPACITY * 2, sentence.capacity());

            sentence.clear();

            assertEquals(0, sentence.size());
            assertEquals(SentenceImpl.DEFAULT_CAPACITY * 2, sentence.capacity());

            for(int i = 0 ; i < SentenceImpl.DEFAULT_CAPACITY * 2 + 1; i ++ )
                sentence.addWord(generator.generate("word0", i,  i, PTB.POS.CC, -1, null));

            assertEquals(SentenceImpl.DEFAULT_CAPACITY * 2 + 1, sentence.size());
            assertEquals(SentenceImpl.DEFAULT_CAPACITY * 3, sentence.capacity());

            sentence.clear();

            assertEquals(0, sentence.size());
            assertEquals(SentenceImpl.DEFAULT_CAPACITY, sentence.capacity());


        }
    }

    @Test
    public void testGetReusableWord() {
        WordImpl.Generator generator = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());
        {
            SentenceImpl sentence = new SentenceImpl(generator, null);
            assertEquals(null, sentence.getReusableWord());
            assertEquals(null, sentence.getReusableWord());
            assertEquals(null, sentence.getReusableWord());
        }

        {
            SentenceImpl sentence = new SentenceImpl(generator, null);
            WordImpl word0 = generator.generate("word0",     0,  5, PTB.POS.CC, -1, null);
            WordImpl word1 = generator.generate("word 1",    6, 12, PTB.POS.DT, -2, null);
            WordImpl word2 = generator.generate("word  2",  13, 20, PTB.POS.NN, -3, null);
            WordImpl word3 = generator.generate("word   3", 21, 29, PTB.POS.IN, -4, null);
            sentence.addWord(word0);
            sentence.addWord(word1);
            sentence.addWord(word2);
            sentence.addWord(word3);
            assertEquals(null, sentence.getReusableWord());

            sentence.clear();
            assertEquals(true, word0 == sentence.getReusableWord());
            sentence.addWord(word0);
            assertEquals(true, word1 == sentence.getReusableWord());
            sentence.addWord(word1);
            assertEquals(true, word2 == sentence.getReusableWord());
            sentence.addWord(word2);
            assertEquals(true, word3 == sentence.getReusableWord());
            sentence.addWord(word3);
            assertEquals(true, sentence.getReusableWord() == null);
        }
    }

    @Test
    public void testAddWord() {
        WordImpl.Generator generator = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());
        {

            SentenceImpl sentence = new SentenceImpl(generator, null);
            WordImpl word0 = generator.generate("word0",     0,  5, PTB.POS.CC, -1, null);
            WordImpl word1 = generator.generate("word 1",    6, 12, PTB.POS.DT, -2, null);
            WordImpl word2 = generator.generate("word  2",  13, 20, PTB.POS.NN, -3, null);
            WordImpl word3 = generator.generate("word   3", 21, 29, PTB.POS.IN, -4, null);
            assertEquals(0, sentence.size());
            sentence.addWord(word0);
            sentence.addWord(word2);
            sentence.addWord(word1);
            sentence.addWord(word3);
            assertEquals(4, sentence.size());
            assertEquals(true, word0 == sentence.getWord(0));
            assertEquals(true, word2 == sentence.getWord(1));
            assertEquals(true, word1 == sentence.getWord(2));
            assertEquals(true, word3 == sentence.getWord(3));

            try {
                sentence.addWord(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the word is null.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
        }

        {
            SentenceImpl sentence = new SentenceImpl(generator, null);
            WordImpl[] words = new WordImpl[SentenceImpl.DEFAULT_CAPACITY];
            for(int  i = 0 ; i < SentenceImpl.DEFAULT_CAPACITY; i++)
                words[i] = generator.generate("word" + i,     i,  i, PTB.POS.CC, -1, null);

            for(WordImpl w : words){
                sentence.addWord(w);
            }

            WordImpl w = generator.generate("xxx",   999,  999, PTB.POS.CC, -1, null);
            assertEquals(SentenceImpl.DEFAULT_CAPACITY, sentence.capacity());
            sentence.addWord(w);
            assertEquals(SentenceImpl.DEFAULT_CAPACITY * 2, sentence.capacity());
            for(int  i = 0 ; i < SentenceImpl.DEFAULT_CAPACITY; i++)
                assertEquals(true, words[i] == sentence.getWord(i));
            assertEquals(true, w == sentence.getWord(SentenceImpl.DEFAULT_CAPACITY) );
        }
    }

}
