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

public class OriginalSampleWriterTest {
    SimpleSentenceReader reader_;
    IDConverterImpl.MutableIDConverter idconverter_;
    DummyParser dummy_parser_;

    File tmpfile_;
    OriginalSampleWriter writer_;
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

        tmpfile_ = File.createTempFile("OriginalSampleWriterTest", ".tmp");
        writer_ = new OriginalSampleWriter();
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

            assertEquals("I\tPRP\t1\t-7", in.readLine());
            assertEquals("saw\tVBD\t-1\t-7", in.readLine());
            assertEquals("a\tDT\t3\t-7", in.readLine());
            assertEquals("girl\tNN\t1\t-7", in.readLine());
            assertEquals("with\tIN\t3\t-7", in.readLine());
            assertEquals("a\tDT\t6\t-7", in.readLine());
            assertEquals("telescope\tNN\t4\t-7", in.readLine());
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

            assertEquals("I\tPRP\t1\t1", in.readLine());
            assertEquals("saw\tVBD\t-1\t-5", in.readLine());
            assertEquals("a\tDT\t3\t1", in.readLine());
            assertEquals("girl\tNN\t1\t-5", in.readLine());
            assertEquals("with\tIN\t3\t-5", in.readLine());
            assertEquals("a\tDT\t6\t-5", in.readLine());
            assertEquals("telescope\tNN\t4\t-5", in.readLine());
            assertEquals("", in.readLine());
            assertEquals(null, in.readLine());
            in.close();
        }
    }
}
