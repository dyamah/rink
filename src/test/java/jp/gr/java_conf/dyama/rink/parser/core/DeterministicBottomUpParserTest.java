package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.core.ActionEstimator;
import jp.gr.java_conf.dyama.rink.parser.core.ActionLearner;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyRelations;
import jp.gr.java_conf.dyama.rink.parser.core.DeterministicBottomUpParser;
import jp.gr.java_conf.dyama.rink.parser.core.IWPT2003BestFeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.core.OracleActionEstimator;
import jp.gr.java_conf.dyama.rink.parser.core.SVMActionLearner;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImpl;
import jp.gr.java_conf.dyama.rink.parser.core.SimpleSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.State;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.*;

public class DeterministicBottomUpParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateSample() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        FeatureFunction function = new IWPT2003BestFeatureFunction(2, 2);
        ActionEstimator estimator = new OracleActionEstimator(function);
        DeterministicBottomUpParser parser =  new DeterministicBottomUpParser(estimator);

        try {
            parser.createSample(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the sentence reader is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        SimpleSentenceReader reader = new SimpleSentenceReader(new WordImpl.Generator(parser.getIDConverter()));
        reader.addWord("I",          0,  1, PTB.POS.PRP,  1);
        reader.addWord("saw",        2,  5, PTB.POS.VBD, -1);
        reader.addWord("a",          6,  7, PTB.POS.DT,   3);
        reader.addWord("girl",       8, 12, PTB.POS.NNS,  1);
        reader.addWord("with",      13, 17, PTB.POS.IN,   3);
        reader.addWord("a",         18, 19, PTB.POS.CC,   6);
        reader.addWord("telescope", 20, 29, PTB.POS.JJ,   4);
        SampleImpl sample =  (SampleImpl)parser.createSample(reader);
        assertEquals(0, sample.getSentence().size());
        assertEquals(0, sample.getState().size());
        assertEquals(0, sample.getY().size());
        assertEquals(true, sample.read());
        assertEquals(7, sample.getSentence().size());
        assertEquals(7, sample.getState().size());
        assertEquals(7, sample.getY().size());

        Field _parser = SampleImpl.class.getDeclaredField("parser_");
        _parser.setAccessible(true);
        DeterministicBottomUpParser result = (DeterministicBottomUpParser) _parser.get(sample);
        assertEquals(true, parser == result);
    }

    @Test
    public void testParse() throws IOException {
        FeatureFunction function = new IWPT2003BestFeatureFunction(2, 2);
        ActionEstimator estimator = new OracleActionEstimator(function);
        DeterministicBottomUpParser parser =  new DeterministicBottomUpParser(estimator);

        SimpleSentenceReader reader = new SimpleSentenceReader(new WordImpl.Generator(parser.getIDConverter()));
        reader.addWord("I",          0,  1, PTB.POS.PRP,  1);
        reader.addWord("saw",        2,  5, PTB.POS.VBD, -1);
        reader.addWord("a",          6,  7, PTB.POS.DT,   3);
        reader.addWord("girl",       8, 12, PTB.POS.NNS,  1);
        reader.addWord("with",      13, 17, PTB.POS.IN,   3);
        reader.addWord("a",         18, 19, PTB.POS.CC,   6);
        reader.addWord("telescope", 20, 29, PTB.POS.JJ,   4);

        SampleImpl sample = (SampleImpl) parser.createSample(reader);
        State state = sample.getState();
        sample.read();
        assertEquals(7, state.size());
        DependencyRelations deps = state.getDependencies();
        assertEquals(7, deps.size());
        for(int i = 0; i < state.size(); i++){
            for(int j = 0; j < state.size(); j++){
                assertEquals(false, deps.hasDependencyRelation(i, j));
            }
        }

        try {
            parser.parse(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the sample is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        while( parser.parse(sample )){
        }
        assertEquals(1, state.size());
        assertEquals(7, deps.size());

        for(int i = 0; i < state.size(); i++){
            for(int j = 0; j < state.size(); j++){
                if ((i == 1 && j == 0) ||
                        (i == 1 && j == 3) ||
                        (i == 3 && j == 2) ||
                        (i == 3 && j == 4) ||
                        (i == 4 && j == 6) ||
                        (i == 6 && j == 5)
                        ){
                    assertEquals(true, deps.hasDependencyRelation(i, j));
                    continue;
                }

                assertEquals(false, deps.hasDependencyRelation(i, j));
            }
        }

    }

    @Test
    public void testDeterministicBottomUpParserActionEstimatorActionLearner() {
        FeatureFunction function = new IWPT2003BestFeatureFunction(2, 2);
        Parameters params = new Parameters.ParametersImpl();
        ActionLearner learner = new SVMActionLearner(params, function);
        ActionEstimator estimator = new OracleActionEstimator(function);

        try {
            new DeterministicBottomUpParser(estimator, null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the action learner is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        try {
            new DeterministicBottomUpParser(null, learner);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the action estimator is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }
        ActionEstimator x = null;
        try {
            new DeterministicBottomUpParser(x);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the action estimator is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }
    }


    @Test
    public void testSaveLoad() throws IOException, ClassNotFoundException {
        FeatureFunction function = new IWPT2003BestFeatureFunction(2, 2);
        File tmpfile = File.createTempFile("DeterministicBottomUpParserTest", ".tmp");
        {
            Parameters params = new Parameters.ParametersImpl();
            params.setKernelType(Parameters.KernelType.POLYNOMIAL);
            params.setDegree(2);
            params.setGamma(1.0);
            params.setCoef0(1.0);
            ActionEstimator estimator = new OracleActionEstimator(function);
            ActionLearner learner = new SVMActionLearner(params, function);
            DeterministicBottomUpParser parser =  new DeterministicBottomUpParser(estimator, learner);

            SimpleSentenceReader reader = new SimpleSentenceReader(new WordImpl.Generator(parser.getIDConverter()));
            reader.addWord("I",          0,  1, PTB.POS.PRP,  1);
            reader.addWord("saw",        2,  5, PTB.POS.VBD, -1);
            reader.addWord("a",          6,  7, PTB.POS.DT,   3);
            reader.addWord("girl",       8, 12, PTB.POS.NNS,  1);
            reader.addWord("with",      13, 17, PTB.POS.IN,   3);
            reader.addWord("a",         18, 19, PTB.POS.CC,   6);
            reader.addWord("telescope", 20, 29, PTB.POS.JJ,   4);

            SampleImpl sample = (SampleImpl) parser.createSample(reader);
            State state = sample.getState();
            sample.read();
            assertEquals(7, state.size());
            DependencyRelations deps = state.getDependencies();
            assertEquals(7, deps.size());
            for(int i = 0; i < state.size(); i++){
                for(int j = 0; j < state.size(); j++){
                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
            while( parser.parse(sample )){
            }

            try {
                parser.save(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the path is null.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                parser.save("src"); // directory
                fail("");
            } catch (IOException e){

            } catch (Exception e){
                fail("");
            }

            parser.save(tmpfile.getAbsolutePath());
        }

        {
            try {
                DeterministicBottomUpParser.load(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the path is null.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                DeterministicBottomUpParser.load("XXX");
                fail("");
            } catch (FileNotFoundException e){

            } catch (Exception e){
                fail("");
            }

            try {
                DeterministicBottomUpParser.load("src");
                fail("");
            } catch (IOException e){

            } catch (Exception e){
                fail("");
            }

            DeterministicBottomUpParser parser = DeterministicBottomUpParser.load(tmpfile.getAbsolutePath());
            parser.toImmutable();
            SimpleSentenceReader reader = new SimpleSentenceReader(new WordImpl.Generator(parser.getIDConverter()));
            reader.addWord("I",          0,  1, PTB.POS.PRP,  1);
            reader.addWord("saw",        2,  5, PTB.POS.VBD, -1);
            reader.addWord("a",          6,  7, PTB.POS.DT,   3);
            reader.addWord("girl",       8, 12, PTB.POS.NNS,  1);
            reader.addWord("with",      13, 17, PTB.POS.IN,   3);
            reader.addWord("a",         18, 19, PTB.POS.CC,   6);
            reader.addWord("telescope", 20, 29, PTB.POS.JJ,   4);

            SampleImpl sample = (SampleImpl) parser.createSample(reader);
            sample.read();
            State state = sample.getState();
            DependencyRelations deps = state.getDependencies();
            assertEquals(7, state.size());
            assertEquals(7, deps.size());
            for(int i = 0; i < state.size(); i++){
                for(int j = 0; j < state.size(); j++){
                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }

            while( parser.parse(sample )){
            }

            for(int i = 0; i < state.size(); i++){
                for(int j = 0; j < state.size(); j++){
                    if ((i == 1 && j == 0) ||
                            (i == 1 && j == 3) ||
                            (i == 3 && j == 2) ||
                            (i == 3 && j == 4) ||
                            (i == 4 && j == 6) ||
                            (i == 6 && j == 5)
                            ){
                        assertEquals(true, deps.hasDependencyRelation(i, j));
                        continue;
                    }

                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
        }

    }

    @Test
    public void testGetIDConverter() {
        FeatureFunction function = new IWPT2003BestFeatureFunction(2, 2);
        ActionEstimator estimator = new OracleActionEstimator(function);
        DeterministicBottomUpParser parser = new DeterministicBottomUpParser(estimator);
        IDConverter converter = parser.getIDConverter();
        assertNotNull(converter);
        assertEquals(true, (converter instanceof IDConverterImpl.MutableIDConverter));
        parser.toImmutable();
        converter = parser.getIDConverter();
        assertEquals(true, (converter instanceof IDConverterImpl.ImmutableIDConverter));

        parser.toImmutable();
        converter = parser.getIDConverter();
        assertEquals(true, (converter instanceof IDConverterImpl.ImmutableIDConverter));

        converter = parser.getIDConverter();
        assertEquals(true, (converter instanceof IDConverterImpl.ImmutableIDConverter));
    }
}
