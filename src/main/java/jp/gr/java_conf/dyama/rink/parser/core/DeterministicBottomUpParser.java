package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.SentenceReader;

/**
 * Deterministic Bottom-Up Dependency Parser
 * @author Hiroyasu Yamada
 *
 */
public class DeterministicBottomUpParser extends DependencyParser {

    /** The action estimator */
    private ActionEstimator estimator_;

    /** The action learner */
    private ActionLearner learner_;

    DeterministicBottomUpParser(IDConverter converter, ActionEstimator estimator){
        super(converter);
        assert(converter != null);
        assert(estimator != null);
        estimator_ = estimator ;
        learner_   = null;
    }

//    DeterministicBottomUpParser(IDConverter converter, ActionEstimator estimator, ActionLearner learner){
//        super(converter);
//        assert(converter != null);
//        assert(estimator != null);
//        assert(learner != null);
//        estimator_ = estimator ;
//        learner_   = learner;
//    }

    /**
     * @param estimator the action estimator.
     * @param learner the action learner.
     * @throws IllegalArgumentException if the action estimator is null.
     * @throws IllegalArgumentException if the action learner is null.
     */
    DeterministicBottomUpParser(ActionEstimator estimator, ActionLearner learner){
        super();
        if (estimator == null)
            throw new IllegalArgumentException("the action estimator is null.");
        if (learner == null)
            throw new IllegalArgumentException("the action learner is null.");
        estimator_ = estimator;
        learner_   = learner;
    }

    /**
     * @param estimator the action estimator.
     * @throw IllegalArgumentException if the action estimator is null.
     */
    DeterministicBottomUpParser(ActionEstimator estimator){
        super();
        if (estimator == null)
            throw new IllegalArgumentException("the action estimator is null.");
        estimator_ = estimator;
        learner_   = null;
    }

    /**
     * Loads the parser from a file.
     * @param path the file path.
     * @return dependency parser.
     * @throws IllegalArgumentException if the file path is null.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     *
     */
    public static DeterministicBottomUpParser load(String path) throws FileNotFoundException, IOException, ClassNotFoundException {
        if (path == null)
            throw new IllegalArgumentException("the path is null.");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
        IDConverter converter = (IDConverter) in.readObject();
        ActionEstimator estimator = (ActionEstimator) in.readObject();
        DeterministicBottomUpParser parser = new DeterministicBottomUpParser(converter, estimator);
        in.close();
        return parser;
    }

    @Override
    public void save(String path) throws IOException {
        if (path == null)
            throw new IllegalArgumentException("the path is null.");
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
        ActionEstimator est = estimator_ ;
        if (learner_ != null)
            est = learner_.learn();

        out.writeObject(getIDConverter());
        out.writeObject(est);
        out.close();
    }

    @Override
    boolean parse(SampleImpl sample){
        if (sample == null)
            throw new IllegalArgumentException("the sample is null.");

        State state = sample.getState();
        if (state.isComplete()){
            return false;
        }

        if (state.isEOS())
            state.setPosition(0);

        Action action = estimator_.estimate(sample);
        if (learner_ != null)
            learner_.addExample(sample, action);

        if (state.apply(action))
            return true ;

        return false;
    }

    @Override
    public Sample createSample(SentenceReader reader) {
        if (reader == null)
            throw new IllegalArgumentException("the sentence reader is null.");
        SampleImpl sample = new SampleImpl(reader, getIDConverter());
        sample.setParser(this);
        return  sample;
    }

    @Override
    public Sample createSample(SentenceReader reader, int beamWidth) {
        if (reader == null)
            throw new IllegalArgumentException("the sentence reader is null.");
        SampleImpl sample = new SampleImpl(reader, getIDConverter());
        sample.setParser(this);
        sample.setAgenda(beamWidth);
        return  sample;

    }
}
