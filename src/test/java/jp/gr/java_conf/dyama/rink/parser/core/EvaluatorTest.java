package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.Sentence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EvaluatorTest {

    SentenceImpl gold_ ;
    SentenceImpl system_ ;
    WordImpl.Generator generator_;
    @Before
    public void setUp() throws Exception {
        generator_ = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());
        gold_ = new SentenceImpl(generator_, null);
        system_ = new SentenceImpl(generator_, null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateSentence() {
        Sentence sentence = Evaluator.createSentence();
        assertNotNull(sentence);
    }

    @Test
    public void testEvalute00() {
        Evaluator eval = new Evaluator();
        assertNull(eval.evalute(null, system_));
        assertNull(eval.evalute(gold_, null));
        Evaluator.Results results = eval.evalute(gold_, system_);
        assertNotNull(results);

        assertEquals(null, results.getName());
        assertEquals(0, results.getCorrectPOS());
        assertEquals(0, results.getCorrectDependencies());
        assertEquals(0, results.getDependencies());
        assertEquals(0, results.getWords());
        assertEquals(0, results.getPunctuations());
        assertEquals(0, results.getSentences());
        assertEquals(0, results.getComplete());
        assertEquals(0, results.getCorrectRoot());

    }

    @Test
    public void testEvalute01() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        gold_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        Evaluator.Results results = eval.evalute(gold_, system_);

        assertEquals(null, results.getName());
        assertEquals(5, results.getCorrectPOS());
        assertEquals(4, results.getCorrectDependencies());
        assertEquals(4, results.getDependencies());
        assertEquals(5, results.getWords());
        assertEquals(1, results.getPunctuations());
        assertEquals(1, results.getSentences());
        assertEquals(1, results.getComplete());
        assertEquals(1, results.getCorrectRoot());
    }

    @Test
    public void testEvalute02() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        gold_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));

        Evaluator.Results results = eval.evalute(gold_, system_);
        assertNull(results);
    }

    @Test
    public void testEvalute03() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        gold_.addWord(generator_.generate("Kenn",  0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        Evaluator.Results results = eval.evalute(gold_, system_);
        assertNull(results);
    }

    @Test
    public void testEvalute04() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        gold_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,    -1, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        Evaluator.Results results = eval.evalute(gold_, system_);

        assertEquals(null, results.getName());
        assertEquals(5, results.getCorrectPOS());
        assertEquals(2, results.getCorrectDependencies());
        assertEquals(4, results.getDependencies());
        assertEquals(5, results.getWords());
        assertEquals(1, results.getPunctuations());
        assertEquals(1, results.getSentences());
        assertEquals(0, results.getComplete());
        assertEquals(0, results.getCorrectRoot());
    }

    @Test
    public void testEvalute05() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        gold_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   2, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,    -1, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,     0, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     1, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        Evaluator.Results results = eval.evalute(gold_, system_);

        assertEquals(null, results.getName());
        assertEquals(5, results.getCorrectPOS());
        assertEquals(0, results.getCorrectDependencies());
        assertEquals(4, results.getDependencies());
        assertEquals(5, results.getWords());
        assertEquals(1, results.getPunctuations());
        assertEquals(1, results.getSentences());
        assertEquals(0, results.getComplete());
        assertEquals(0, results.getCorrectRoot());
    }

    @Test
    public void testEvalute06() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        gold_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,  -1, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,    -1, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,    -1, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, -1, null));

        Evaluator.Results results = eval.evalute(gold_, system_);

        assertEquals(null, results.getName());
        assertEquals(5, results.getCorrectPOS());
        assertEquals(0, results.getCorrectDependencies());
        assertEquals(4, results.getDependencies());
        assertEquals(5, results.getWords());
        assertEquals(1, results.getPunctuations());
        assertEquals(1, results.getSentences());
        assertEquals(0, results.getComplete());
        assertEquals(0, results.getCorrectRoot());
    }

    @Test
    public void testEvalute07() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   -1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.COLON,   0, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.COMMA,   0, null));
        gold_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.ODQ,     0, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD,  0, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.CDQ,     0, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.SHARP,   0, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   -1, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,      0, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,      1, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,      0, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD,  0, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD,  0, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD,  0, null));

        Evaluator.Results results = eval.evalute(gold_, system_);

        assertEquals(null, results.getName());
        assertEquals(2, results.getCorrectPOS());
        assertEquals(2, results.getCorrectDependencies());
        assertEquals(2, results.getDependencies());
        assertEquals(7, results.getWords());
        assertEquals(5, results.getPunctuations());
        assertEquals(1, results.getSentences());
        assertEquals(1, results.getComplete());
        assertEquals(1, results.getCorrectRoot());
    }

    @Test
    public void testEvalute08() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.PERIOD, 2, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        gold_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,    -1, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        Evaluator.Results results = eval.evalute(gold_, system_);

        assertEquals(null, results.getName());
        assertEquals(4, results.getCorrectPOS());
        assertEquals(2, results.getCorrectDependencies());
        assertEquals(3, results.getDependencies());
        assertEquals(5, results.getWords());
        assertEquals(2, results.getPunctuations());
        assertEquals(1, results.getSentences());
        assertEquals(0, results.getComplete());
        assertEquals(0, results.getCorrectRoot());
    }

    @Test
    public void testResultsAdd00() {
        Evaluator.Results results0 = new Evaluator.Results("0");
        assertEquals("0",  results0.getName());
        assertEquals(0,    results0.getCorrectPOS());
        assertEquals(0,    results0.getCorrectDependencies());
        assertEquals(0,    results0.getDependencies());
        assertEquals(0,    results0.getWords());
        assertEquals(0,    results0.getPunctuations());
        assertEquals(0,    results0.getSentences());
        assertEquals(0,    results0.getComplete());
        assertEquals(0,    results0.getCorrectRoot());

        Evaluator.Results results1 = new Evaluator.Results(null);

        assertEquals(null, results1.getName());
        assertEquals(0,    results1.getCorrectPOS());
        assertEquals(0,    results1.getCorrectDependencies());
        assertEquals(0,    results1.getDependencies());
        assertEquals(0,    results1.getWords());
        assertEquals(0,    results1.getPunctuations());
        assertEquals(0,    results1.getSentences());
        assertEquals(0,    results1.getComplete());
        assertEquals(0,    results1.getCorrectRoot());

        results0.add(results1);

        assertEquals("0",  results0.getName());
        assertEquals(0,    results0.getCorrectPOS());
        assertEquals(0,    results0.getCorrectDependencies());
        assertEquals(0,    results0.getDependencies());
        assertEquals(0,    results0.getWords());
        assertEquals(0,    results0.getPunctuations());
        assertEquals(0,    results0.getSentences());
        assertEquals(0,    results0.getComplete());
        assertEquals(0,    results0.getCorrectRoot());

    }

    @Test
    public void testResultsAdd01() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        gold_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NNS,   -1, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        Evaluator.Results results0 = new Evaluator.Results("0");
        assertEquals("0",  results0.getName());
        assertEquals(0,    results0.getCorrectPOS());
        assertEquals(0,    results0.getCorrectDependencies());
        assertEquals(0,    results0.getDependencies());
        assertEquals(0,    results0.getWords());
        assertEquals(0,    results0.getPunctuations());
        assertEquals(0,    results0.getSentences());
        assertEquals(0,    results0.getComplete());
        assertEquals(0,    results0.getCorrectRoot());

        Evaluator.Results results1 = eval.evalute(gold_, system_);

        assertEquals(null, results1.getName());
        assertEquals(4,    results1.getCorrectPOS());
        assertEquals(2,    results1.getCorrectDependencies());
        assertEquals(4,    results1.getDependencies());
        assertEquals(5,    results1.getWords());
        assertEquals(1,    results1.getPunctuations());
        assertEquals(1,    results1.getSentences());
        assertEquals(0,    results1.getComplete());
        assertEquals(0,    results1.getCorrectRoot());

        results0.add(results1);

        assertEquals("0",  results0.getName());
        assertEquals(4,    results0.getCorrectPOS());
        assertEquals(2,    results0.getCorrectDependencies());
        assertEquals(4,    results0.getDependencies());
        assertEquals(5,    results0.getWords());
        assertEquals(1,    results0.getPunctuations());
        assertEquals(1,    results0.getSentences());
        assertEquals(0,    results0.getComplete());
        assertEquals(0,    results0.getCorrectRoot());
    }

    @Test
    public void testResultsAdd02() {
        Evaluator eval = new Evaluator();

        gold_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        gold_.addWord(generator_.generate("name", 0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        gold_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        gold_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        system_.addWord(generator_.generate("My",   0, 2, PTB.POS.PRP$,   1, null));
        system_.addWord(generator_.generate("name", 0, 2, PTB.POS.NNS,    2, null));
        system_.addWord(generator_.generate("is",   0, 2, PTB.POS.VB,    -1, null));
        system_.addWord(generator_.generate("Ken",  0, 2, PTB.POS.NN,     2, null));
        system_.addWord(generator_.generate(".",    0, 2, PTB.POS.PERIOD, 2, null));

        Evaluator.Results results0 = new Evaluator.Results("0");
        assertEquals("0",  results0.getName());
        assertEquals(0,    results0.getCorrectPOS());
        assertEquals(0,    results0.getCorrectDependencies());
        assertEquals(0,    results0.getDependencies());
        assertEquals(0,    results0.getWords());
        assertEquals(0,    results0.getPunctuations());
        assertEquals(0,    results0.getSentences());
        assertEquals(0,    results0.getComplete());
        assertEquals(0,    results0.getCorrectRoot());

        Evaluator.Results results1 = eval.evalute(gold_, system_);

        assertEquals(null, results1.getName());
        assertEquals(4,    results1.getCorrectPOS());
        assertEquals(4,    results1.getCorrectDependencies());
        assertEquals(4,    results1.getDependencies());
        assertEquals(5,    results1.getWords());
        assertEquals(1,    results1.getPunctuations());
        assertEquals(1,    results1.getSentences());
        assertEquals(1,    results1.getComplete());
        assertEquals(1,    results1.getCorrectRoot());

        results0.add(results1);

        assertEquals("0",  results0.getName());
        assertEquals(4,    results0.getCorrectPOS());
        assertEquals(4,    results0.getCorrectDependencies());
        assertEquals(4,    results0.getDependencies());
        assertEquals(5,    results0.getWords());
        assertEquals(1,    results0.getPunctuations());
        assertEquals(1,    results0.getSentences());
        assertEquals(1,    results0.getComplete());
        assertEquals(1,    results0.getCorrectRoot());

        results0.add(results1);

        assertEquals("0",  results0.getName());
        assertEquals(8,    results0.getCorrectPOS());
        assertEquals(8,    results0.getCorrectDependencies());
        assertEquals(8,    results0.getDependencies());
        assertEquals(10,    results0.getWords());
        assertEquals(2,    results0.getPunctuations());
        assertEquals(2,    results0.getSentences());
        assertEquals(2,    results0.getComplete());
        assertEquals(2,    results0.getCorrectRoot());

        results0.add(results1);

        assertEquals("0",  results0.getName());
        assertEquals(12,    results0.getCorrectPOS());
        assertEquals(12,    results0.getCorrectDependencies());
        assertEquals(12,    results0.getDependencies());
        assertEquals(15,    results0.getWords());
        assertEquals(3,    results0.getPunctuations());
        assertEquals(3,    results0.getSentences());
        assertEquals(3,    results0.getComplete());
        assertEquals(3,    results0.getCorrectRoot());

        {
            Evaluator.Results[] x = {results0, results1};
            eval.show(x, System.err);
        }
    }

    @Test
    public void testShow00(){
        Evaluator eval = new Evaluator();
        Evaluator.Results[] results = {new Evaluator.Results("0")};
        eval.show(results, System.err);
    }
}
