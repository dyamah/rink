package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.Sentence;
import jp.gr.java_conf.dyama.rink.parser.SentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.CoNLLXSentenceReader.Mode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CoNLLXSentenceReaderTest {
    static final File valid_columns7      = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/CoNLLXSentenceReader.valid.columns7.txt");
    static final File valid_columns6      = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/CoNLLXSentenceReader.valid.columns6.txt");
    static final File invalid_columns5    = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/CoNLLXSentenceReader.invalid.columns5.txt");
    static final File invalid_head1       = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/CoNLLXSentenceReader.invalid.head1.txt");
    static final File invalid_head2       = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/CoNLLXSentenceReader.invalid.head2.txt");
    static final File invalid_unknown_pos = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/CoNLLXSentenceReader.invalid.unknown_pos.txt");
    static final File noExist             = new File("src/test/java/jp/gr/java_conf/dyama/rink/parser/core/testcases/Foo.txt");

    @Before
    public void setUp() throws Exception {
        assertEquals(false, noExist.exists());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCoNLLXSentenceReader() {

        try {
            new CoNLLXSentenceReader(null, CoNLLXSentenceReader.Mode.TRAIN);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the path is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            new CoNLLXSentenceReader(valid_columns7.getAbsolutePath(), null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the mode is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }


        try {
            new CoNLLXSentenceReader(noExist.getAbsolutePath(), Mode.TRAIN);
            fail("");
        } catch (FileNotFoundException e) {
            assertNotNull(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testHasAnySentences() throws IOException {
        {
            SentenceReader reader = new CoNLLXSentenceReader(valid_columns7.getAbsolutePath(), Mode.TRAIN);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(false, reader.hasAnySentences());
            reader.close();
        }

        {
            SentenceReader reader = new CoNLLXSentenceReader(valid_columns7.getAbsolutePath(), Mode.TEST);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(true, reader.hasAnySentences());
            reader.read(sentence);
            assertEquals(true, reader.hasAnySentences());
            reader.close();
        }

    }

    @Test
    public void testRead00() throws IOException {
        SentenceReader reader = new CoNLLXSentenceReader(valid_columns7.getAbsolutePath(), Mode.TRAIN);
        SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
        assertEquals(0, sentence.size());
        assertEquals(true, reader.read(sentence));
        assertEquals(4, sentence.size());
        {
            WordImpl word = (WordImpl) sentence.getWord(0);
            assertEquals("I", word.getSurface());
            assertEquals(PTB.POS.PRP, word.getPOS());
            assertEquals(1, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(1);
            assertEquals("saw", word.getSurface());
            assertEquals(PTB.POS.VBD, word.getPOS());
            assertEquals(-1, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(2);
            assertEquals("a", word.getSurface());
            assertEquals(PTB.POS.DT, word.getPOS());
            assertEquals(3, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(3);
            assertEquals("girl", word.getSurface());
            assertEquals(PTB.POS.NN, word.getPOS());
            assertEquals(1, word.getParent());
        }

        assertEquals(true, reader.read(sentence));
        assertEquals(4, sentence.size());
        {
            WordImpl word = (WordImpl) sentence.getWord(0);
            assertEquals("He", word.getSurface());
            assertEquals(PTB.POS.PRP, word.getPOS());
            assertEquals(1, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(1);
            assertEquals("has", word.getSurface());
            assertEquals(PTB.POS.VBZ, word.getPOS());
            assertEquals(-1, word.getParent());
        }

        {
            WordImpl word = (WordImpl) sentence.getWord(2);
            assertEquals("his", word.getSurface());
            assertEquals(PTB.POS.PRP$, word.getPOS());
            assertEquals(3, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(3);
            assertEquals("house", word.getSurface());
            assertEquals(PTB.POS.NN, word.getPOS());
            assertEquals(1, word.getParent());
        }

        assertEquals(true, reader.read(sentence));
        assertEquals(1, sentence.size());
        {
            WordImpl word = (WordImpl) sentence.getWord(0);
            assertEquals("Good", word.getSurface());
            assertEquals(PTB.POS.RB, word.getPOS());
            assertEquals(-1, word.getParent());
        }
        assertEquals(false, reader.read(sentence));
        reader.close();
    }

    @Test
    public void testRead01() throws IOException {
        SentenceReader reader = new CoNLLXSentenceReader(valid_columns6.getAbsolutePath(), Mode.TRAIN);
        SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
        assertEquals(0, sentence.size());
        try {
            assertEquals(true, reader.read(sentence));
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("invalid columns at line 1.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
        reader.close();
    }

    @Test
    public void testRead02() throws IOException {
        SentenceReader reader = new CoNLLXSentenceReader(valid_columns6.getAbsolutePath(), Mode.TEST);
        SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
        assertEquals(0, sentence.size());
        assertEquals(true, reader.read(sentence));
        assertEquals(4, sentence.size());
        {
            WordImpl word = (WordImpl) sentence.getWord(0);
            assertEquals("I", word.getSurface());
            assertEquals(PTB.POS.PRP, word.getPOS());
            assertEquals(-1, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(1);
            assertEquals("saw", word.getSurface());
            assertEquals(PTB.POS.VBD, word.getPOS());
            assertEquals(-1, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(2);
            assertEquals("a", word.getSurface());
            assertEquals(PTB.POS.DT, word.getPOS());
            assertEquals(-1, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(3);
            assertEquals("girl", word.getSurface());
            assertEquals(PTB.POS.NN, word.getPOS());
            assertEquals(-1, word.getParent());
        }

        assertEquals(true, reader.read(sentence));
        assertEquals(4, sentence.size());
        {
            WordImpl word = (WordImpl) sentence.getWord(0);
            assertEquals("He", word.getSurface());
            assertEquals(PTB.POS.PRP, word.getPOS());
            assertEquals(-1, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(1);
            assertEquals("has", word.getSurface());
            assertEquals(PTB.POS.VBZ, word.getPOS());
            assertEquals(-1, word.getParent());
        }

        {
            WordImpl word = (WordImpl) sentence.getWord(2);
            assertEquals("his", word.getSurface());
            assertEquals(PTB.POS.PRP$, word.getPOS());
            assertEquals(-1, word.getParent());
        }
        {
            WordImpl word = (WordImpl) sentence.getWord(3);
            assertEquals("house", word.getSurface());
            assertEquals(PTB.POS.NN, word.getPOS());
            assertEquals(-1, word.getParent());
        }

        assertEquals(true, reader.read(sentence));
        assertEquals(1, sentence.size());
        {
            WordImpl word = (WordImpl) sentence.getWord(0);
            assertEquals("Good", word.getSurface());
            assertEquals(PTB.POS.RB, word.getPOS());
            assertEquals(-1, word.getParent());
        }
        assertEquals(false, reader.read(sentence));
        reader.close();
    }

    @Test
    public void testRead03() throws IOException {
        {
            SentenceReader reader = new CoNLLXSentenceReader(invalid_columns5.getAbsolutePath(), Mode.TRAIN);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            try {
                reader.read(sentence);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("invalid columns at line 4.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            reader.close();
        }
        {
            SentenceReader reader = new CoNLLXSentenceReader(invalid_columns5.getAbsolutePath(), Mode.TEST);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            try {
                reader.read(sentence);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("invalid columns at line 4.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            reader.close();
        }
    }
    @Test
    public void testRead04() throws IOException {
        {
            SentenceReader reader = new CoNLLXSentenceReader(invalid_head1.getAbsolutePath(), Mode.TRAIN);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            assertEquals(true, reader.read(sentence));
            assertEquals(4, sentence.size());
            try {
                reader.read(sentence);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("invalid head ID: _ at line 8.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            reader.close();
        }
        {
            SentenceReader reader = new CoNLLXSentenceReader(invalid_head1.getAbsolutePath(), Mode.TEST);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            assertEquals(true, reader.read(sentence));
            assertEquals(4, sentence.size());
            assertEquals(true, reader.read(sentence));
            assertEquals(4, sentence.size());
            reader.close();
        }
    }

    @Test
    public void testRead05() throws IOException {
        {
            SentenceReader reader = new CoNLLXSentenceReader(invalid_head2.getAbsolutePath(), Mode.TRAIN);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            assertEquals(true, reader.read(sentence));
            assertEquals(4, sentence.size());
            try {
                reader.read(sentence);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("invalid head ID: -1 at line 7.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            reader.close();
        }
        {
            SentenceReader reader = new CoNLLXSentenceReader(invalid_head2.getAbsolutePath(), Mode.TEST);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            assertEquals(true, reader.read(sentence));
            assertEquals(4, sentence.size());
            assertEquals(true, reader.read(sentence));
            assertEquals(4, sentence.size());
            reader.close();
        }
    }

    @Test
    public void testRead06() throws IOException {
        {
            SentenceReader reader = new CoNLLXSentenceReader(invalid_unknown_pos.getAbsolutePath(), Mode.TRAIN);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            assertEquals(true, reader.read(sentence));
            assertEquals(4, sentence.size());
            try {
                reader.read(sentence);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("undefined part-of-speech tag: PRN at line 9.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            reader.close();
        }
        {
            SentenceReader reader = new CoNLLXSentenceReader(invalid_unknown_pos.getAbsolutePath(), Mode.TEST);
            SentenceImpl sentence = new SentenceImpl(new WordImpl.Generator(new IDConverterImpl.MutableIDConverter()), null);
            assertEquals(0, sentence.size());
            assertEquals(true, reader.read(sentence));
            assertEquals(4, sentence.size());
            try {
                reader.read(sentence);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("undefined part-of-speech tag: PRN at line 9.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            reader.close();
        }
    }

    @Test
    public void testClose() {
        SentenceReader reader = null;
        try {
            reader = new CoNLLXSentenceReader(valid_columns7.getAbsolutePath(), Mode.TRAIN);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("");
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("");
        }
    }
}
