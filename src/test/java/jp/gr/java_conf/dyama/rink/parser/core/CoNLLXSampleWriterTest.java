package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImplTest.DummyParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CoNLLXSampleWriterTest {
    SimpleSentenceReader reader_;
    IDConverterImpl.MutableIDConverter idconverter_;
    DummyParser dummy_parser_;

    File tmpfile_;
    CoNLLXSampleWriter writer_;
    @Before
    public void setUp() throws Exception {
        idconverter_ = new IDConverterImpl.MutableIDConverter();
        reader_ = new SimpleSentenceReader(new WordImpl.Generator(idconverter_));
        dummy_parser_ = new DummyParser();

        reader_.addWord("I", 0, 1, PTB.POS.PRP, 1);
        reader_.addWord("saw", 0, 1, PTB.POS.VBD, -1);
        reader_.addWord("a", 0, 1, PTB.POS.DT, 3);
        reader_.addWord("girl", 0, 1, PTB.POS.NN, 1);
        reader_.addWord("with", 0, 1, PTB.POS.IN, 3);
        reader_.addWord("a", 0, 1, PTB.POS.DT, 6);
        reader_.addWord("telescope", 0, 1, PTB.POS.NN, 4);

        tmpfile_ = File.createTempFile("CoNLLXSampleWriterTest", ".tmp");
        writer_ = new CoNLLXSampleWriter();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testWrite() {


        try {
            writer_.write(null, System.out);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the sample is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

    }

    @Test
    public void testWrite00() throws IOException{

        tmpfile_.deleteOnExit();
        {
            SampleImpl sample = new SampleImpl(reader_, idconverter_);
            PrintStream out = new PrintStream(tmpfile_);
            PrintStream n = null;


            writer_.write(sample, n);
            out.close();

            BufferedReader in = new BufferedReader(new FileReader(tmpfile_));
            assertEquals(null, in.readLine());
            in.close();
        }

        {
            SampleImpl sample = new SampleImpl(reader_, idconverter_);
            sample.read();
            PrintStream out = new PrintStream(tmpfile_);
            writer_.write(sample, out);
            out.close();
            BufferedReader in = new BufferedReader(new FileReader(tmpfile_));

            assertEquals("1\tI\t_\tPRP\tPRP\t_\t0", in.readLine());
            assertEquals("2\tsaw\t_\tVBD\tVBD\t_\t0", in.readLine());
            assertEquals("3\ta\t_\tDT\tDT\t_\t0", in.readLine());
            assertEquals("4\tgirl\t_\tNN\tNN\t_\t0", in.readLine());
            assertEquals("5\twith\t_\tIN\tIN\t_\t0", in.readLine());
            assertEquals("6\ta\t_\tDT\tDT\t_\t0", in.readLine());
            assertEquals("7\ttelescope\t_\tNN\tNN\t_\t0", in.readLine());
            assertEquals("", in.readLine());
            assertEquals(null, in.readLine());
            in.close();
        }
    }

    @Test
    public void testWrite01() throws IOException{
        {
            SampleImpl sample = new SampleImpl(reader_, idconverter_);
            sample.read();
            PrintStream out = new PrintStream(tmpfile_);
            State state = sample.getState();
            state.apply(new ActionImpl(Action.Type.RIGHT));
            state.apply(new ActionImpl(Action.Type.LEFT));

            writer_.write(sample, out);

            out.close();
            BufferedReader in = new BufferedReader(new FileReader(tmpfile_));

            assertEquals("1\tI\t_\tPRP\tPRP\t_\t2", in.readLine());
            assertEquals("2\tsaw\t_\tVBD\tVBD\t_\t0", in.readLine());
            assertEquals("3\ta\t_\tDT\tDT\t_\t2", in.readLine());
            assertEquals("4\tgirl\t_\tNN\tNN\t_\t0", in.readLine());
            assertEquals("5\twith\t_\tIN\tIN\t_\t0", in.readLine());
            assertEquals("6\ta\t_\tDT\tDT\t_\t0", in.readLine());
            assertEquals("7\ttelescope\t_\tNN\tNN\t_\t0", in.readLine());
            assertEquals("", in.readLine());
            assertEquals(null, in.readLine());
            in.close();
        }
    }
}
