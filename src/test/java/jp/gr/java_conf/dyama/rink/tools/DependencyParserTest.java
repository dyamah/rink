package jp.gr.java_conf.dyama.rink.tools;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.tools.DependencyParser;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DependencyParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDependencyParser() {
        DependencyParser parser = new DependencyParser();
        assertNull(parser.model_);
        assertNotNull(parser.opts_);
        assertNull(parser.parser_);
        assertNull(parser.reader_);
        assertEquals(0, parser.sentences_);
        assertEquals(0, parser.steps_);
        assertNull(parser.test_);
        assertEquals(1, parser.threads_);
        assertEquals(false, parser.verbose_);
    }

    @Test
    public void testParseCommandLineArguments() {
        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-h", "-i", "samplea/train.txt"};
            try {
                assertEquals(false, parser.parseCommandLineArguments(args));
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-i", "samples/train.txt", "-m", "samples/sample.model"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"samples/train.txt", "-m", "samples/sample.model"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
                fail("");
            } catch (ParseException e) {
                assertEquals("option -i was not found.", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-i", "samples", "-m", "samples/sample.model"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
                fail("");
            } catch (ParseException e) {
                assertEquals("samples is a directory.", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-i", "XxXx", "-m", "samples/sample.model"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
                fail("");
            } catch (ParseException e) {
                assertNotNull("XxXx is not found.");
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-i", "samples/train.txt", "samples/sample.model"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
                fail("");
            } catch (ParseException e) {
                assertNotNull("option -m was not found.");
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-i", "samples/train.txt", "-m", "samples"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
                fail("");
            } catch (ParseException e) {
                assertNotNull("samples is a directory.");
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-i", "samples/train.txt", "-m", "samples/bbbdddd"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
                fail("");
            } catch (ParseException e) {
                assertNotNull("samples/bbbdddd was not found.");
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-i", "samples/train.txt", "-m", "samples/sample.model", "-t", "4"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
                assertEquals(4, parser.threads_);
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-i", "samples/train.txt", "-m", "samples/sample.model", "-t", "4.1"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
                assertEquals(4, parser.threads_);
            } catch (ParseException e) {
                assertNotNull(e);
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

        {
            DependencyParser parser = new DependencyParser();
            String[] args = {"-i", "samples/train.txt", "-m", "samples/sample.model", "-v"};
            try {
                assertEquals(true, parser.parseCommandLineArguments(args));
                assertEquals(true, parser.verbose_);
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }

    }

}
