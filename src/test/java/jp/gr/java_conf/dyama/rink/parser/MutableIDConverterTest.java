package jp.gr.java_conf.dyama.rink.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl.ImmutableIDConverter;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl.MutableIDConverter;

public class MutableIDConverterTest {

    @Before
    public void setUp() throws Exception {
        assertEquals( 0, MutableIDConverter.UNDEFINED);
        assertEquals(10, MutableIDConverter.START);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMutableIDConverter() {

        MutableIDConverter converter = new MutableIDConverter();
        assertEquals( 0, converter.size());

        assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
        assertEquals( 0, converter.size());
        assertEquals(MutableIDConverter.START + 0, converter.convert(""));
        assertEquals( 1, converter.size());
        assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
        assertEquals( 1, converter.size());
        assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
        assertEquals( 2, converter.size());
        assertEquals(MutableIDConverter.START + 2, converter.convert("fooyou"));
        assertEquals( 3, converter.size());
        assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
        assertEquals( 3, converter.size());
        assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
        assertEquals( 3, converter.size());
        assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
        assertEquals( 3, converter.size());
        assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
        assertEquals( 3, converter.size());
        assertEquals(MutableIDConverter.START + 3, converter.convert("fooyo"));
        assertEquals( 4, converter.size());
    }

    @Test
    public void testToImmutable() {
        {
            MutableIDConverter converter = new MutableIDConverter();
            ImmutableIDConverter im = converter.toImmutable();
            assertEquals(MutableIDConverter.UNDEFINED, im.convert(""));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert(null));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert("foo"));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert("barbar"));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert("foo"));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert("gg"));

            assertEquals(MutableIDConverter.START + 0, converter.convert(""));
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(MutableIDConverter.START + 2, converter.convert("barbar"));
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(MutableIDConverter.START + 3, converter.convert("gg"));

            assertEquals(MutableIDConverter.UNDEFINED, im.convert(""));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert(null));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert("foo"));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert("barbar"));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert("foo"));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert("gg"));
        }

        {
            MutableIDConverter converter = new MutableIDConverter();


            assertEquals(MutableIDConverter.START + 0, converter.convert(""));
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(MutableIDConverter.START + 2, converter.convert("barbar"));
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(3, converter.size());

            ImmutableIDConverter im = converter.toImmutable();
            assertEquals(0, converter.size());
            assertEquals(MutableIDConverter.START + 0, im.convert(""));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert(null));
            assertEquals(MutableIDConverter.START + 1, im.convert("foo"));
            assertEquals(MutableIDConverter.START + 2, im.convert("barbar"));
            assertEquals(MutableIDConverter.START + 1, im.convert("foo"));
            assertEquals(MutableIDConverter.UNDEFINED, im.convert("gg"));

            assertEquals(MutableIDConverter.START + 0, converter.convert("gg"));
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(MutableIDConverter.START + 1, converter.convert("barbar"));
            assertEquals(MutableIDConverter.START + 2, converter.convert("foo"));
            assertEquals(MutableIDConverter.START + 2, converter.convert("foo"));
            assertEquals(MutableIDConverter.START + 0, converter.convert("gg"));
        }


    }

    @Test
    public void testSerialize() throws IOException, ClassNotFoundException {

        File tmpfile = File.createTempFile("MutableIDConverteTest.testSerialize", ".tmp");
        tmpfile.deleteOnExit();
        {
            MutableIDConverter converter = new MutableIDConverter();
            assertEquals( 0, converter.size());

            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals( 0, converter.size());
            assertEquals(MutableIDConverter.START + 0, converter.convert(""));
            assertEquals( 1, converter.size());
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals( 1, converter.size());
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals( 2, converter.size());
            assertEquals(MutableIDConverter.START + 2, converter.convert("fooyou"));
            assertEquals( 3, converter.size());
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals( 3, converter.size());
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals( 3, converter.size());
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals( 3, converter.size());
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals( 3, converter.size());
            assertEquals(MutableIDConverter.START + 3, converter.convert("fooyo"));
            assertEquals( 4, converter.size());

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(converter);
            out.close();
        }

        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            MutableIDConverter converter = (MutableIDConverter)in.readObject();
            in.close();

            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.START + 0, converter.convert(""));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.START + 2, converter.convert("fooyou"));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.START + 3, converter.convert("fooyo"));
            assertEquals( 4, converter.size());
            assertEquals(MutableIDConverter.START + 4, converter.convert("foo-yo"));
            assertEquals( 5, converter.size());

        }

    }

}
