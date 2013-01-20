package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl.MutableIDConverter;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;

public class WordImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGenerator(){

        {
            try {
                new WordImpl.Generator(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the converter is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");

            }
        }
        {
            IDConverter converter = new MutableIDConverter();
            WordImpl.Generator generator = new WordImpl.Generator(converter);
            WordImpl word = generator.generate("foo", 0, 4, PTB.POS.CC, -1, null);
            assertEquals(MutableIDConverter.START + 0, word.getID());
            assertEquals(0, word.getBegin());
            assertEquals(4, word.getEnd());
            assertEquals(PTB.POS.CC, word.getPOS());
            assertEquals("foo", word.getSurface());
            assertEquals(true, word.getBaseForm() == null);
            assertEquals(-1, word.getParent());

            WordImpl w1 = generator.generate("bee", 0, 3, PTB.POS.VBD, 2, word);
            assertEquals(true, w1 == word);
            assertEquals(MutableIDConverter.START + 1, word.getID());
            assertEquals(0, word.getBegin());
            assertEquals(3, word.getEnd());
            assertEquals(PTB.POS.VBD, word.getPOS());
            assertEquals("bee", word.getSurface());
            assertEquals(true, word.getBaseForm() == null);
            assertEquals(2, word.getParent());


        }


        { // generate(): surface is null.
            IDConverter converter = new MutableIDConverter();
            WordImpl.Generator generator = new WordImpl.Generator(converter);

            try {
                generator.generate(null, 3, 4, PTB.POS.NN, -1, null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the surface is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");

            }
        }

        { // generator(): begin < 0
            IDConverter converter = new MutableIDConverter();
            WordImpl.Generator generator = new WordImpl.Generator(converter);

            try {
                generator.generate("foo", -1, 0, PTB.POS.CD, -1, null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the beginning of position is negative.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");

            }
        }
        { // generate(): end < begin
            IDConverter converter = new MutableIDConverter();
            WordImpl.Generator generator = new WordImpl.Generator(converter);
            try {
                generator.generate("foo", 1, 0, PTB.POS.IN, -1, null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the end of position is less than the beginning one.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }
    }

    @Test
    public void testLength() {
        IDConverter converter = new MutableIDConverter();
        WordImpl.Generator generator = new WordImpl.Generator(converter);
        WordImpl word = generator.generate("foo", 0, 4, PTB.POS.CC, -1, null);
        assertEquals(4, word.length());
    }

}
