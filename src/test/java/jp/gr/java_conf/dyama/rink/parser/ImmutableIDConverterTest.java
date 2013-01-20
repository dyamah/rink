package jp.gr.java_conf.dyama.rink.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl.ImmutableIDConverter;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl.MutableIDConverter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImmutableIDConverterTest {

    @Before
    public void setUp() throws Exception {
        assertEquals(0, ImmutableIDConverter.UNDEFINED);
        assertEquals(10, ImmutableIDConverter.START);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testImmutableIDConverter() {

        { // No exception
            MutableIDConverter m_converter = new MutableIDConverter();
            new ImmutableIDConverter(m_converter);
        }

        {
            try {
                new ImmutableIDConverter(null);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("the converter is null.", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }

        }

    }

    @Test
    public void testConvert() {
        {
            ImmutableIDConverter converter = new ImmutableIDConverter(new MutableIDConverter());
            assertEquals(0, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(0, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert("foo"));
            assertEquals(0, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert("bar"));
            assertEquals(0, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert(""));
            assertEquals(0, converter.size());
        }

        {
            MutableIDConverter x = new MutableIDConverter();
            assertEquals(ImmutableIDConverter.START + 0, x.convert("foo"));
            assertEquals(ImmutableIDConverter.START + 1, x.convert("foobar"));
            assertEquals(ImmutableIDConverter.START + 2, x.convert("bar"));
            assertEquals(ImmutableIDConverter.START + 3, x.convert(""));

            ImmutableIDConverter converter = new ImmutableIDConverter(x);

            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 0, converter.convert("foo"));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 1, converter.convert("foobar"));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 2, converter.convert("bar"));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 3, converter.convert(""));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert("xxx"));
            assertEquals(4, converter.size());
        }

    }

    @Test
    public void testSerialize() throws IOException, ClassNotFoundException {

        File tmpfile = File.createTempFile("ImmutableIDConverteTest.testSerialize", ".tmp");
        {
            MutableIDConverter converter = new MutableIDConverter();
            assertEquals(0, converter.size());

            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(0, converter.size());
            assertEquals(MutableIDConverter.START + 0, converter.convert(""));
            assertEquals(1, converter.size());
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(1, converter.size());
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(2, converter.size());
            assertEquals(MutableIDConverter.START + 2, converter.convert("fooyou"));
            assertEquals(3, converter.size());
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(3, converter.size());
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(3, converter.size());
            assertEquals(MutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(3, converter.size());
            assertEquals(MutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(3, converter.size());
            assertEquals(MutableIDConverter.START + 3, converter.convert("fooyo"));
            assertEquals(4, converter.size());

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(new ImmutableIDConverter(converter));
            out.close();
        }

        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            ImmutableIDConverter converter = (ImmutableIDConverter) in.readObject();
            in.close();

            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 0, converter.convert(""));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 2, converter.convert("fooyou"));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 1, converter.convert("foo"));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert(null));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.START + 3, converter.convert("fooyo"));
            assertEquals(4, converter.size());
            assertEquals(ImmutableIDConverter.UNDEFINED, converter.convert("foo-yo"));
            assertEquals(4, converter.size());
        }
    }

}
