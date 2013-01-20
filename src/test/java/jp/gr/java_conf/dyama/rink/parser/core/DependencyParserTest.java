package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.core.ActionEstimator;
import jp.gr.java_conf.dyama.rink.parser.core.ActionLearner;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyParser;
import jp.gr.java_conf.dyama.rink.parser.core.DeterministicBottomUpParser;
import jp.gr.java_conf.dyama.rink.parser.core.SVMActionEstimator;
import jp.gr.java_conf.dyama.rink.parser.core.SVMActionLearner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DependencyParserTest {
    File train_ = new File("samples/train.txt");
    File model_ = new File("samples/sample.model");
    File gmodel_ = new File("samples/sample.g.model");

    @Before
    public void setUp() throws Exception {
        Parameters params = new Parameters.ParametersImpl();
        params.setKernelType(Parameters.KernelType.POLYNOMIAL);
        params.setDegree(2);
        params.setGamma(1.0);
        params.setCoef0(1.0);
        {
            DependencyParser parser = DependencyParser.Builder.buildSVMDependencyLearner(params);

            AnnotatedSentenceReader reader = new AnnotatedSentenceReader(train_.getPath(), AnnotatedSentenceReader.AnnotationLevel.DEPENDENCY);

            Sample sample = parser.createSample(reader);
            while(sample.read())
                while(sample.parseOneStep());
            parser.save(model_.getPath());
        }

        {
            DependencyParser parser = DependencyParser.Builder.buildPOSGroupingSVMDependencyLearner(params);

            AnnotatedSentenceReader reader = new AnnotatedSentenceReader(train_.getPath(), AnnotatedSentenceReader.AnnotationLevel.DEPENDENCY);

            Sample sample = parser.createSample(reader);
            while(sample.read())
                while(sample.parseOneStep());
            parser.save(gmodel_.getPath());
        }

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoadVocaburaly(){
        // TODO implementation
    }

    @Test
    public void testBuild(){
        try {
            DependencyParser.Builder.build(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the path is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        try {
            DependencyParser.Builder.build("XXX");
            fail("");
        } catch (FileNotFoundException e) {
            assertNotNull(e);
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        try {
            DependencyParser.Builder.build(train_.getPath());
            fail("");
        } catch (IOException e) {
            assertNotNull(e);
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        DeterministicBottomUpParser parser = null;
        try {
            parser = (DeterministicBottomUpParser) DependencyParser.Builder.build(model_.getPath());
            Field learner = DeterministicBottomUpParser.class.getDeclaredField("learner_");
            learner.setAccessible(true);
            Field estimator = DeterministicBottomUpParser.class.getDeclaredField("estimator_");
            estimator.setAccessible(true);
            assertEquals(null, learner.get(parser));
            ActionEstimator est = (ActionEstimator) estimator.get(parser);
            assertEquals(true, (est instanceof SVMActionEstimator));
            IDConverter idconv = parser.getIDConverter();
            assertEquals(true, (idconv instanceof IDConverterImpl.ImmutableIDConverter));
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        parser = null;
        try {
            parser = (DeterministicBottomUpParser) DependencyParser.Builder.build(gmodel_.getPath());
            Field learner = DeterministicBottomUpParser.class.getDeclaredField("learner_");
            learner.setAccessible(true);
            Field estimator = DeterministicBottomUpParser.class.getDeclaredField("estimator_");
            estimator.setAccessible(true);
            assertEquals(null, learner.get(parser));
            ActionEstimator est = (ActionEstimator) estimator.get(parser);
            assertEquals(true, (est instanceof SVMActionEstimator));
            IDConverter idconv = parser.getIDConverter();
            assertEquals(true, (idconv instanceof IDConverterImpl.ImmutableIDConverter));
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

    }

    @Test
    public void testBuildLeraner() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{

        Field learner = DeterministicBottomUpParser.class.getDeclaredField("learner_");
        learner.setAccessible(true);

        Parameters params = new Parameters.ParametersImpl();

        try {
            DependencyParser.Builder.buildSVMDependencyLearner(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the parameters is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        {
            DeterministicBottomUpParser parser = (DeterministicBottomUpParser) DependencyParser.Builder.buildSVMDependencyLearner(params);
            ActionLearner al = (ActionLearner) learner.get(parser);
            assertEquals(true, (al instanceof SVMActionLearner));
        }



        try {
            DependencyParser.Builder.buildPOSGroupingSVMDependencyLearner(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the parameters is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        {
            DeterministicBottomUpParser parser = (DeterministicBottomUpParser)DependencyParser.Builder.buildPOSGroupingSVMDependencyLearner(params);
            ActionLearner al = (ActionLearner) learner.get(parser);
            assertEquals(true, (al instanceof SVMActionLearner));
        }


    }

}
