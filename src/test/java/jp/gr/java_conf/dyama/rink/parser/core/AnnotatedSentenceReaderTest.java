package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;


import java.io.File;
import java.io.IOException;

import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.SentenceImpl;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader.AnnotationLevel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AnnotatedSentenceReaderTest {
    static final File valid           = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/AnnotatedSentenceReader.valid.txt");
    static final File invalid_parent  = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/AnnotatedSentenceReader.invalid.invalid_parent.txt");
    static final File invalid_unknown = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/AnnotatedSentenceReader.invalid.unknown_pos.txt");
    static final File invalid_columns = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/AnnotatedSentenceReader.invalid.2columns.txt");
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnnotatedSentenceReader() throws IOException {
        try {
            new AnnotatedSentenceReader(null, AnnotationLevel.DEPENDENCY);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the path is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            new AnnotatedSentenceReader(valid.getAbsolutePath(), null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the level is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        AnnotatedSentenceReader reader = new AnnotatedSentenceReader(valid.getAbsolutePath(), AnnotationLevel.DEPENDENCY);
        reader.close();
    }

    @Test
    public void testRead00() throws IOException {
        {
            AnnotatedSentenceReader reader = new AnnotatedSentenceReader(valid.getAbsolutePath(), AnnotationLevel.DEPENDENCY);
            try {
                reader.read(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the sentence is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            assertEquals(true, reader.hasAnySentences());
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(4, sentence.size());
            {
                WordImpl w = (WordImpl) sentence.getWord(0);
                assertEquals("I", w.getSurface());
                assertEquals(PTB.POS.PRP, w.getPOS());
                assertEquals(1, w.getParent());
                assertEquals(0, w.getBegin());
                assertEquals(1, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(1);
                assertEquals("saw", w.getSurface());
                assertEquals(PTB.POS.VBD, w.getPOS());
                assertEquals(-1, w.getParent());
                assertEquals(2, w.getBegin());
                assertEquals(5, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(2);
                assertEquals("a", w.getSurface());
                assertEquals(PTB.POS.DT, w.getPOS());
                assertEquals(3, w.getParent());
                assertEquals(6, w.getBegin());
                assertEquals(7, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(3);
                assertEquals("girl", w.getSurface());
                assertEquals(PTB.POS.NN, w.getPOS());
                assertEquals(1, w.getParent());
                assertEquals(8, w.getBegin());
                assertEquals(12, w.getEnd());
            }

            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(4, sentence.size());
            {
                WordImpl w = (WordImpl) sentence.getWord(0);
                assertEquals("He", w.getSurface());
                assertEquals(PTB.POS.PRP, w.getPOS());
                assertEquals(1, w.getParent());
                assertEquals(0, w.getBegin());
                assertEquals(2, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(1);
                assertEquals("has", w.getSurface());
                assertEquals(PTB.POS.VBZ, w.getPOS());
                assertEquals(-1, w.getParent());
                assertEquals(3, w.getBegin());
                assertEquals(6, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(2);
                assertEquals("his", w.getSurface());
                assertEquals(PTB.POS.PRP$, w.getPOS());
                assertEquals(3, w.getParent());
                assertEquals(7, w.getBegin());
                assertEquals(10, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(3);
                assertEquals("house", w.getSurface());
                assertEquals(PTB.POS.NN, w.getPOS());
                assertEquals(1, w.getParent());
                assertEquals(11, w.getBegin());
                assertEquals(16, w.getEnd());
            }

            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(1, sentence.size());
            {
                WordImpl w = (WordImpl) sentence.getWord(0);
                assertEquals("Good", w.getSurface());
                assertEquals(PTB.POS.RB, w.getPOS());
                assertEquals(-1, w.getParent());
                assertEquals(0, w.getBegin());
                assertEquals(4, w.getEnd());
            }
            assertEquals(true, reader.hasAnySentences());
            assertEquals(false, reader.read(sentence));
            assertEquals(false, reader.hasAnySentences());

            reader.close();
        }
    }

    @Test
    public void testRead01() throws IOException {
        {
            AnnotatedSentenceReader reader = new AnnotatedSentenceReader(valid.getAbsolutePath(), AnnotationLevel.POS);
            try {
                reader.read(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the sentence is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            assertEquals(true, reader.hasAnySentences());
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(4, sentence.size());
            {
                WordImpl w = (WordImpl) sentence.getWord(0);
                assertEquals("I", w.getSurface());
                assertEquals(PTB.POS.PRP, w.getPOS());
                assertEquals(1, w.getParent());
                assertEquals(0, w.getBegin());
                assertEquals(1, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(1);
                assertEquals("saw", w.getSurface());
                assertEquals(PTB.POS.VBD, w.getPOS());
                assertEquals(-1, w.getParent());
                assertEquals(2, w.getBegin());
                assertEquals(5, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(2);
                assertEquals("a", w.getSurface());
                assertEquals(PTB.POS.DT, w.getPOS());
                assertEquals(3, w.getParent());
                assertEquals(6, w.getBegin());
                assertEquals(7, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(3);
                assertEquals("girl", w.getSurface());
                assertEquals(PTB.POS.NN, w.getPOS());
                assertEquals(1, w.getParent());
                assertEquals(8, w.getBegin());
                assertEquals(12, w.getEnd());
            }

            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(4, sentence.size());
            {
                WordImpl w = (WordImpl) sentence.getWord(0);
                assertEquals("He", w.getSurface());
                assertEquals(PTB.POS.PRP, w.getPOS());
                assertEquals(1, w.getParent());
                assertEquals(0, w.getBegin());
                assertEquals(2, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(1);
                assertEquals("has", w.getSurface());
                assertEquals(PTB.POS.VBZ, w.getPOS());
                assertEquals(-1, w.getParent());
                assertEquals(3, w.getBegin());
                assertEquals(6, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(2);
                assertEquals("his", w.getSurface());
                assertEquals(PTB.POS.PRP$, w.getPOS());
                assertEquals(3, w.getParent());
                assertEquals(7, w.getBegin());
                assertEquals(10, w.getEnd());
            }

            {
                WordImpl w = (WordImpl) sentence.getWord(3);
                assertEquals("house", w.getSurface());
                assertEquals(PTB.POS.NN, w.getPOS());
                assertEquals(1, w.getParent());
                assertEquals(11, w.getBegin());
                assertEquals(16, w.getEnd());
            }

            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(1, sentence.size());
            {
                WordImpl w = (WordImpl) sentence.getWord(0);
                assertEquals("Good", w.getSurface());
                assertEquals(PTB.POS.RB, w.getPOS());
                assertEquals(-1, w.getParent());
                assertEquals(0, w.getBegin());
                assertEquals(4, w.getEnd());
            }
            assertEquals(true, reader.hasAnySentences());
            assertEquals(false, reader.read(sentence));
            assertEquals(false, reader.hasAnySentences());

            reader.close();
        }
    }

    @Test
    public void testRead02() throws IOException {
        AnnotatedSentenceReader reader = new AnnotatedSentenceReader(invalid_parent.getAbsolutePath(), AnnotationLevel.DEPENDENCY);
        SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
        assertEquals(true, reader.hasAnySentences());
        try {
            reader.read(sentence);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("invalid format as a dependency parent: 3.1 at line 3.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testRead03() throws IOException {
        AnnotatedSentenceReader reader = new AnnotatedSentenceReader(invalid_parent.getAbsolutePath(), AnnotationLevel.POS);
        SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
        assertEquals(true, reader.hasAnySentences());
        try {
            reader.read(sentence);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("invalid format as a dependency parent: 3.1 at line 3.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testRead04() throws IOException {
        AnnotatedSentenceReader reader = new AnnotatedSentenceReader(invalid_columns.getAbsolutePath(), AnnotationLevel.DEPENDENCY);
        SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
        assertEquals(true, reader.hasAnySentences());
        reader.read(sentence);
        assertEquals(true, reader.hasAnySentences());
        try {
            reader.read(sentence);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("invalid columns at line 8.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
        reader.close();
    }

    @Test
    public void testRead05() throws IOException {
        AnnotatedSentenceReader reader = new AnnotatedSentenceReader(invalid_columns.getAbsolutePath(), AnnotationLevel.POS);
        SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
        assertEquals(true, reader.hasAnySentences());
        assertEquals(true, reader.read(sentence));
        assertEquals(true, reader.hasAnySentences());
        assertEquals(true, reader.read(sentence));
        assertEquals(4, sentence.size());
        {
            WordImpl w = (WordImpl) sentence.getWord(0);
            assertEquals("He", w.getSurface());
            assertEquals(PTB.POS.PRP, w.getPOS());
            assertEquals(1, w.getParent());
            assertEquals(0, w.getBegin());
            assertEquals(2, w.getEnd());
        }

        {
            WordImpl w = (WordImpl) sentence.getWord(1);
            assertEquals("has", w.getSurface());
            assertEquals(PTB.POS.VBZ, w.getPOS());
            assertEquals(-1, w.getParent());
            assertEquals(3, w.getBegin());
            assertEquals(6, w.getEnd());
        }

        {
            WordImpl w = (WordImpl) sentence.getWord(2);
            assertEquals("his", w.getSurface());
            assertEquals(PTB.POS.PRP$, w.getPOS());
            assertEquals(-1, w.getParent());
            assertEquals(7, w.getBegin());
            assertEquals(10, w.getEnd());
        }

        {
            WordImpl w = (WordImpl) sentence.getWord(3);
            assertEquals("house", w.getSurface());
            assertEquals(PTB.POS.NN, w.getPOS());
            assertEquals(1, w.getParent());
            assertEquals(11, w.getBegin());
            assertEquals(16, w.getEnd());
        }
        reader.close();
    }

    @Test
    public void testRead06() throws IOException {
        AnnotatedSentenceReader reader = new AnnotatedSentenceReader(invalid_unknown.getAbsolutePath(), AnnotationLevel.DEPENDENCY);
        SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
        assertEquals(true, reader.hasAnySentences());
        reader.read(sentence);
        assertEquals(true, reader.hasAnySentences());
        try {
            reader.read(sentence);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("undefined part-of-speech tag: PRN at line 9.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testRead07() throws IOException {
        AnnotatedSentenceReader reader = new AnnotatedSentenceReader(invalid_unknown.getAbsolutePath(), AnnotationLevel.POS);
        SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
        assertEquals(true, reader.hasAnySentences());
        reader.read(sentence);
        assertEquals(true, reader.hasAnySentences());
        try {
            reader.read(sentence);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("undefined part-of-speech tag: PRN at line 9.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
    }

}
