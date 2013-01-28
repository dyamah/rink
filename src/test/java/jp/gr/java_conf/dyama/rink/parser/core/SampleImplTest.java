package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.Sentence;
import jp.gr.java_conf.dyama.rink.parser.SentenceReader;
import jp.gr.java_conf.dyama.rink.parser.Word;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyParser;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyRelations;
import jp.gr.java_conf.dyama.rink.parser.core.FeatureImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SimpleSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.State;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SampleImplTest {
    static public class DummyParser extends DependencyParser {

        private IDConverter idconverter_;

        DummyParser() {
            idconverter_ = new IDConverterImpl.MutableIDConverter();
        }

        @Override
        public Sample createSample(SentenceReader reader) {
            SampleImpl sample = new SampleImpl(reader, idconverter_);
            return sample;
        }

        @Override
        boolean parse(SampleImpl sample) {
            int pos = sample.getState().getPosition();
            if (pos < sample.getSentence().size()) {
                sample.getState().setPosition(pos + 1);
                return true;
            }
            return false;
        }

        @Override
        public void save(String path) {
            throw new UnsupportedOperationException("can not save this parser.");
        }

        @Override
        public Sample createSample(SentenceReader reader, int beamWidth) {
            SampleImpl sample = new SampleImpl(reader, idconverter_);
            sample.setAgenda(beamWidth);
            return sample;
        }
    }

    SimpleSentenceReader reader_;
    IDConverterImpl.MutableIDConverter idconverter_;
    DummyParser dummy_parser_;

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

        try {
            dummy_parser_.save(null);
            fail("");
        } catch (UnsupportedOperationException e){
            assertNotNull(e);
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDummyParser(){
        dummy_parser_ = new DummyParser();
        dummy_parser_.createSample(reader_, 3);
    }

    @Test
    public void testSampleImpl00() {

        try {
            new SampleImpl(null, idconverter_);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the sentence reader is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        try {
            new SampleImpl(reader_, null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the ID converter is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        Sentence sentence = sample.getSentence();
        assertEquals(0, sentence.size());
        DependencyRelations deps = sample.getY();
        assertEquals(0, deps.size());
        State state = sample.getState();
        assertEquals(0, state.size());
        assertNotNull(sample.getFeature());
        assertNotNull(sample.getFeatureBuffer());
        assertNotNull(sample.getFeatureVector());

    }

    @Test
    public void testSetParser() {
        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        try {
            sample.setParser(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the parser is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        sample.setParser(dummy_parser_);
    }

    @Test
    public void testRead() throws IOException {
        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        {
            Sentence sentence = sample.getSentence();
            assertEquals(0, sentence.size());
            DependencyRelations deps = sample.getY();
            assertEquals(0, deps.size());
            State state = sample.getState();
            assertEquals(0, state.size());
        }

        assertEquals(true, sample.read());

        {
            Sentence sentence = sample.getSentence();
            assertEquals(7, sentence.size());
            Word w = null;
            w = sentence.getWord(0);
            assertEquals("I", w.getSurface());
            w = sentence.getWord(1);
            assertEquals("saw", w.getSurface());
            w = sentence.getWord(2);
            assertEquals("a", w.getSurface());
            w = sentence.getWord(3);
            assertEquals("girl", w.getSurface());
            w = sentence.getWord(4);
            assertEquals("with", w.getSurface());
            w = sentence.getWord(5);
            assertEquals("a", w.getSurface());
            w = sentence.getWord(6);
            assertEquals("telescope", w.getSurface());

            DependencyRelations y = sample.getY();
            assertEquals(7, y.size());

            Map<Integer, Integer> d = new HashMap<Integer, Integer>();
            for (int i = 0; i < y.size(); i++) {
                for (int j = 0; j < y.size(); j++) {
                    if (y.hasDependencyRelation(i, j))
                        d.put(j, i);
                }
            }
            assertEquals(6, d.size());
            assertEquals(1, (int) d.get(0));
            assertEquals(1, (int) d.get(3));
            assertEquals(3, (int) d.get(2));
            assertEquals(3, (int) d.get(4));
            assertEquals(6, (int) d.get(5));
            assertEquals(4, (int) d.get(6));
            State state = sample.getState();
            assertEquals(7, state.size());
            assertEquals(0, state.getPosition());

            DependencyRelations x = state.getDependencies();
            assertEquals(7, x.size());

            d = new HashMap<Integer, Integer>();
            for (int i = 0; i < x.size(); i++) {
                for (int j = 0; j < x.size(); j++) {
                    if (x.hasDependencyRelation(i, j))
                        d.put(j, i);
                }
            }
            assertEquals(0, d.size());
        }

        assertEquals(false, sample.read());

        {
            Sentence sentence = sample.getSentence();
            assertEquals(0, sentence.size());
            DependencyRelations deps = sample.getY();
            assertEquals(0, deps.size());
            State state = sample.getState();
            assertEquals(0, state.size());
        }
    }

    @Test
    public void testGetY() throws IOException {
        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        DependencyRelations deps0 = sample.getY();
        DependencyRelations deps1 = sample.getY();
        sample.read();
        DependencyRelations deps2 = sample.getY();
        DependencyRelations deps3 = sample.getY();

        assertEquals(true, deps0 == deps1);
        assertEquals(true, deps0 == deps2);
        assertEquals(true, deps0 == deps3);
    }

    @Test
    public void testGetState() throws IOException {
        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        State state0 = sample.getState();
        sample.read();
        State state1 = sample.getState();
        State state2 = sample.getState();
        assertEquals(true, state0 == state1);
        assertEquals(true, state0 == state2);
    }

    @Test
    public void testGetFeatureBuffer() throws IOException {
        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        BinaryFeatureVector.Buffer buf0 = sample.getFeatureBuffer();
        sample.read();
        BinaryFeatureVector.Buffer buf1 = sample.getFeatureBuffer();
        BinaryFeatureVector.Buffer buf2 = sample.getFeatureBuffer();
        assertEquals(true, buf0 == buf1);
        assertEquals(true, buf0 == buf2);
    }

    @Test
    public void testGetFeature() throws IOException {
        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        FeatureImpl f0 = sample.getFeature();
        sample.read();
        FeatureImpl f1 = sample.getFeature();
        FeatureImpl f2 = sample.getFeature();
        assertEquals(true, f0 == f1);
        assertEquals(true, f0 == f2);
    }

    @Test
    public void testParseOneStep0() throws IOException {

        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        assertEquals(false, sample.parseOneStep());
        sample.read();
        assertEquals(false, sample.parseOneStep());
        sample.reparse();
        assertEquals(false, sample.parseOneStep());
        sample.reparse(0);
        assertEquals(false, sample.parseOneStep());
    }

    @Test
    public void testParseOneStep1() throws IOException {

        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        sample.read();
        sample.setParser(dummy_parser_);
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(false, sample.parseOneStep());
        sample.reparse();
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(false, sample.parseOneStep());

        sample.reparse(0);
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(true, sample.parseOneStep());
        assertEquals(false, sample.parseOneStep());

    }

    @Test
    public void testGetNumberOfCorrectDependencies() throws IOException{
        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        assertEquals(0, sample.getNumberOfCorrectDependencies());

        sample.read();
        assertEquals(1, sample.getNumberOfCorrectDependencies());

        DependencyRelations dep = sample.getState().getDependencies();
        dep.depend(1, 0);
        dep.depend(1, 3);
        dep.depend(1, 6);
        assertEquals(3, sample.getNumberOfCorrectDependencies());
    }

    @Test
    public void testAgenda(){
        SampleImpl sample = new SampleImpl(reader_, idconverter_);
        {
            SampleImpl.Agenda agenda = sample.getAgenda();
            assertEquals(null, agenda);
        }

        {
            sample.setAgenda(1);
            SampleImpl.Agenda agenda = sample.getAgenda();
            assertEquals(null, agenda);
        }

        {
            sample.setAgenda(2);
            SampleImpl.Agenda agenda = sample.getAgenda();
            assertNotNull(agenda);
        }



    }

    @Test
    public void testGetSentence() throws IOException {
        SampleImpl sample = new SampleImpl(reader_, idconverter_);

        Sentence sentence0 = sample.getSentence();
        sample.read();
        Sentence sentence1 = sample.getSentence();
        Sentence sentence2 = sample.getSentence();

        assertEquals(true, sentence0 == sentence1);
        assertEquals(true, sentence0 == sentence2);
    }
}