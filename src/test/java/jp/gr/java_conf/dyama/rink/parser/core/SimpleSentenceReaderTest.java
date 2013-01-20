package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.IOException;

import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.Word;
import jp.gr.java_conf.dyama.rink.parser.core.SentenceImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SimpleSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleSentenceReaderTest {
    WordImpl.Generator generator_ ;

    @Before
    public void setUp() throws Exception {
        generator_ = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSimpleSentenceReader() throws IOException {
        {
            try {
                new SimpleSentenceReader(null);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("the word generator is null.", e.getMessage());
            } catch (Exception  e){
                e.printStackTrace();
                fail("");
            }
        }

        {
            SimpleSentenceReader reader = new SimpleSentenceReader(generator_);
            assertEquals(false, reader.hasAnySentences());
        }
    }

    @Test
    public void testAddWord() throws IOException {
        SimpleSentenceReader reader = new SimpleSentenceReader(generator_);
        try {
            reader.addWord(null, 0, 1, PTB.POS.DT, -1);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the surface is null.", e.getMessage());
        } catch (Exception  e){
            e.printStackTrace();
            fail("");
        }

        try {
            reader.addWord("foo", -1, 1, PTB.POS.DT, -1);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the beginning of position is negative.", e.getMessage());
        } catch (Exception  e){
            e.printStackTrace();
            fail("");
        }

        try {
            reader.addWord("foo", 1, 0, PTB.POS.DT, -1);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the end of position is less than the beginning one.", e.getMessage());
        } catch (Exception  e){
            e.printStackTrace();
            fail("");
        }


        assertEquals(false, reader.hasAnySentences());
        reader.addWord("foo", 0, 3,PTB.POS.NN, -1);
        reader.addWord("foo", 0, 3, null, -1);
        assertEquals(true, reader.hasAnySentences());
    }

    @Test
    public void testRead() throws IOException {
        SimpleSentenceReader reader = new SimpleSentenceReader(generator_);
        reader.addWord("foo", 0, 3, PTB.POS.NN, -1);
        reader.addWord("bar", 4, 8, PTB.POS.CC, -1);
        SentenceImpl sentence = new SentenceImpl(generator_, null);
        try {
            reader.read(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the sentence is null.", e.getMessage());
        } catch (Exception  e){
            e.printStackTrace();
            fail("");
        }

        assertEquals(0, sentence.size());
        assertEquals(true, reader.hasAnySentences());
        reader.read(sentence);
        assertEquals(false, reader.hasAnySentences());
        assertEquals(2, sentence.size());
        {
            Word word = sentence.getWord(0);
            assertEquals("foo", word.getSurface());
            assertEquals(0, word.getBegin());
            assertEquals(3, word.getEnd());
            assertEquals(PTB.POS.NN, word.getPOS());
        }

        {
            Word word = sentence.getWord(1);
            assertEquals("bar", word.getSurface());
            assertEquals(4, word.getBegin());
            assertEquals(8, word.getEnd());
            assertEquals(PTB.POS.CC, word.getPOS());
        }




    }

}
